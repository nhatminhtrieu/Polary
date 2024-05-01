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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.Class.HttpMethod
import com.example.polary.PostView.VisibilityAdapter
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.Group
import com.example.polary.`object`.FriendsData
import com.example.polary.`object`.GlobalResources
import com.example.polary.`object`.GroupsData
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SendPhotoActivity: AppCompatActivity(),
    AddCaptionFragment.OnInputListener,
    FrameButtonsFragment.OnFrameChangeListener,
    FontButtonsFragment.OnFontChangeListener{
    private lateinit var imageName: String
    private lateinit var imageFile: File
    private lateinit var imageUri: String
    private lateinit var postCaption : TextView
    private var font = 0
    private var frame = 0
    private lateinit var user : User
    private val friends = mutableListOf<Friend>()
    private val groups = mutableListOf<Group>()
    private val selectedFriends = mutableListOf<Int>()
    private val selectedGroups = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)

        // Get the user ID from the SharedPreferences
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!

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
            if (selectedFriends.isEmpty()) {
                Toast.makeText(this, "Please select at least one friend", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else createPost(user.id, postCaption.text.toString(), frame, font, selectedFriends)
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
        FriendsData.getFriends(user.id, "SendPhotoActivity") { friends ->
            if (friends != null) {
                this.friends.addAll(friends)
                selectedFriends.addAll(friends.map { it.id })
                GroupsData.getGroupsWithMembers(user.id, "SendPhotoActivity") { groups ->
                    if (groups != null) {
                        this.groups.addAll(groups)
                        renderFriends()
                    }
                }
            }
        }
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

    private fun openFragment() {
        val addCaptionFragment = AddCaptionFragment(postCaption.text.toString())
        addCaptionFragment.show(supportFragmentManager, "AddCaptionFragment")
    }
    override fun sendInput(input: String) {
        postCaption.text = input
    }
    private fun createPost(userId: Int, caption: String, frame: Int, font: Int, visibleToIds: List<Int>) {
        // Show a ProgressDialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Creating post...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        lifecycleScope.launch {
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
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerVisibilities)
        val visibilityAdapter = VisibilityAdapter(friends, groups, selectedFriends, selectedGroups)
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