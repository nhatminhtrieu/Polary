package com.example.polary.PostView

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.polary.Class.HttpMethod
import com.example.polary.`object`.GlobalResources
import com.example.polary.dataClass.Post
import com.example.polary.dataClass.PostNotification
import com.example.polary.R
import com.example.polary.constant.EmojiDrawable
import com.example.polary.utils.ApiCallBack


class PostFragment : Fragment(), OnCommentSentListener {
    companion object {
        private const val ARG_POST = "post"
        private const val ARG_USER_ID = "userId"
        private const val ARG_NOTIFICATION = "notification"

        fun newInstance(post: Post, userId: String, postNotification: PostNotification?): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putSerializable(ARG_POST, post)
            args.putString(ARG_USER_ID, userId)
            args.putSerializable(ARG_NOTIFICATION, postNotification)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var post: Post
    private lateinit var userId: String
    private lateinit var postNotification: PostNotification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = arguments?.getSerializable(ARG_POST) as Post
        userId = arguments?.getString(ARG_USER_ID) ?: ""
        postNotification = arguments?.getSerializable(ARG_NOTIFICATION) as? PostNotification ?: PostNotification(0, "none", "0")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post, container, false)
        val caption = view.findViewById<TextView>(R.id.post_caption)
        caption.text = post.caption
        val typeface = ResourcesCompat.getFont(requireContext(), GlobalResources.fonts[post.font])
        caption.typeface = typeface

        val frame = view.findViewById<ImageView>(R.id.post_frame)
        Glide.with(this).load(GlobalResources.frames[post.frame]).into(frame)

