package com.example.polary.Photo

import SwipeGestureDetector
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.polary.BaseActivity
import com.example.polary.Class.FocusCircleView
import com.example.polary.Class.NotificationService
import com.example.polary.PostView.PostActivity
import com.example.polary.Profile.ProfileActivity
import com.example.polary.R
import com.example.polary.dataClass.User
import com.example.polary.friends.FriendsActivity
import com.example.polary.`object`.FriendRequestsData
import com.example.polary.utils.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhotoActivity : BaseActivity() {
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)android.Manifest.permission.POST_NOTIFICATIONS else android.Manifest.permission.POST_NOTIFICATIONS)
    private val REQUEST_CODE_PERMISSIONS = 10
    private var imageCapture: ImageCapture? = null
    private lateinit var camera: Camera
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var preview: Preview? = null
    private lateinit var scaleGestureDetector : ScaleGestureDetector
    private lateinit var gestureDetector: SwipeGestureDetector
    private lateinit var user: User
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            var notificationGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false) {
                    when(it.key) {
                        android.Manifest.permission.CAMERA -> {
                            Log.d("Permission", "Camera permission denied")
                            permissionGranted = false
                        }
                        android.Manifest.permission.POST_NOTIFICATIONS -> {
                            Log.d("Permission", "Notification permission denied")
                            notificationGranted = false
                        }
                        android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            Log.d("Permission", "Read external storage permission denied")
                            permissionGranted = false
                        }
                    }
                }
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
            if (!notificationGranted) {
                Toast.makeText(baseContext,
                    "Notification permission denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                getFCMToken()
                NotificationService.createNotificationChannel(this)
            }
        }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
        findViewById<MaterialCardView>(R.id.cnt_friends_bubble).visibility = View.GONE

        // Force hide the action bar
        supportActionBar?.hide()

        // Request permissions for camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        // Get the user ID from the SharedPreferences
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sessionManager = SessionManager(sharedPreferences)
        user = sessionManager.getUserFromSharedPreferences()!!
        FriendRequestsData.getFriendRequestsOfReceiver(user.id, "TakePhotoActivity") {
            val cnt = FriendRequestsData.cntFriendRequestsOfReceiver()
            Log.i("TakePhotoActivity", "cnt: $cnt")
            if (cnt > 0) {
                findViewById<MaterialCardView>(R.id.cnt_friends_bubble).visibility = View.VISIBLE
                findViewById<TextView>(R.id.cnt_friends).text = cnt.toString()
            }
        }
        val flashBtn = findViewById<MaterialButton>(R.id.btn_flash)
        flashBtn.setOnClickListener {
            val imageCapture = imageCapture ?: return@setOnClickListener
            flashMode = imageCapture.flashMode
            imageCapture.flashMode = if (flashMode == ImageCapture.FLASH_MODE_ON) {
                ImageCapture.FLASH_MODE_OFF
            } else {
                ImageCapture.FLASH_MODE_ON
            }
            flashBtn.icon = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                getDrawable(R.drawable.ic_flash)
            } else {
                getDrawable(R.drawable.ic_flash_off)
            }
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

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val zoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                val newZoomRatio = zoomRatio * detector.scaleFactor
                camera.cameraControl.setLinearZoom(newZoomRatio)
                return true
            }
        })
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        checkNotificationIsEnabled()
        FriendRequestsData.getFriendRequestsOfReceiver(user.id, "TakePhotoActivity") {
            val cnt = FriendRequestsData.cntFriendRequestsOfReceiver()
            Log.i("TakePhotoActivity", "cnt: $cnt")
            if (cnt > 0) {
                findViewById<MaterialCardView>(R.id.cnt_friends_bubble).visibility = View.VISIBLE
                findViewById<TextView>(R.id.cnt_friends).text = cnt.toString()
            }
        }
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

        // Create output file in internal storage
        val outputFile = File(cacheDir, name)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(outputFile)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
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
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
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

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val previewView = findViewById<androidx.camera.view.PreviewView>(R.id.previewView)
            val focusCircleView = findViewById<FocusCircleView>(R.id.focusCircleView)
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
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                // Set a touch listener on the previewView
                previewView.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            // Focus
                            val factory = SurfaceOrientedMeteringPointFactory(
                                previewView.width.toFloat(), previewView.height.toFloat()
                            )
                            val point = factory.createPoint(event.x, event.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            camera.cameraControl.startFocusAndMetering(action)
                            focusCircleView.focusAt(event.x, event.y)
                        }
                        MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                            // Pass the event to the ScaleGestureDetector in all cases
                            scaleGestureDetector.onTouchEvent(event)
                        }
                    }
                    true
                }
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
    private fun checkNotificationIsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                getFCMToken()
                NotificationService.createNotificationChannel(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}