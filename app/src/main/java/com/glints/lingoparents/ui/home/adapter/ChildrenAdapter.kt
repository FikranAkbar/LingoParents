package com.glints.lingoparents.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.databinding.ItemDashboardChildrenBinding

class ChildrenAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<ChildrenAdapter.CustomViewHolder>() {
    private val children = ArrayList<DataItem>()

    inner class CustomViewHolder(private val itemDashboardChildrenBinding: ItemDashboardChildrenBinding) :
        RecyclerView.ViewHolder(itemDashboardChildrenBinding.root) {
        fun bind(holder: CustomViewHolder, children: DataItem) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(children)
            }
            itemDashboardChildrenBinding.apply {
                //Glide.with(holder.itemView.context).load(R.drawable.ic_user_avatar_female_square).into(ivChildren)
                //Glide.with(holder.itemView.context).load(children.photo).into(ivChildren)
                ivChildren.load(children.photo)
                tvChildrenName.text = children.name
                tvChildrenAge.text = children.age.toString()
                tvChildrenRelationship.text = children.relationship
                if (children.level == null || children.sublevel == null) {
                    tvChildrenLevel.text = "not taking any courses"
                } else {
                    tvChildrenLevel.text = "${children.level} - ${children.sublevel}"
                }
            }
        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(children: DataItem)
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
    fun submitList(list: List<DataItem>) {
        children.clear()
        children.addAll(list)
        notifyDataSetChanged()
    }
}

//class ChildrenAdapter(private val listener: OnItemClickCallback) : RecyclerView.Adapter<ChildrenAdapter.CustomViewHolder>() {
//    private val children = ArrayList<ChildrenItem>()
//
//    inner class CustomViewHolder(private val itemDashboardChildrenBinding: ItemDashboardChildrenBinding) :
//        RecyclerView.ViewHolder(itemDashboardChildrenBinding.root) {
//        fun bind(holder: CustomViewHolder, children: ChildrenItem) {
//            itemDashboardChildrenBinding.apply {
//                Glide.with(holder.itemView.context).load(R.drawable.ic_user_avatar_female_square).into(ivChildren)
//                tvChildrenName.text = "Jane Doe"
//                tvChildrenAge.text = "(16 years old)"
//                tvChildrenRelationship.text = "Relationship : Guardian"
//                tvChildrenLevelSublevel.text = "Beginner - Sublevel 2"
//            }
//        }
//    }
//
//
//    interface OnItemClickCallback {
//        fun onItemClicked(children: ChildrenItem)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val itemDashboardChildrenBinding =
//            ItemDashboardChildrenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return CustomViewHolder(itemDashboardChildrenBinding)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val childrenItem = children[position]
//        holder.bind(holder, childrenItem)
//    }
//
//    override fun getItemCount(): Int = children.size
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(list: List<ChildrenItem>) {
//        children.clear()
//        children.addAll(list)
//        notifyDataSetChanged()
//    }
//}