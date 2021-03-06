package com.glints.lingoparents.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.StudentListResponse
import com.glints.lingoparents.databinding.ItemDashboardChildrenBinding

class ChildrenAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<ChildrenAdapter.CustomViewHolder>() {
    private val children = ArrayList<StudentListResponse.DataItem>()

    inner class CustomViewHolder(private val itemDashboardChildrenBinding: ItemDashboardChildrenBinding) :
        RecyclerView.ViewHolder(itemDashboardChildrenBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(holder: CustomViewHolder, children: StudentListResponse.DataItem) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(children)
            }
            itemDashboardChildrenBinding.apply {
                ivChildren.load(children.photo)
                tvChildrenName.text = children.name
                tvChildrenAge.text = "(${children.age} years old)"
                tvChildrenRelationship.text = children.relationship
                if (children.level == null) {
                    tvChildrenLevel.text = "No Level"
                } else {
                    tvChildrenLevel.text = children.level
                }
                if (children.sublevel == null) {
                    tvChildrenSublevel.text = "No Sublevel"

                } else {
                    tvChildrenSublevel.text = children.sublevel

                }
            }
        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(children: StudentListResponse.DataItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemDashboardChildrenBinding =
            ItemDashboardChildrenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemDashboardChildrenBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val childrenItem = children[position]
        holder.bind(holder, childrenItem)
    }

    override fun getItemCount(): Int = children.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<StudentListResponse.DataItem>) {
        children.clear()
        children.addAll(list)
        notifyDataSetChanged()
    }
}

