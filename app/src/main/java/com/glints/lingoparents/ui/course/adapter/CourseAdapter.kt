package com.glints.lingoparents.ui.course.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.databinding.ItemCourseBinding

class CourseAdapter(private val listCourse: ArrayList<CourseItem>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(course: CourseItem)
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: CourseItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.image)
                    //.apply(RequestOptions().override(250, 250))
                    .into(courseImage)
                courseTitle.text = user.name

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(listCourse[position])
    }

    override fun getItemCount(): Int = listCourse.size

}