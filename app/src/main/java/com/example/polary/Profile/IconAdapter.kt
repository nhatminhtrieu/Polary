package com.example.polary.Profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.R
import com.example.polary.constant.IconDrawable
import com.example.polary.utils.SessionManager

data class AppIcon(val aliasName: String) {
    val resourceId: Int
        get() = IconDrawable.map[aliasName] ?: R.mipmap.ic_launcher
}
class AppIconAdapter(
    private val icons: List<AppIcon>,
    private val onIconClicked: (AppIcon) -> Unit
) : RecyclerView.Adapter<AppIconAdapter.IconViewHolder>() {
    private var selectedRadioButton: RadioButton? = null
    private var oldPositionTemp: Int = -1

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(icon: AppIcon, position: Int) {
            val imageView = itemView.findViewById<ImageView>(R.id.iconImageView)
            val radioButton = itemView.findViewById<RadioButton>(R.id.iconRadioButton)

            imageView.setImageResource(icon.resourceId)
            val sharedPreferences = itemView.context.getSharedPreferences("user", 0)
            val sessionManager = SessionManager(sharedPreferences)
            val currentIcon = sessionManager.getAppIcon() ?: "default"

            // Check if the current icon is the default icon and the position is 0
            val isSelected = if (currentIcon == "default" && position == 0) {
                true
            } else {
                currentIcon == icon.aliasName
            }

            radioButton.isChecked = isSelected
            if (isSelected) {
                selectedRadioButton = radioButton
                oldPositionTemp = position
            }

            radioButton.setOnClickListener {
                onIconClicked(icon)
                selectedRadioButton?.isChecked = false
                selectedRadioButton = radioButton
                radioButton.isChecked = true
                sessionManager.saveAppIcon(icon.aliasName)
                if (oldPositionTemp == -1) {
                    notifyItemChanged(oldPositionTemp)
                    notifyItemChanged(position)
                }
                oldPositionTemp = position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position], position)
    }

    override fun getItemCount() = icons.size
}