package com.glints.lingoparents.ui.course.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.databinding.ItemCourseBinding

class CourseAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CourseAdapter.CustomViewHolder>() {

    private val courseList = ArrayList<AllCoursesResponse.CourseItemResponse>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllCoursesResponse.CourseItemResponse>) {
        courseList.clear()
        courseList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(private val itemCourseBinding: ItemCourseBinding) :
        RecyclerView.ViewHolder(itemCourseBinding.root) {
        fun bind(
            holder: CustomViewHolder,
            item: AllCoursesResponse.CourseItemResponse
        ) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }

            itemCourseBinding.apply {
                courseTitle.text = item.title
                courseImage.load(item.cover_flag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemCourseBinding =
            ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemCourseBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val courseItem = courseList[position]
        holder.bind(holder, courseItem)
    }

    override fun getItemCount(): Int = courseList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: AllCoursesResponse.CourseItemResponse)
    }
}