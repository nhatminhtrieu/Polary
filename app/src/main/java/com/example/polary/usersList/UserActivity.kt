package com.example.polary.usersList

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.Class.Post
import com.example.polary.R
import com.example.polary.ultils.ApiCallBack

class UserActivity : AppCompatActivity() {
    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postAdapter: PostAdapter
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        listView = findViewById(R.id.list_post)
        getUsersPosts(5)
    }

    private fun getUsersPosts(userId: Number) {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Post>("users/${userId}/viewable-posts", object: ApiCallBack<Any>{
            override fun onSuccess(data: Any) {
                posts = ArrayList(data as List<Post>)
                postAdapter = PostAdapter(this@UserActivity, posts)
                listView.adapter = postAdapter
                Log.i("Hello", posts.toString())
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.message.toString())
            }
        })
    }
}