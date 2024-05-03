package com.example.polary.PostView

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.Class.HttpMethod
import com.example.polary.R
import com.example.polary.dataClass.Comment
import com.example.polary.utils.ApiCallBack
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentFragment(private val postId: Number) : BottomSheetDialogFragment() {
    private var Behavior: BottomSheetBehavior<FrameLayout>? = null
    private lateinit var _comments: List<Comment>
    private lateinit var rvComment: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.comment, container, false)
        getComments(postId)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle).apply {
            setContentView(R.layout.comment)
            setCanceledOnTouchOutside(true)
            val bottomSheet = view?.findViewById<FrameLayout>(R.id.standard_bottom_sheet)
            if (bottomSheet != null) {
                Behavior = BottomSheetBehavior.from(bottomSheet)
            }


            view?.findViewById<RecyclerView>(R.id.comment_recycler_view)?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(Behavior is CustomBottomSheetBehavior) {
                        (Behavior as CustomBottomSheetBehavior).setAllowDragging(newState == RecyclerView.SCROLL_STATE_IDLE)
                    }
                }
            })
        }
    }

    private fun getComments(postId: Number) {
        // get comments from server
        val httpMethod = HttpMethod()
        httpMethod.doGet<Comment>("posts/${postId}/comments", object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                val comments = data as List<Comment>
                _comments = comments
                rvComment = view?.findViewById(R.id.comment_recycler_view)!!
                commentAdapter = CommentAdapter(comments)
                rvComment.layoutManager = LinearLayoutManager(context)
                rvComment.adapter = commentAdapter
                rvComment.setHasFixedSize(true)
            }
            override fun onError(error: Throwable) {
                Log.e("error", "Error: ${error.message}")
            }
        })
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}