package com.glints.lingoparents.ui.course.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.databinding.ItemCourseBinding

//class CourseAdapter(private val listCourse: ArrayList<CourseItem>) :
//    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
//    private lateinit var onItemClickCallback: OnItemClickCallback
//    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback
//    }
//
//    interface OnItemClickCallback {
//        fun onItemClicked(course: CourseItem)
//    }
//
//    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(user: CourseItem) {
//            with(binding) {
//                Glide.with(itemView.context)
//                    .load(user.image)
//                    //.apply(RequestOptions().override(250, 250))
//                    .into(courseImage)
//                courseTitle.text = user.name
//
//                itemView.setOnClickListener {
//                    onItemClickCallback?.onItemClicked(user)
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
//        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return CourseViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
//        holder.bind(listCourse[position])
//    }
//
//    override fun getItemCount(): Int = listCourse.size
//
//}

class CourseAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CourseAdapter.CustomViewHolder>() {

    private val liveCourseList = ArrayList<AllCoursesResponse.CourseItemResponse>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllCoursesResponse.CourseItemResponse>) {
        liveCourseList.clear()
        liveCourseList.addAll(list)
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
                //atau pakai glide
                courseImage.load(item.cover_flag)
//                tvLiveEventTitle.text = item.title
//                tvLiveEventDate.text = item.date
//
//                ivImage.load(item.speaker_photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemCourseBinding =
            ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemCourseBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val courseItem = liveCourseList[position]
        holder.bind(holder, courseItem)
    }

    override fun getItemCount(): Int = liveCourseList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: AllCoursesResponse.CourseItemResponse)
    }
}