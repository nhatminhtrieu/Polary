package com.example.polary.Photo

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.Class.HttpMethod
import com.example.polary.PostView.VisibilityAdapter
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.Visibility
import com.example.polary.utils.ApiCallBack
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

data class CreatePostRequest (
    val authorId: Int,
    val caption: String,
    val visibleToIds: List<Int>
)
class SendPhotoActivity: AppCompatActivity() {
    private lateinit var imageName: String
    private lateinit var imageFile: File
    private lateinit var visibilityAdapter: VisibilityAdapter
    private val visibleToIds = mutableListOf<Int>()
    private val userId = 1
    private var isAll = true

    private var friends: ArrayList<Visibility> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)

        // Get the image name from the intent extras
        imageName = intent.getStringExtra("imageName").toString()
        loadImage(imageName)

        findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
        findViewById<MaterialButton>(R.id.btn_send).setOnClickListener {
            createPost(imageFile, userId, "Test", visibleToIds)
        }
        val httpMethod = HttpMethod()
        httpMethod.doGet<Friend>("users/$userId/friends", object : ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                Log.d("SendPhotoActivity", "Successfully fetched friends: $data")
                val friendsResponse = data as ArrayList<Friend>
                friends = friendsResponse.map { Visibility(it.id, it.username, it.avatar) } as ArrayList<Visibility>
                renderFriends()
            }

            override fun onError(error: Throwable) {
                Log.e("SendPhotoActivity", "Failed to fetch friends: $error")
            }
        })
    }

    private fun createPost(imageName: File?, userId: Int, caption: String, visibleToIds: List<Int>) {
        lifecycleScope.launch {
            Log.i("SendPhotoActivity", "Creating post: $imageName")
            val httpMethod = HttpMethod()
    
            // Create a request body with the file and content type
            val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile.readBytes())
    
            // Create a multipart request body with the file data
            val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name + ".jpeg", requestBody)
            val authorBody = MultipartBody.Part.createFormData("authorId", userId.toString())
            val captionBody = MultipartBody.Part.createFormData("caption", caption)
            val visibleToIdsBodies = visibleToIds.map { id ->
                MultipartBody.Part.createFormData("visibleToIds[]", id.toString())
            }
            httpMethod.doPostMultiPart("/posts/created-posts", multipartBody, authorBody, captionBody, visibleToIdsBodies, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Log.d("SendPhotoActivity", "Successfully posted image: $data")
                    Toast.makeText(this@SendPhotoActivity, "Image posted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
    
                override fun onError(error: Throwable) {
                    Log.e("SendPhotoActivity", "Failed to post image: $error")
                    Toast.makeText(this@SendPhotoActivity, "Failed to post image!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun renderFriends() {
        visibleToIds.addAll(friends.map { it.id })
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerVisibilities)
        visibilityAdapter = VisibilityAdapter(friends, object : AdapterBehavior {
            override fun onFriendClick(id: Int, on: Boolean) {
                if (isAll) {
                    isAll = false
                    visibleToIds.clear()
                }
                // Handle item click here
                if (on) visibleToIds.add(id)
                else visibleToIds.remove(id)
                Log.i("SendPhotoActivity", "Visible to: $visibleToIds")
            }

            override fun onAllClick(on: Boolean) {
                if (on) {
                    isAll = true
                    visibleToIds.clear()
                    visibleToIds.addAll(friends.map { it.id }.filter { it !in visibleToIds })
                }
                else visibleToIds.clear()
                Log.i("SendPhotoActivity", "Visible to: $visibleToIds")
            }
        })
        recyclerView.adapter = visibilityAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@SendPhotoActivity, RecyclerView.HORIZONTAL, false)

        findViewById<MaterialButton>(R.id.btn_save_image).setOnClickListener {
            saveImage(imageName, imageFile)
        }
    }

    private fun loadImage(imageName: String?) {
        if (imageName === null || !File(cacheDir, imageName).exists()) {
            // Handle error
            Toast.makeText(this, "Error: Image not found", Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            imageFile = File(cacheDir, imageName)
            // Display the image
            val imageView = findViewById<ImageView>(R.id.capturedPhoto)
            Glide.with(this)
                .load(imageFile.absolutePath)
                .into(imageView)
        }
    }
    private fun saveImage(imageName: String?, imageFile: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Polary")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        contentResolver.openOutputStream(uri!!).use { outputStream ->
            imageFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream!!)
            }
        }

        Log.i("SendPhotoActivity", "Image saved to: $uri")
        cacheDir.deleteRecursively()
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
    }
}