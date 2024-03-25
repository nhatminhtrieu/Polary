package com.example.polary

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.Glide
import com.example.polary.ui.theme.PolaryTheme
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : ComponentActivity() {
    private lateinit var imagePost: ImageView
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePost = findViewById(R.id.post_image)

        getImage()
    }

    private fun getImage() {
        val imageUrl = "https://storage.googleapis.com/polary-8862a.appspot.com/1711355101396_Simerenya.jpeg?GoogleAccessId=firebase-adminsdk-rqg42%40polary-8862a.iam.gserviceaccount.com&Expires=16446992400&Signature=Ey4yPi5uUlAOWyvBdnelslkI9ywOFfBrcZIVhh6NR61daz4NNZDt250hqbZ5VHlGBCyRjSZsAkR6yrouVorhCPi0wOy5CBTnxXRuw%2Bvff8Ipy%2Bvt2GfE9T4T1B03JlGzWHt8skSvIhFpikQVCl%2BZm2F4CvZ3Jc8H6NpI1Dbcim8wunTYHg%2FIatY1FKI77ILYzZPC2oFXqgDMU3%2FekGUY2P7dH%2B9go9VMOwg2vH4PnXCCDmsqEgv9YNMXp0SBKRvTVZRC4BFKNmAlzEOn1xykxqUjhiDHtyygPfvSdlyOENgi3ta4oODHrybnfddQN7PM%2FcCFmS%2BAp2UyI8vRZ49gyw%3D%3D"
        Glide.with(this)
            .load(imageUrl)
            .into(imagePost)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PolaryTheme {
        Greeting("Android")
    }
}