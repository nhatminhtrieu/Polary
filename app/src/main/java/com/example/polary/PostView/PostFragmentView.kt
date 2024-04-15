package com.example.polary.PostView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Post
import com.example.polary.utils.ApiCallBack

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "authorId"

/**
 * A simple [Fragment] subclass.
 * Use the [PostFragmentView.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostFragmentView : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String? = null
    private var authorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
            authorId = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val httpMethod = HttpMethod()
        val endpoint = "users/${userId}/viewable-posts"
        val queryParam = mapOf("authorId" to authorId.toString())
        httpMethod.doGetWithQuery<Post>(endpoint, queryParam, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val posts = ArrayList(data as List<Post>)
                if(posts.isEmpty()){
                    view.findViewById<ImageView>(R.id.empty_icon).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.empty_caption).visibility = View.VISIBLE
                    return
                }
                val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
                val postAdapter = PostPagerAdapter(childFragmentManager, posts)
                viewPager.adapter = postAdapter
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.toString())
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostFragmentView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostFragmentView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}