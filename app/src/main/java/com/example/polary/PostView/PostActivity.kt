package com.example.polary.PostView

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.Post
import com.example.polary.R
import com.example.polary.utils.ApiCallBack

class PostActivity : AppCompatActivity() {
    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postAdapter: PostPagerAdapter
    private lateinit var viewPager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        viewPager = findViewById(R.id.viewPager)
        getUsersPosts(5)
    }

    private fun getUsersPosts(userId: Number) {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Post>("users/${userId}/viewable-posts", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                posts = ArrayList(data as List<Post>)
                postAdapter = PostPagerAdapter(supportFragmentManager, posts)
                viewPager.adapter = postAdapter
                Log.i("Hello", posts.toString())
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.message.toString())
            }
        })
    }
}