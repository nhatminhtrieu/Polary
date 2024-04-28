package com.example.polary.Profile

import AppIcon
import AppIconAdapter
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polary.R
import com.example.polary.constant.IconDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppIconFragment : BottomSheetDialogFragment() {

    private lateinit var iconRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_icon, container, false)

        iconRecyclerView = view.findViewById(R.id.appIconRecyclerView)
        iconRecyclerView.layoutManager = GridLayoutManager(context, 3)
        val icons = IconDrawable.map.keys.map { AppIcon(it) }
        iconRecyclerView.adapter = AppIconAdapter(icons) { icon ->
            changeAppIcon(icon)
            dismiss()
        }

        return view
    }


    private fun changeAppIcon(icon: AppIcon) {
        val pm = context?.packageManager ?: run {
            Toast.makeText(requireContext(), "Failed to change app icon", Toast.LENGTH_SHORT).show()
            return
        }

        val iconComponent = ComponentName(requireContext(), "${requireContext().packageName}.${icon.aliasName}")
        val onBoardingComponent = ComponentName(requireContext(), "${requireContext().packageName}.OnBoarding")

        val isIconEnabled = pm.getComponentEnabledSetting(iconComponent) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED

        pm.setComponentEnabledSetting(
            iconComponent,
            if (isIconEnabled) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        pm.setComponentEnabledSetting(
            onBoardingComponent,
            if (isIconEnabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}

