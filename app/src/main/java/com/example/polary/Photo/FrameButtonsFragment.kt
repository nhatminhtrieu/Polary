package com.example.polary.Photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.`object`.GlobalResources
import com.google.android.material.button.MaterialButton
import kotlin.properties.Delegates

class FrameButtonsFragment : Fragment() {
    interface OnFrameChangeListener {
        fun onFrameChanged(frame: Int)
    }
    private var listener: OnFrameChangeListener? = null
    private lateinit var frameBtns: Array<MaterialButton>
    private var frame by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frame_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frame = arguments?.getInt("frame") ?: 0

        frameBtns = arrayOf(
            view.findViewById(R.id.btn_frame0),
            view.findViewById(R.id.btn_frame1),
            view.findViewById(R.id.btn_frame2),
            view.findViewById(R.id.btn_frame3),
            view.findViewById(R.id.btn_frame4)
        )
        frameBtns[frame].alpha = 1f

        changeButtonSizeAndAlpha(R.id.btn_frame0, 0)
        changeButtonSizeAndAlpha(R.id.btn_frame1, 1)
        changeButtonSizeAndAlpha(R.id.btn_frame2, 2)
        changeButtonSizeAndAlpha(R.id.btn_frame3, 3)
        changeButtonSizeAndAlpha(R.id.btn_frame4, 4)
    }

    private fun changeButtonSizeAndAlpha(buttonId: Int, newFrame: Int) {
        val button = view?.findViewById<MaterialButton>(buttonId)
        button?.setOnClickListener {
            val previousBtn = frameBtns[frame]
            previousBtn.alpha = 0.5f
            // Change the alpha of the button to 1 (fully opaque)
            it.alpha = 1f
            frame = newFrame
            listener?.onFrameChanged(frame)
            val postFrame = activity?.findViewById<ImageView>(R.id.post_frame)
            Glide.with(this).load(GlobalResources.frames[frame]).into(postFrame!!)
        }
    }

    fun setOnFrameChangeListener(listener: OnFrameChangeListener) {
        this.listener = listener
    }
}