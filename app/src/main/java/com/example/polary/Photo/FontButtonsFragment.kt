package com.example.polary.Photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.polary.R
import com.example.polary.`object`.GlobalResources
import com.google.android.material.button.MaterialButton
import kotlin.properties.Delegates

class FontButtonsFragment : Fragment() {
    interface OnFontChangeListener {
        fun onFontChanged(font: Int)
    }
    private var listener: OnFontChangeListener? = null
    private lateinit var fontBtns: Array<MaterialButton>
    private var font by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_font_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        font = arguments?.getInt("font") ?: 0

        fontBtns = arrayOf(
            view.findViewById(R.id.btn_font0),
            view.findViewById(R.id.btn_font1),
            view.findViewById(R.id.btn_font2),
            view.findViewById(R.id.btn_font3),
            view.findViewById(R.id.btn_font4)
        )
        fontBtns[font].alpha = 1f

        changeButtonAlpha(R.id.btn_font0, 0)
        changeButtonAlpha(R.id.btn_font1, 1)
        changeButtonAlpha(R.id.btn_font2, 2)
        changeButtonAlpha(R.id.btn_font3, 3)
        changeButtonAlpha(R.id.btn_font4, 4)
    }

    private fun changeButtonAlpha(buttonId: Int, newFont: Int) {
        val button = view?.findViewById<MaterialButton>(buttonId)
        button?.setOnClickListener {
            val previousBtn = fontBtns[font]
            previousBtn.alpha = 0.5f
            // Change the alpha of the button to 1 (fully opaque)
            it.alpha = 1f
            font = newFont
            listener?.onFontChanged(font)
            val caption = activity?.findViewById<TextView>(R.id.post_caption)
            val typeface = ResourcesCompat.getFont(requireContext(), GlobalResources.fonts[font])
            if (caption != null) {
                caption.typeface = typeface
            }
        }
    }

    fun setOnFontChangeListener(listener: OnFontChangeListener) {
        this.listener = listener
    }
}