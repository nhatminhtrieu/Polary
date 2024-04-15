package com.example.polary.PostView

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Author
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager

class PostActivity : AppCompatActivity(R.layout.activity_post) {
    private lateinit var postFragment: Fragment
    private lateinit var authorSpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUsers()
    }

    private fun getUsers() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Author>("users", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val users = ArrayList(data as List<Author>)
                users.add(0, Author(0, "All", null))
                authorSpinner = findViewById(R.id.author_spinner)
                val adapter = AuthorAdapterSpinner(this@PostActivity, R.layout.author_spinner_item, users)
                adapter.setDropDownViewResource(R.layout.author_spinner_item)
                authorSpinner.adapter = adapter

                authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        val authorId = users[position].id
                        val bundle = Bundle()
                        bundle.putString("userId", "5") // the value of userId is this account's id
                        bundle.putString("authorId", authorId.toString()) // the value of authorId is the selected author's id
                        postFragment = PostFragmentView()
                        postFragment.arguments = bundle

                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.post_fragment, postFragment)
                            commit()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Do nothing
                    }
                }
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.message.toString())
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}