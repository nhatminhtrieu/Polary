package com.example.polary.usersList

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.polary.dataClass.Post
import com.example.polary.R
import com.example.polary.constant.EmojiDrawable
import java.util.Objects

class PostFragment : Fragment() {

    companion object {
        private const val ARG_POST = "post"

        fun newInstance(post: Post): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putSerializable(ARG_POST, post)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = arguments?.getSerializable(ARG_POST) as Post
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post, container, false)
        val author = view.findViewById<TextView>(R.id.post_author)
        author.text = post.author.username


        val caption = view.findViewById<TextView>(R.id.post_caption)
        caption.text = post.caption

        val image = view.findViewById<ImageView>(R.id.post_image)
        Glide.with(this).load(post.imageUrl).into(image)

        val listReactions = post.reactions
        val latestUser = view.findViewById<TextView>(R.id.latest_user)
       when {
           listReactions.size >= 3 -> {
               val firstEmojiDrawable= EmojiDrawable.map[listReactions[0].type ?: ""]
                val secondEmojiDrawable = EmojiDrawable.map[listReactions[1].type ?: ""]
                val thirdEmojiDrawable = EmojiDrawable.map[listReactions[2].type ?: ""]

                if(firstEmojiDrawable != null)
                     Glide.with(this).load(firstEmojiDrawable).into(view.findViewById<ImageView>(R.id.n1_reaction))
                if(secondEmojiDrawable != null)
                        Glide.with(this).load(secondEmojiDrawable).into(view.findViewById<ImageView>(R.id.n2_reaction))
                if(thirdEmojiDrawable != null)
                        Glide.with(this).load(thirdEmojiDrawable).into(view.findViewById<ImageView>(R.id.n3_reaction))
               latestUser.text = "${listReactions[0].author.username ?: ""} and others"
           }
           listReactions.isNotEmpty() -> {
               val firstEmojiDrawableId = EmojiDrawable.map[listReactions[0].type]
               val drawable = if(firstEmojiDrawableId != null) {
                   context?.getDrawable(firstEmojiDrawableId)
               } else {
                   null
               }

                if (drawable != null) {
                     view.findViewById<ImageView>(R.id.n1_reaction).setImageDrawable(drawable)
                } else {
                    view.findViewById<ImageView>(R.id.n1_reaction).visibility = View.GONE
                }

               if (listReactions.size == 2) {
                   val secondEmojiDrawableId = EmojiDrawable.map[listReactions[1].type]
                   val secondEmojiDrawable = if(secondEmojiDrawableId != null) {
                          context?.getDrawable(secondEmojiDrawableId)
                     } else {
                          null
                     }

                     if (secondEmojiDrawable != null)
                          view.findViewById<ImageView>(R.id.n2_reaction).setImageDrawable(secondEmojiDrawable)
               } else {
                   val n2Reaction = view.findViewById<ImageView>(R.id.n2_reaction)
                   n2Reaction.visibility = View.GONE
                   val layoutParams = n2Reaction.layoutParams as ViewGroup.MarginLayoutParams
                   val layoutParamsText = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                   layoutParamsText.marginStart -= layoutParams.marginStart
                   latestUser.layoutParams = layoutParamsText
                    layoutParams.marginStart = 0
                   n2Reaction.layoutParams = layoutParams
               }

               if (listReactions.size == 3) {
                   val thirdEmojiDrawableId = EmojiDrawable.map[listReactions[2].type ?: ""]
                     val thirdEmojiDrawable = if(thirdEmojiDrawableId != null) {
                              context?.getDrawable(thirdEmojiDrawableId)
                        } else {
                              null
                        }

                        if (thirdEmojiDrawable != null)
                              view.findViewById<ImageView>(R.id.n3_reaction).setImageDrawable(thirdEmojiDrawable)
               } else {
                   val n3Reaction = view.findViewById<ImageView>(R.id.n3_reaction)
                   n3Reaction.visibility = View.GONE
                   val layoutParams = n3Reaction.layoutParams as ViewGroup.MarginLayoutParams
                   val layoutParamsText = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                   layoutParamsText.marginStart -= layoutParams.marginStart
                     latestUser.layoutParams = layoutParamsText
                   layoutParams.marginStart = 0
                   n3Reaction.layoutParams = layoutParams
               }

               when {
                   listReactions.size == 1 -> latestUser.text = listReactions[0].author.username
                   else -> latestUser.text = "${listReactions[0].author.username ?: ""} and others"
               }
           }

           else -> {
                view.findViewById<ImageView>(R.id.n1_reaction).visibility = View.GONE
                view.findViewById<ImageView>(R.id.n2_reaction).visibility = View.GONE
                view.findViewById<ImageView>(R.id.n3_reaction).visibility = View.GONE
                val layoutParams = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = 0
                latestUser.layoutParams = layoutParams
                latestUser.text = "No reaction yet"
           }

       }

        return view
    }
}