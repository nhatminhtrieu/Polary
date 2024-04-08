package com.example.polary.PostView


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.polary.dataClass.Post

class PostPagerAdapter(
    fragmentManager: FragmentManager,
    private val posts: List<Post>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = posts.size

    override fun getItem(position: Int): Fragment = PostFragment.newInstance(posts[position])
}