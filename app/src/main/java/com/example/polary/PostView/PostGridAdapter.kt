import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.polary.PostView.PostActivity
import com.example.polary.PostView.PostPagerFragment
import com.example.polary.R
import com.example.polary.dataClass.Post
import com.example.polary.utils.SessionManager
import java.util.concurrent.atomic.AtomicBoolean

interface ViewHolderListener {
    fun onLoadCompleted(view: ImageView, adapterPosition: Int)
    fun onItemClicked(view: View, adapterPosition: Int)
    fun updateAuthorId(authorId: String?)
}

class PostGridAdapter(
    private val posts: ArrayList<Post>,
    authorId: String? = null,
    fragment: Fragment
) : RecyclerView.Adapter<PostGridAdapter.ImageViewHolder>() {

    private val requestManager: RequestManager = Glide.with(fragment)
    private val viewHolderListener: ViewHolderListener = ViewHolderListenerImpl(authorId, fragment, fragment.activity as PostActivity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list_item, parent, false)
        return ImageViewHolder(view, posts, requestManager, viewHolderListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun setPosts(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyItemRangeInserted(0, posts.size)
    }
    fun clearData() {
        val size = posts.size
        posts.clear()
        notifyItemRangeRemoved(0, size)

    }

    fun updateBundle(authorId: String?) {
        viewHolderListener.updateAuthorId(authorId)
    }

    private class ViewHolderListenerImpl(
        private var authorId: String? = null,
        private val fragment: Fragment,
        private val postActivity: PostActivity
    ) : ViewHolderListener {

        private val enterTransitionStarted = AtomicBoolean()

        override fun onLoadCompleted(view: ImageView, position: Int) {
            if (PostActivity.currentPosition != position) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        override fun onItemClicked(view: View, adapterPosition: Int) {
            // Handle item click here
            PostActivity.currentPosition = adapterPosition
            (fragment.exitTransition as TransitionSet).excludeTarget(view, true)
            val transitioningView: ImageView = view.findViewById(R.id.post_grid_image)
            val bundle = Bundle()
            val user = SessionManager(fragment.requireContext().getSharedPreferences("user", MODE_PRIVATE)).getUserFromSharedPreferences()!!
            bundle.putString("userId", user.id.toString()) // the value of userId is this account's id
            bundle.putString("authorId", authorId) // the value of authorId is the selected author's id
            val postPagerFragment = PostPagerFragment()
            postPagerFragment.arguments = bundle
            PostActivity.mode = PostActivity.PagerView
            PostActivity.imageSrcIcon = R.drawable.ic_grid
            postActivity.updateViewIcon()
            fragment.fragmentManager
                ?.beginTransaction()
                ?.setReorderingAllowed(true)
                ?.addSharedElement(transitioningView, transitioningView.transitionName)
                ?.replace(R.id.post_fragment, postPagerFragment, PostPagerFragment.Companion::class.java.simpleName)
                ?.addToBackStack(null)
                ?.commit()
        }

        override fun updateAuthorId(authorId: String?) {
            this.authorId = authorId
        }
    }

    class ImageViewHolder(
        itemView: View,
        private val posts: ArrayList<Post>,
        private val requestManager: RequestManager,
        private val viewHolderListener: ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val image: ImageView = itemView.findViewById(R.id.post_grid_image)

        init {
            itemView.findViewById<View>(R.id.post_grid_card).setOnClickListener(this)
        }

        fun onBind() {
            val adapterPosition = adapterPosition
            setImage(adapterPosition)
            image.transitionName = posts[adapterPosition].imageUrl
        }

        private fun setImage(adapterPosition: Int) {
            requestManager
                .load(posts[adapterPosition].imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        viewHolderListener.onLoadCompleted(image, adapterPosition)
                        return false
                    }
                })
                .into(image)
        }

        override fun onClick(view: View) {
            viewHolderListener.onItemClicked(view, adapterPosition)
        }
    }
}