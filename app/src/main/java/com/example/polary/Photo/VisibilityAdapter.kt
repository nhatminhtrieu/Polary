package com.example.polary.PostView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.Photo.AdapterBehavior
import com.example.polary.dataClass.Visibility
import com.example.polary.R

class VisibilityAdapter(private var visibilities: List<Visibility>, private val listener: AdapterBehavior) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_ALL_PEOPLE = 0
        private const val VIEW_TYPE_FRIEND = 1
    }

    inner class AllPeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var isColorChanged = false
        private val visibilityStroke: androidx.cardview.widget.CardView = itemView.findViewById(R.id.visibility_stroke_all)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            isColorChanged = toggleColor(visibilityStroke, isColorChanged)
            listener.onAllClick(isColorChanged)
        }
    }

    inner class VisibilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val displayName: TextView = itemView.findViewById(R.id.display_name)
        val avatar: ImageView = itemView.findViewById(R.id.user_avatar_view)
        private val visibilityStroke: androidx.cardview.widget.CardView = itemView.findViewById(R.id.visibility_stroke)
        private var isOn = false // Add this line

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onFriendClick(visibilities[position - 1].id, !isOn)
                // Toggle the color of visibilityStroke when the item is clicked
                isOn = toggleColor(visibilityStroke, isOn)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ALL_PEOPLE else VIEW_TYPE_FRIEND
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ALL_PEOPLE -> {
                val view = inflater.inflate(R.layout.all_people_item, parent, false)
                AllPeopleViewHolder(view)
            }
            VIEW_TYPE_FRIEND -> {
                val view = inflater.inflate(R.layout.friend_visibility_item, parent, false)
                VisibilityViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AllPeopleViewHolder -> {
                // Bind your data to the views of AllPeopleViewHolder if needed
            }
            is VisibilityViewHolder -> {
                // Since position 0 is AllPeopleViewHolder, we subtract 1 to get the correct index for visibilities list
                val visibility: Visibility = visibilities[position - 1]
                holder.displayName.text = visibility.displayName
                Glide.with(holder.itemView).load(visibility.avatar).into(holder.avatar)
            }
        }
    }

    override fun getItemCount(): Int {
        // We add 1 to the size of visibilities list because we have an extra item (AllPeopleViewHolder)
        return visibilities.size + 1
    }

    private fun toggleColor(cardView: androidx.cardview.widget.CardView, isColorChanged: Boolean): Boolean {
        if (isColorChanged) {
            // Change it back to the original color
            cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, com.google.android.material.R.color.m3_ref_palette_dynamic_neutral50)) // Replace 'original_color' with your original color resource
        } else {
            // Change the color to md_theme_primary
            cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.md_theme_primary))
        }
        return !isColorChanged // Toggle the flag
    }

    private fun turnOffColor(cardView: androidx.cardview.widget.CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, com.google.android.material.R.color.m3_ref_palette_dynamic_neutral50)) // Replace 'original_color' with your original color resource
    }

    private fun turnOnColor(cardView: androidx.cardview.widget.CardView) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.md_theme_primary))
    }
}