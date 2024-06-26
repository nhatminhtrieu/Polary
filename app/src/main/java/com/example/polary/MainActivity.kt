package com.example.polary

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.polary.PostView.PostActivity
import com.example.polary.authentication.SignIn
import com.example.polary.ui.theme.PolaryTheme

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    Button(onClick = { navigateToUserActivity() }) {
                        Text("Go to Login")
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
    }

    private fun navigateToUserActivity() {
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PolaryTheme {
//        Greeting("Android")
//    }
//}