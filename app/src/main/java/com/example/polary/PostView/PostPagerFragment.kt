package com.example.polary.PostView

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.SharedElementCallback
import androidx.viewpager.widget.ViewPager
import com.example.polary.Class.HttpMethod
import com.example.polary.dataClass.PostNotification
import com.example.polary.R
import com.example.polary.dataClass.Post
import com.example.polary.utils.ApiCallBack

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "authorId"
private const val ARG_PARAM3 = "postId"

/**
 * A simple [Fragment] subclass.
 * Use the [PostPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostPagerFragment : Fragment() {
    private var userId: String? = null
    private var authorId: String? = null
    private lateinit var viewPager: ViewPager
    private lateinit var postNotification: List<PostNotification>
    private lateinit var posts: List<Post>
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
            authorId = it.getString(ARG_PARAM2)
            postId = it.getString(ARG_PARAM3) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        postponeEnterTransition()
        val view = inflater.inflate(R.layout.fragment_post_view, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        prepareSharedElementTransition()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val httpMethod = HttpMethod()
        httpMethod.doGet<PostNotification>("users/${userId}/notifications", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                postNotification = ArrayList(data as List<PostNotification>).ifEmpty {
                    emptyList()
                }
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.toString())
            }
        })
        val endpoint = "users/${userId}/viewable-posts"
        val queryParam = mapOf("authorId" to authorId.toString())
        httpMethod.doGetWithQuery<Post>(endpoint, queryParam, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                posts = ArrayList(data as List<Post>)
                if(posts.isEmpty()){
                    PostActivity.canChangeView = false
                    viewPager.visibility = View.GONE
                    view.findViewById<ImageView>(R.id.empty_icon).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.empty_caption).visibility = View.VISIBLE
                    return
                }
                viewPager.visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.empty_icon).visibility = View.GONE
                view.findViewById<TextView>(R.id.empty_caption).visibility = View.GONE
                PostActivity.canChangeView = true
                val postAdapter = PostPagerAdapter(childFragmentManager, posts, userId.toString(), postNotification)
                viewPager.adapter = postAdapter
                viewPager.setCurrentItem(PostActivity.currentPosition)
                viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                    }

                    override fun onPageSelected(position: Int) {
                        PostActivity.currentPosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {

                    }
                })

                if(postId.isNotEmpty()){
                    val post = posts.find { it.id.toString() == postId }
                    if(post != null){
                        val position = posts.indexOf(post)
                        moveToPage(position)
                    }
                }
                prepareSharedElementTransition()
                if(savedInstanceState == null){
                    postponeEnterTransition()
                }
            }

            override fun onError(error: Throwable) {
                Log.e("Error", error.toString())
            }
        })
    }

    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition
        setEnterSharedElementCallback(object: SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if(names.isNullOrEmpty())
                    return
                val currentFragment: Fragment = viewPager.adapter!!
                    .instantiateItem(viewPager, viewPager.currentItem) as Fragment
                val view = currentFragment.view ?: return
                sharedElements?.put(names[0], view.findViewById(R.id.post_image))
            }
        })
    }

    fun updatePosts(bundle: Bundle) {
        userId = bundle.getString("userId")
        authorId = bundle.getString("authorId")
        onViewCreated(requireView(), null)
    }
    fun moveToPage(position: Int) {
        viewPager.setCurrentItem(position, true)
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
        fun newInstance(param1: String, param2: String, param3: String) =
            PostPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }
}