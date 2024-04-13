package com.example.polary.PostView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.polary.dataClass.Post
import com.example.polary.R
import com.example.polary.constant.EmojiDrawable

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

        val avatar = view.findViewById<ImageView>(R.id.user_avatar_view)
        Glide.with(this).load(post.author.avatar).into(avatar)

        val caption = view.findViewById<TextView>(R.id.post_caption)
        caption.text = post.caption

        val image = view.findViewById<ImageView>(R.id.post_image)
        Glide.with(this).load(post.imageUrl).into(image)

        val totalReactions: RelativeLayout = view.findViewById(R.id.total_reaction)
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
               latestUser.text = "${listReactions[0].author.username ?: ""} ${requireContext().getString(R.string.other_user)}"
           }
           listReactions.isNotEmpty() -> {
               val firstEmojiDrawableId = EmojiDrawable.map[listReactions[0].type]
               if(firstEmojiDrawableId != null)
                   Glide.with(this).load(firstEmojiDrawableId).into(view.findViewById<ImageView>(R.id.n1_reaction))

               if (listReactions.size == 2) {
                   val secondEmojiDrawableId = EmojiDrawable.map[listReactions[1].type]
                   Glide.with(this).load(secondEmojiDrawableId).into(view.findViewById<ImageView>(R.id.n2_reaction))
               } else {
                   val n2Reaction = view.findViewById<ImageView>(R.id.n2_reaction)
                   n2Reaction.visibility = View.GONE
                   val layoutN2Reaction = n2Reaction.layoutParams as ViewGroup.MarginLayoutParams
                   val layoutParamsText = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                   layoutParamsText.marginStart -= layoutN2Reaction.marginStart
                   layoutN2Reaction.marginStart = 0
                   latestUser.layoutParams = layoutParamsText
                   n2Reaction.layoutParams = layoutN2Reaction
               }

               when {
                   listReactions.size == 1 -> latestUser.text = listReactions[0].author.username
                   else -> latestUser.text = "${listReactions[0].author.username ?: ""} ${requireContext().getString(R.string.other_user)}"
               }
           }

           else -> {
                view.findViewById<ImageView>(R.id.n1_reaction).visibility = View.GONE
                view.findViewById<ImageView>(R.id.n2_reaction).visibility = View.GONE
                view.findViewById<ImageView>(R.id.n3_reaction).visibility = View.GONE
                val layoutParams = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = 0
                latestUser.layoutParams = layoutParams
                latestUser.text =  requireContext().getString(R.string.no_reaction)
           }

       }
        val totalComments = view.findViewById<TextView>(R.id.total_comment)
        when(val countComments = post.countComments.toInt()) {
            0 -> totalComments.text = requireContext().getString(R.string.no_comment)
            1 -> totalComments.text = requireContext().getString(R.string.one_comment)
            else -> totalComments.text = "View all $countComments comments"
        }

        totalComments.setOnClickListener{
            openCommentSheet(post.id, post.countComments.toInt())
        }


        totalReactions.setOnClickListener {
            openReactionSheet(post.id, listReactions.size)
        }

        return view
    }

    private fun openCommentSheet(postId: Number, totalComments: Int) {
        if(totalComments == 0) return
        val modalBottomSheet = CommentFragment(postId)
        modalBottomSheet.show(parentFragmentManager, CommentFragment.TAG)
    }

    private fun openReactionSheet(postId: Number, totalReactions: Int) {
        if(totalReactions == 0) return
        val modalBottomSheet = ReactionFragment(postId)
        modalBottomSheet.show(parentFragmentManager, ReactionFragment.TAG)
    }


}