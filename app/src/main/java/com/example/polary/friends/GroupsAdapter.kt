package com.example.polary.PostView

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.polary.dataClass.Comment
import com.example.polary.R
import com.example.polary.dataClass.Group
import com.example.polary.friends.GroupDetailsFragment
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GroupsAdapter(private var groups: List<Group>) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.group_name)
        val memberCount: TextView = itemView.findViewById(R.id.group_members)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.group_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group: Group = groups[position]
        holder.name.text = group.name
        holder.memberCount.text = "${group.memberCount} members"
        holder.itemView.setOnClickListener {
            val fragment = GroupDetailsFragment()
            val bundle = Bundle()
            bundle.putInt("groupId", group.id)
            fragment.arguments = bundle
            (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("GroupDetailsFragment")
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}