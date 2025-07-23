package com.example.digitalsafety_child.permissionUtils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.constants.Constants
import com.example.digitalsafety_child.model.PermissionOverview
import com.google.android.material.textview.MaterialTextView


class PermissionOverviewAdapter :
    RecyclerView.Adapter<PermissionOverviewAdapter.Holder>() {

    private var permissionOverviewList: List<PermissionOverview> = ArrayList()
    var onClickListener: ((permissionOverView: PermissionOverview) -> Unit)? = null
    private var isDisplayPopUp = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.permission_overview_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentItem = permissionOverviewList[position]
        if(currentItem.id == Constants.PermissionId.LOCATION){

        }
        isDisplayPopUp = currentItem.id == Constants.PermissionId.DISPLAY_POP_UP_IN_BACKGROUND
        if(currentItem.imageResource != -1) {
            holder.logo.setImageResource(currentItem.imageResource)
        }else{
            holder.logo.visibility = View.GONE
        }
        holder.heading.text = currentItem.name
        holder.description.text = currentItem.description

        holder.itemView.setOnClickListener {
            onClickListener?.invoke(currentItem)
        }


        if (position == permissionOverviewList.indexOf(permissionOverviewList[permissionOverviewList.size - 1]) || currentItem.id == Constants.PermissionId.SPECIAL_XIAOMI_PERMISSION) {
            holder.divider.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return permissionOverviewList.size
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.permissionLogo)
        val heading: MaterialTextView = itemView.findViewById(R.id.permissionOverviewHeading)
        val description: MaterialTextView =
            itemView.findViewById(R.id.permissionOverviewDescription)
        val divider: View = itemView.findViewById(R.id.divider)
    }

    fun setListOfPermissionOverview(permissionOverviewList: List<PermissionOverview>) {
        this.permissionOverviewList = permissionOverviewList
    }
}