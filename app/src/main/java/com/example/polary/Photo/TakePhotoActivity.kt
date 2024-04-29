package com.example.polary.Photo

import SwipeGestureDetector
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.polary.Class.NotificationService
import com.example.polary.BaseActivity
import com.example.polary.PostView.PostActivity
import com.example.polary.Profile.ProfileActivity
import com.example.polary.R
import com.example.polary.utils.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.example.polary.friends.FriendsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhotoActivity : BaseActivity() {
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)android.Manifest.permission.POST_NOTIFICATIONS else TODO())
    private val REQUEST_CODE_PERMISSIONS = 10
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    // Camera front or back
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    // Preview
    private var preview: Preview? = null
    private lateinit var gestureDetector: SwipeGestureDetector
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
                getFCMToken()
                NotificationService.createNotificationChannel(this)
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        // Force hide the action bar
        supportActionBar?.hide()

        // Request permissions for camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        findViewById<MaterialButton>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        findViewById<MaterialCardView>(R.id.btn_history_posts).setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        findViewById<MaterialButton>(R.id.btn_take_photo).setOnClickListener {
            takePhoto()
        }
        findViewById<MaterialButton>(R.id.btn_flip_camera).setOnClickListener {
            changeCamera()
        }
        findViewById<MaterialButton>(R.id.btn_image_gallery).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PERMISSIONS)
        }
        findViewById<MaterialButton>(R.id.btn_friends).setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }

        gestureDetector = object : SwipeGestureDetector(this) {
            override fun onSwipeRight() {
                val intent = Intent(this@TakePhotoActivity, ProfileActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            override fun onSwipeLeft() {
                val intent = Intent(this@TakePhotoActivity, PostActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PERMISSIONS && resultCode == RESULT_OK) {
            val uri = data?.data
            val intent = Intent(this, SendPhotoActivity::class.java).apply {
                putExtra("imageUri", uri.toString())
            }
            startActivity(intent)
        }
    }
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Polary")
        }

        // Create output file in internal storage
        val outputFile = File(cacheDir, name)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(outputFile)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext,
                        "Photo capture failed: ${exc.message}",
                        Toast.LENGTH_SHORT).show()
                    Log.e("PolaryApp", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){

                    val msg = "Photo capture succeeded"
                    Log.d("PolaryApp", msg)
                    // Start SendPhotoActivity and pass the image name as an extra
                    val intent = Intent(this@TakePhotoActivity, SendPhotoActivity::class.java).apply {
                        putExtra("imageName", name)
                    }
                    startActivity(intent)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val previewView = findViewById<androidx.camera.view.PreviewView>(R.id.previewView)
            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e("PolaryApp", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
        imageCapture = ImageCapture.Builder().build()
    }

    private fun changeCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        val previewView = findViewById<androidx.camera.view.PreviewView>(R.id.previewView)

        // Create an animator set so we can play the animations together
        val animatorSet = AnimatorSet()

        // Create the first half of the flip animation
        val firstHalfFlip = ObjectAnimator.ofFloat(previewView, View.SCALE_X, 1.0f, 0.0f)
        firstHalfFlip.duration = 250
        firstHalfFlip.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                // Restart the camera at the half-way point of the animation
                startCamera()
            }
        })

        // Create the second half of the flip animation
        val secondHalfFlip = ObjectAnimator.ofFloat(previewView, View.SCALE_X, 0.0f, 1.0f)
        secondHalfFlip.duration = 250

        // Play the animations together
        animatorSet.playSequentially(firstHalfFlip, secondHalfFlip)
        animatorSet.start()
    }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("TakePhotoActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            val user = SessionManager(getSharedPreferences("user", MODE_PRIVATE)).getUserFromSharedPreferences()!!
            NotificationService().sendRegistrationToServer(token!!, user.id.toString())
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}