        val image = view.findViewById<ImageView>(R.id.post_image)
        image.transitionName = post.imageUrl
        Glide.with(this)
            .load(post.imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                    // startPostponedEnterTransition() should also be called on it to get the transition
                    // going in case of a failure.
                    parentFragment?.startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                    // startPostponedEnterTransition() should also be called on it to get the transition
                    // going when the image is ready.
                    parentFragment?.startPostponedEnterTransition()
                    return false
                }
            })
            .into(image)

        val avatar: ImageView = view.findViewById(R.id.author_avatar_view)
        Glide.with(this).load(post.author.avatar).error(R.drawable.ic_launcher_foreground).into(avatar)

        val username: TextView = view.findViewById(R.id.author_username)
        username.text = if(post.author.id.toString() == userId) {
            "You"
        } else {
            post.author.username
        }

        val totalReactions: RelativeLayout = view.findViewById(R.id.total_reaction)
        if(userId != post.author.id.toString()) {
            totalReactions.visibility = View.INVISIBLE
        } else {
            val listReactions = post.reactions
            val latestUser = view.findViewById<TextView>(R.id.latest_user)
            when {
                listReactions.size >= 3 -> {
                    val firstEmojiDrawable = EmojiDrawable.map[listReactions[0].type ?: ""]
                    val secondEmojiDrawable = EmojiDrawable.map[listReactions[1].type ?: ""]
                    val thirdEmojiDrawable = EmojiDrawable.map[listReactions[2].type ?: ""]

                    if (firstEmojiDrawable != null)
                        Glide.with(this).load(firstEmojiDrawable)
                            .into(view.findViewById<ImageView>(R.id.n1_reaction))
                    if (secondEmojiDrawable != null)
                        Glide.with(this).load(secondEmojiDrawable)
                            .into(view.findViewById<ImageView>(R.id.n2_reaction))
                    if (thirdEmojiDrawable != null)
                        Glide.with(this).load(thirdEmojiDrawable)
                            .into(view.findViewById<ImageView>(R.id.n3_reaction))
                    latestUser.text =
                        "${listReactions[0].author.username ?: ""} ${requireContext().getString(R.string.other_user)}"
                }

                listReactions.isNotEmpty() -> {
                    val firstEmojiDrawableId = EmojiDrawable.map[listReactions[0].type]
                    if (firstEmojiDrawableId != null)
                        Glide.with(this).load(firstEmojiDrawableId)
                            .into(view.findViewById<ImageView>(R.id.n1_reaction))

                    if (listReactions.size == 2) {
                        val secondEmojiDrawableId = EmojiDrawable.map[listReactions[1].type]
                        Glide.with(this).load(secondEmojiDrawableId)
                            .into(view.findViewById<ImageView>(R.id.n2_reaction))
                    } else {
                        val n2Reaction = view.findViewById<ImageView>(R.id.n2_reaction)
                        n2Reaction.visibility = View.GONE
                        val layoutN2Reaction =
                            n2Reaction.layoutParams as ViewGroup.MarginLayoutParams
                        val layoutParamsText =
                            latestUser.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParamsText.marginStart -= layoutN2Reaction.marginStart
                        layoutN2Reaction.marginStart = 0
                        latestUser.layoutParams = layoutParamsText
                        n2Reaction.layoutParams = layoutN2Reaction
                    }

                    when {
                        listReactions.size == 1 -> latestUser.text =
                            listReactions[0].author.username

                        else -> latestUser.text = "${listReactions[0].author.username ?: ""} ${
                            requireContext().getString(R.string.other_user)
                        }"
                    }
                }

                else -> {
                    view.findViewById<ImageView>(R.id.n1_reaction).visibility = View.GONE
                    view.findViewById<ImageView>(R.id.n2_reaction).visibility = View.GONE
                    view.findViewById<ImageView>(R.id.n3_reaction).visibility = View.GONE
                    val layoutParams = latestUser.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.marginStart = 0
                    latestUser.layoutParams = layoutParams
                    latestUser.text = requireContext().getString(R.string.no_reaction)
                }

            }
            totalReactions.setOnClickListener {
                openReactionSheet(post.id, listReactions.size)
            }
        }
        val totalComments = view.findViewById<TextView>(R.id.total_comment)
        if(userId != post.author.id.toString()) {
            totalComments.visibility = View.INVISIBLE
        } else {
            when (val countComments = post.countComments.toInt()) {
                0 -> totalComments.text = requireContext().getString(R.string.no_comment)
                1 -> totalComments.text = requireContext().getString(R.string.one_comment)
                else -> totalComments.text = "View all $countComments comments"
            }

            totalComments.setOnClickListener {
                openCommentSheet(post.id, post.countComments.toInt())
            }
        }
        val buttonInputComment = view.findViewById<ImageButton>(R.id.button_input_comment)
        if (userId == post.author.id.toString()) {
            buttonInputComment.isEnabled = false
        } else {
            buttonInputComment.setOnClickListener { openCommentDialog()}
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventClickReaction()
    }

    override fun onResume() {
        super.onResume()
        postNotification.let { notification ->
            if(notification.emoji != "none" && postNotification.postId == post.id.toString()) {
                animateReaction(notification.emoji, "RECEIVE")
                val httpMethod = HttpMethod()
                httpMethod.doPatch("notifications/${notification.id}", mapOf("status" to "read"), object: ApiCallBack<Any> {
                    override fun onSuccess(data: Any) {
                        Log.d("Notification", "Notification read")
                    }

                    override fun onError(error: Throwable) {
                        Log.e("Notification", "Error: ${error.message}")
                    }
                })
                postNotification = PostNotification(0, "none", "0")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        postNotification = PostNotification(0, "none", "0")
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

    private fun sendReactions (postId: Number, authorId: Number, type: String, view: View) {
        val httpMethod = HttpMethod()
        val endpoint = "posts/${postId}/reactions"
        val body = mapOf("authorId" to authorId, "type" to type)
        httpMethod.doPost(endpoint, body, object: ApiCallBack<Any> {
            override fun onSuccess(data: Any) {
                animateReaction(type, "SEND")
            }

            override fun onError(error: Throwable) {
                Log.e("error", "Error: ${error.message}")
            }
        })
    }

    private fun eventClickReaction() {
        val heart = view?.findViewById<ImageButton>(R.id.love)
        val cry = view?.findViewById<ImageButton>(R.id.cry)
        val smile = view?.findViewById<ImageButton>(R.id.smile)
        val clown = view?.findViewById<ImageButton>(R.id.clown)
        val skull = view?.findViewById<ImageButton>(R.id.skull)

        if(userId == post.author.id.toString()) {
            heart?.isEnabled = false
            cry?.isEnabled = false
            smile?.isEnabled = false
            clown?.isEnabled = false
            skull?.isEnabled = false
            return
        }

        heart?.setOnClickListener {
            sendReactions(post.id, userId.toInt(), "heart", it)
        }

        cry?.setOnClickListener {
            sendReactions(post.id, userId.toInt(), "cry", it)
        }

        smile?.setOnClickListener {
            sendReactions(post.id, userId.toInt(), "smile", it)
        }

        clown?.setOnClickListener {
            sendReactions(post.id, userId.toInt(), "clown", it)
        }

        skull?.setOnClickListener {
            sendReactions(post.id, userId.toInt(), "skull", it)
        }
    }

    private fun animateReaction(type: String, mode: String) {
        val drawableId = EmojiDrawable.map[type] ?: return

        val parentLayout = activity?.findViewById<ConstraintLayout>(R.id.main)

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        val spaces = ArrayList<Int>(
            listOf(
                100,
                -150,
                200,
                -250,
                300,
                -350
            )
        )
        for (i in 0 until 6) { // Change this to the number of emojis you want
            val reactionImage = ImageView(context)
            reactionImage.setImageResource(drawableId)

            val layoutParams = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.leftMargin = (screenWidth / 2) - (reactionImage.drawable.intrinsicWidth / 2) // Center the emoji
            reactionImage.layoutParams = layoutParams

            parentLayout?.addView(reactionImage)

            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val anim = if(mode == "SEND") {
                TranslateAnimation((screenWidth/2 + spaces[i]).toFloat(), (screenWidth/2 + spaces[i]).toFloat(), screenHeight.toFloat(), -500f)
            } else {
                TranslateAnimation((screenWidth/2 + spaces[i]).toFloat(), (screenWidth/2 + spaces[i]).toFloat(), -500f, screenHeight.toFloat())
            }
            anim.duration = 1000
            anim.fillAfter = true
            anim.startOffset = (i * 300).toLong() // Add a delay to the start of each animation
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    parentLayout?.removeView(reactionImage)
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
            reactionImage.startAnimation(anim)
        }
    }

    private fun openCommentDialog() {
        val modalBottomSheet = TypeCommentFragment(userId.toInt(), post.id)
        modalBottomSheet.listener = this
        modalBottomSheet.show(parentFragmentManager, TypeCommentFragment.TAG)
    }

    override fun onCommentSent() {
       animateReaction("comment", "SEND")
    }
}