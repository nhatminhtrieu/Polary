package com.example.polary.PostView

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.polary.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.content.Context
import android.util.Log
import android.widget.ImageButton
import com.example.polary.Class.HttpMethod
import com.example.polary.utils.ApiCallBack

class TypeCommentFragment(private val authorId: Number, private val postId: Number) : BottomSheetDialogFragment() {
    private lateinit var textInputComment: EditText
    private lateinit var sendButton: ImageButton
    var listener: OnCommentSentListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AddCommentDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.type_comment, container, false)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputComment = view.findViewById(R.id.text_input_comment)
        sendButton = view.findViewById(R.id.send_comment)
        sendButton.setOnClickListener {
            sendComment()
            dismiss()
        }
    }
    override fun onResume() {
        super.onResume()
        showKeyboard()
    }
    private fun showKeyboard() {
        textInputComment.requestFocus()
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(textInputComment, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun sendComment() {
        val httpMethod = HttpMethod()
        val endpoint = "posts/${postId}/comments"
        val body = mapOf("authorId" to authorId, "content" to textInputComment.text.toString())
        httpMethod.doPost(endpoint, body, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                listener?.onCommentSent()
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