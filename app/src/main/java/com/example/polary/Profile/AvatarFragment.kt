package com.example.polary.Profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.utils.SessionManager
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AvatarFragment() : BottomSheetDialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private var imageFile: File? = null
    private lateinit var imageUri: String
    val REQUEST_CODE_PERMISSIONS = 101
    private var listener: OnAvatarFragmentListener? = null

    interface OnAvatarFragmentListener {
        fun onAvatarUpdated()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAvatarFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnAvatarUpdateListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_avatar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        view.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<CardView>(R.id.btn_change_avatar).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PERMISSIONS)
        }
        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            if (imageFile != null) {
                updateAvatar()
            }
            else {
                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PERMISSIONS && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data.toString()
            loadImageFromDevice()
        }
    }

    private fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(contentUri, proj, null, null, null)
        if (cursor?.moveToFirst() == true) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor?.close()
        return res
    }
    private fun loadImageFromDevice() {
        // Display the image
        val imageView = view?.findViewById<ImageView>(R.id.new_avatar)
        val imagePath = getPathFromURI(Uri.parse(imageUri))
        imageFile = imagePath?.let { File(it) }!!
        if (imageView != null) {
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }

    private fun updateAvatar() {
        // Show a ProgressDialog
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Updating avatar...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (imageFile == null) {
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            val httpMethod = HttpMethod()
            val requestBody =
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile!!.readBytes())

            // Create a multipart request body with the file data
            val multipartBody =
                MultipartBody.Part.createFormData("file", imageFile!!.name + ".jpeg", requestBody)
            httpMethod.doPutMultipart("/users/${user.id}/avatar", multipartBody, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    Toast.makeText(context, "New avatar updated!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss() // Dismiss the ProgressDialog
                    listener?.onAvatarUpdated()
                    dismiss()
                }

                override fun onError(error: Throwable) {
                    Log.e("SendPhotoActivity", "Failed to post image: $error")
                    Toast.makeText(context, "Failed to update avatar!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss() // Dismiss the ProgressDialog
                }
            })
        }
    }
    companion object {
        val TAG = "AvatarFragment"
    }

}