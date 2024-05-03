package com.example.polary.PostView

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.polary.BaseActivity
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Author
import com.example.polary.dataClass.User
import com.example.polary.utils.ApiCallBack
import com.example.polary.utils.SessionManager

class PostActivity : BaseActivity() {
    private lateinit var postFragment: Fragment
    private lateinit var authorSpinner: Spinner
    private lateinit var gridViewButton: ImageButton
    private lateinit var user: User

    companion object {
        var currentPosition = 0
        var PagerView = 0
        var GridView = 1
        var mode = PagerView
        var canChangeView = true
        var imageSrcIcon = R.drawable.ic_grid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        // Force hide the action bar
        supportActionBar?.hide()
        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getInt("currentPosition", 0)
            return;
        }
        authorSpinner = findViewById(R.id.author_spinner)
        gridViewButton = findViewById(R.id.grid_view_button)
        gridViewButton.setImageResource(imageSrcIcon)
        user = SessionManager(getSharedPreferences("user", MODE_PRIVATE)).getUserFromSharedPreferences()!!
        getUsers()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPosition", currentPosition)
    }
    private fun getUsers() {
        val httpMethod = HttpMethod()
        httpMethod.doGet<Author>("users/${user.id}/friends", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val users = ArrayList(data as List<Author>)
                users.add(0, Author(user.id, user.username, null))
                users.add(0, Author(0, "All", null))
                val adapter = AuthorAdapterSpinner(this@PostActivity, R.layout.author_spinner_item, users, user)
                adapter.setDropDownViewResource(R.layout.author_spinner_item)
                authorSpinner.adapter = adapter

                val myPost = intent.getBooleanExtra("myPost", false)
                if(myPost) {
                    val position = users.indexOfFirst { it.id == user.id }
                    authorSpinner.setSelection(position)
                } else {
                    authorSpinner.setSelection(0)
                }

                authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    var isInitialSelection = true
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        val postId = intent.getStringExtra("postId")
                        val authorId = users[position].id
                        val bundle = Bundle()
                        bundle.putString("userId", user.id.toString()) // the value of userId is this account's id
                        bundle.putString("authorId", authorId.toString()) // the value of authorId is the selected author's id
                        bundle.putString("postId", postId)
                        currentPosition = 0
                        if (isInitialSelection) {
                            isInitialSelection = false
                            postFragment = PostPagerFragment()
                            postFragment.arguments = bundle
                        } else {
                            if (mode == GridView) {
                                if (postFragment is PostGridFragment) {
                                    (postFragment as PostGridFragment).updatePosts(bundle) // use the same instance to update the posts
                                } else {
                                    postFragment = PostGridFragment()
                                    postFragment.arguments = bundle
                                }
                            } else {
                                if (postFragment is PostPagerFragment) {
                                    (postFragment as PostPagerFragment).updatePosts(bundle) // use the same instance to update the posts
                                } else {
                                    postFragment = PostPagerFragment()
                                    postFragment.arguments = bundle
                                }
                            }
                        }

                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.post_fragment, postFragment)
                            commit()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Do nothing
                    }
                }
                gridViewButton.setOnClickListener(View.OnClickListener {
                    if(!canChangeView) return@OnClickListener
                    val bundle = Bundle()
                    val author = authorSpinner.selectedItem as Author
                    bundle.putString("userId", user.id.toString()) // the value of userId is this account's id
                    bundle.putString("authorId", author.id.toString()) // the value of authorId is the selected author's id

                     if(mode == GridView) {
                        imageSrcIcon = R.drawable.ic_grid
                        gridViewButton.setImageResource(imageSrcIcon)
                        mode = PagerView
                        postFragment = PostPagerFragment()
                    } else {
                        imageSrcIcon = R.drawable.ic_pager
                        gridViewButton.setImageResource(imageSrcIcon)
                        mode = GridView
                        postFragment = PostGridFragment()
                    }

                    postFragment.arguments = bundle

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.post_fragment, postFragment)
                        commit()
                    }
                })
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.message.toString())
            }
        })
    }
    fun updateViewIcon() {
        gridViewButton.setImageResource(imageSrcIcon)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}