package com.example.polary.PostView

import PostGridAdapter
import androidx.core.app.SharedElementCallback
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [PostGridFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostGridFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String? = null
    private var authorId: String? = null
    private lateinit var rvPostGrid: RecyclerView
    private lateinit var postGridAdapter: PostGridAdapter
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
    ): View {
        // Inflate the layout for this fragment
        postponeEnterTransition()
        val view =  inflater.inflate(R.layout.fragment_post_grid, container, false)
        rvPostGrid = view.findViewById(R.id.post_grid)
        rvPostGrid.setHasFixedSize(true)
        postGridAdapter = PostGridAdapter(ArrayList(), authorId,this)
        prepareTransition()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val httpMethod = HttpMethod()
        val endpoint = "users/${userId}/viewable-posts"
        val queryParam = mapOf("authorId" to authorId.toString())
        httpMethod.doGetWithQuery<Post>(endpoint, queryParam, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val posts = ArrayList(data as List<Post>)
                if(posts.isEmpty()) {
                    postGridAdapter.clearData()
                    PostActivity.canChangeView = false
                    rvPostGrid.visibility = View.GONE
                    view.findViewById<ImageView>(R.id.empty_icon).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.empty_caption).visibility = View.VISIBLE
                    return
                }
                PostActivity.canChangeView = true
                rvPostGrid.visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.empty_icon).visibility = View.GONE
                view.findViewById<TextView>(R.id.empty_caption).visibility = View.GONE
                postGridAdapter.setPosts(posts)
                rvPostGrid.adapter = postGridAdapter
                rvPostGrid.layoutManager = GridLayoutManager(context, 3)
                startPostponedEnterTransition()
                scrollToPosition()
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
         * @return A new instance of fragment PostGridFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostGridFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun scrollToPosition() {
        rvPostGrid.addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                rvPostGrid.removeOnLayoutChangeListener(this)
                val layoutManager = rvPostGrid.layoutManager
                val viewAtPosition = layoutManager?.findViewByPosition(PostActivity.currentPosition)
                if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
                    rvPostGrid.post {
                        layoutManager?.scrollToPosition(PostActivity.currentPosition)
                    }
                }
            }
        })
    }
    private fun prepareTransition() {
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.grid_exit_transition)
        setExitSharedElementCallback(object: SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                val selectedViewHolder: RecyclerView.ViewHolder = rvPostGrid
                    .findViewHolderForAdapterPosition(PostActivity.currentPosition) ?: return
                sharedElements?.put(names!![0], selectedViewHolder.itemView.findViewById(R.id.post_grid_image))
            }
        })
    }

    fun updatePosts(bundle: Bundle) {
        userId = bundle.getString("userId")
        authorId = bundle.getString("authorId")
        postGridAdapter.updateBundle(this.authorId)
        onViewCreated(requireView(), null)
    }
}