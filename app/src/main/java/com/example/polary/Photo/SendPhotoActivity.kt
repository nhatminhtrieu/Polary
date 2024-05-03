package com.example.polary.Photo

import AddCaptionFragment
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.BaseActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.PostView.VisibilityAdapter
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.Visibility
import com.example.polary.`object`.GlobalResources
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import kotlin.properties.Delegates

class SendPhotoActivity: BaseActivity(),
    AddCaptionFragment.OnInputListener,
    FrameButtonsFragment.OnFrameChangeListener,
    FontButtonsFragment.OnFontChangeListener{
    private lateinit var imageName: String
    private lateinit var imageFile: File
    private lateinit var imageUri: String
    private lateinit var visibilityAdapter: VisibilityAdapter
    private lateinit var postCaption : TextView
    private var font = 0
    private var frame = 0
    private var userId by Delegates.notNull<Int>()
    private val visibleToIds = mutableListOf<Int>()
    private var isAll = true
    private var friends: ArrayList<Visibility> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)

        // Get the user ID from the SharedPreferences
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        val user = sessionManager.getUserFromSharedPreferences()!!
        userId = user.id

        // Get the image name from the intent extras
        imageName = intent.getStringExtra("imageName").toString()
        imageUri = intent.getStringExtra("imageUri").toString()
        if (imageUri != "null") {
            loadImageFromDevice(imageUri)
        } else {
            loadImageFromCache(imageName)
        }

        findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
        findViewById<MaterialButton>(R.id.btn_send).setOnClickListener {
            createPost(imageFile, userId, postCaption.text.toString(), frame, font, visibleToIds)
        }
        findViewById<MaterialButton>(R.id.btn_save_image).setOnClickListener {
            if (imageUri != "null") {
                Toast.makeText(this, "Image is already in your device", Toast.LENGTH_SHORT).show()
            } else {
                saveImage(imageName, imageFile)
            }
        }

        findViewById<MaterialButton>(R.id.btn_change_frame).setOnClickListener {
            val frameFragment = FrameButtonsFragment().apply {
                arguments = Bundle().apply {
                    putInt("frame", frame)
                }
                setOnFrameChangeListener(this@SendPhotoActivity)
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.decorator_buttons_fragment, frameFragment)
            transaction.commit()
        }

        findViewById<MaterialButton>(R.id.btn_change_font).setOnClickListener {
            val fontFragment = FontButtonsFragment().apply {
                arguments = Bundle().apply {
                    putInt("font", font)
                }
                setOnFontChangeListener(this@SendPhotoActivity)
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.decorator_buttons_fragment, fontFragment)
            transaction.commit()
        }

        // Post caption
        postCaption = findViewById(R.id.post_caption)
        postCaption.text = ""
        val btnAddCaption = findViewById<MaterialButton>(R.id.btn_add_caption)
        btnAddCaption.bringToFront()
        val color = Color.parseColor("#80000000") // Black with 50% transparency
        btnAddCaption.backgroundTintList = ColorStateList.valueOf(color)
        btnAddCaption.setOnClickListener {
            openFragment()
        }
        postCaption.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    btnAddCaption.visibility = View.GONE
                } else {
                    btnAddCaption.visibility = View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }
        })
        postCaption.setOnClickListener {
            openFragment()
        }
        getFriends()
    }

    override fun onFrameChanged(frame: Int) {
        this.frame = frame
        Log.d("SendPhotoActivity", "Frame changed to: ${this.frame}")
        val postFrame = findViewById<ImageView>(R.id.post_frame)
        Glide.with(this@SendPhotoActivity).load(GlobalResources.frames[this.frame]).into(postFrame)
    }

    override fun onFontChanged(font: Int) {
        this.font = font
        Log.d("SendPhotoActivity", "Font changed to: ${this.font}")
    }

    private fun getFriends() {
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
    private fun openFragment() {
        val addCaptionFragment = AddCaptionFragment(postCaption.text.toString())
        addCaptionFragment.show(supportFragmentManager, "AddCaptionFragment")
    }
    override fun sendInput(input: String) {
        // Use the input value here
        postCaption.text = input
    }
    private fun createPost(imageName: File?, userId: Int, caption: String, frame: Int, font: Int, visibleToIds: List<Int>) {
        // Show a ProgressDialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Creating post...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        lifecycleScope.launch {
            Log.i("SendPhotoActivity", "Creating post: $imageName")
            val httpMethod = HttpMethod()

            // Create a request body with the file and content type
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile.readBytes())

            // Create a multipart request body with the file data
            val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name + ".jpeg", requestBody)
            val authorBody = MultipartBody.Part.createFormData("authorId", userId.toString())
            val captionBody = MultipartBody.Part.createFormData("caption", caption)
            val frameBody = MultipartBody.Part.createFormData("frame", frame.toString())
            val fontBody = MultipartBody.Part.createFormData("font", font.toString())
            val visibleToIdsBodies = visibleToIds.map { id ->
                MultipartBody.Part.createFormData("visibleToIds[]", id.toString())
            }
            Log.d("SendPhotoActivity", "frame: $frame, font: $font")
            httpMethod.doPostMultiPart("/posts/created-posts", multipartBody, authorBody, captionBody, frameBody, fontBody, visibleToIdsBodies, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Log.d("SendPhotoActivity", "Successfully posted image: $data")
                    Toast.makeText(this@SendPhotoActivity, "Image posted!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss() // Dismiss the ProgressDialog
                    finish()
                }

                override fun onError(error: Throwable) {
                    Log.e("SendPhotoActivity", "Failed to post image: $error")
                    Toast.makeText(this@SendPhotoActivity, "Failed to post image!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss() // Dismiss the ProgressDialog
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
    }

    private fun loadImageFromCache(imageName: String?) {
        if (imageName === null || !File(cacheDir, imageName).exists()) {
            // Handle error
            Toast.makeText(this, "Error: Image not found", Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            imageFile = File(cacheDir, imageName)
            // Display the image
            val imageView = findViewById<ImageView>(R.id.post_image)
            Glide.with(this)
                .load(imageFile.absolutePath)
                .into(imageView)
        }
    }

    private fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor?.moveToFirst() == true) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor?.close()
        return res
    }
    private fun loadImageFromDevice(imageUri: String?) {
        if (imageUri === null) {
            // Handle error
            Toast.makeText(this, "Error: Image not found", Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            // Display the image
            val imageView = findViewById<ImageView>(R.id.post_image)
            val imagePath = getPathFromURI(Uri.parse(imageUri))
            imageFile = imagePath?.let { File(it) }!!
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }
    private fun saveImage(imageName: String?, imageFile: File) {
        Log.i("SendPhotoActivity", "Saving image: $imageName")
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