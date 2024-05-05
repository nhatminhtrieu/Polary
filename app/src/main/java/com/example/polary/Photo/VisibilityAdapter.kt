package com.example.polary.PostView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.polary.R
import com.example.polary.dataClass.Friend
import com.example.polary.dataClass.Group
import java.util.Locale

class VisibilityAdapter(
    private var friends: List<Friend>,
    private var groups: List<Group>,
    private var selectedFriends: MutableList<Int>,
    private var selectedGroups: MutableList<Int>
) : RecyclerView.Adapter<ViewHolder>() {
    private var isAllPeopleSelected: Boolean = true
    companion object {
        private const val ALL_PEOPLE = 0
        private const val FRIEND = 1
        private const val GROUP = 2
    }

    inner class AllPeopleViewHolder(itemView: View) : ViewHolder(itemView) {
        val visibilityStroke: androidx.cardview.widget.CardView = itemView.findViewById(R.id.visibility_stroke_all)
        init {
            itemView.setOnClickListener {
                if (isAllPeopleSelected) {
                    isAllPeopleSelected = false
                    selectedFriends.clear()
                    selectedGroups.clear()
                } else {
                    isAllPeopleSelected = true
                    selectedFriends.clear()
                    selectedGroups.clear()
                    selectedFriends.addAll(friends.map { it.id })
                }
                notifyItemRangeChanged(0, itemCount - 1)
            }
        }
    }

    inner class FriendViewHolder(itemView: View) : ViewHolder(itemView) {
        val displayName: TextView = itemView.findViewById(R.id.display_name)
        val avatar: ImageView = itemView.findViewById(R.id.user_avatar_view)
        val visibilityStroke: androidx.cardview.widget.CardView = itemView.findViewById(R.id.visibility_stroke)
        init {
            itemView.setOnClickListener {
                val friend: Friend = friends[adapterPosition - groups.size - 1]
                if (isAllPeopleSelected) {
                    isAllPeopleSelected = false
                    selectedFriends.clear()
                    notifyItemChanged(0)
                }
                if (selectedFriends.contains(friend.id)) {
                    selectedFriends.remove(friend.id)
                    notifyItemChanged(adapterPosition)
                } else {
                    selectedFriends.add(friend.id)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    inner class GroupViewHolder(itemView: View) : ViewHolder(itemView) {
        val displayName: TextView = itemView.findViewById(R.id.display_name)
        val groupName: TextView = itemView.findViewById(R.id.group_shorten_name)
        val visibilityStroke: androidx.cardview.widget.CardView = itemView.findViewById(R.id.visibility_stroke)
        init {
            itemView.setOnClickListener {
                val group: Group = groups[adapterPosition - 1]
                if (group.memberIds?.size == 0) {
                    Toast.makeText(itemView.context, "No members in this group", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (selectedGroups.contains(group.id)) {
                    selectedGroups.remove(group.id)
                    notifyItemChanged(adapterPosition)
                } else {
                    if (isAllPeopleSelected) {
                        isAllPeopleSelected = false
                        selectedFriends.clear()
                        notifyItemChanged(0)
                    }
                    selectedGroups.add(group.id)
                    group.memberIds?.let { it1 -> selectedFriends.addAll(it1) }
                    notifyItemRangeChanged(groups.size, itemCount)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ALL_PEOPLE
            in 1..groups.size -> GROUP
            else -> FRIEND
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ALL_PEOPLE -> {
                val view = inflater.inflate(R.layout.all_people_item, parent, false)
                AllPeopleViewHolder(view)
            }
            FRIEND -> {
                val view = inflater.inflate(R.layout.friend_visibility_item, parent, false)
                FriendViewHolder(view)
            }
            GROUP -> {
                val view = inflater.inflate(R.layout.group_visibility_item, parent, false)
                GroupViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is AllPeopleViewHolder -> {
                if (isAllPeopleSelected) {
                    turnOnColor(holder)
                } else {
                    turnOffColor(holder)
                }
            }
            is FriendViewHolder -> {
                val friend : Friend = friends[position - groups.size - 1]
                holder.displayName.text = friend.username
                Glide.with(holder.itemView).load(friend.avatar).into(holder.avatar)
                if (selectedFriends.contains(friend.id) && !isAllPeopleSelected) {
                    turnOnColor(holder)
                } else {
                    turnOffColor(holder)
                }
            }
            is GroupViewHolder -> {
                val group : Group = groups[position - 1]
                holder.displayName.text = group.name
                val groupNameFormatted = if (group.name.split(" ").size == 1) {
                    if (group.name.length < 2) group.name else group.name.take(2)
                } else {
                    group.name.split(" ").take(2).joinToString("") { it[0].toString() }
                }
                holder.groupName.text = groupNameFormatted.uppercase(Locale.ROOT)
                if (selectedGroups.contains(group.id)) {
                    turnOnColor(holder)
                } else {
                    turnOffColor(holder)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groups.size + friends.size + 1
    }

    @SuppressLint("PrivateResource")
    private fun turnOffColor(holder: ViewHolder) {
        val stroke = when (holder) {
            is AllPeopleViewHolder -> holder.visibilityStroke
            is FriendViewHolder -> holder.visibilityStroke
            is GroupViewHolder -> holder.visibilityStroke
            else -> return
        }
        stroke.setCardBackgroundColor(ContextCompat.getColor(stroke.context, androidx.appcompat.R.color.material_grey_50)) // Replace 'original_color' with your original color resource
    }

    private fun turnOnColor(holder: ViewHolder) {
        val stroke = when (holder) {
            is AllPeopleViewHolder -> holder.visibilityStroke
            is FriendViewHolder -> holder.visibilityStroke
            is GroupViewHolder -> holder.visibilityStroke
            else -> return
        }
        stroke.setCardBackgroundColor(ContextCompat.getColor(stroke.context, R.color.md_theme_primary))
    }
}