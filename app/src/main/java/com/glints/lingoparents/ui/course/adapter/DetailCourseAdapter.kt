package com.glints.lingoparents.ui.course.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.TrxCourseCardsItem
import com.glints.lingoparents.databinding.ItemCourseDetailBinding

class DetailCourseAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<DetailCourseAdapter.CustomViewHolder>() {

    private val courseDetailList = ArrayList<TrxCourseCardsItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<TrxCourseCardsItem>) {
        courseDetailList.clear()
        courseDetailList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(private val itemCourseDetailBinding: ItemCourseDetailBinding) :
        RecyclerView.ViewHolder(itemCourseDetailBinding.root) {
        fun bind(
            holder: CustomViewHolder,
            item: TrxCourseCardsItem
        ) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }

            itemCourseDetailBinding.apply {
//                courseTitle.text = item.title
//                courseImage.load(item.cover_flag)
                tvCourse.text = item.description
                ivCourseCard.load(item.cardPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemCourseDetailBinding =
            ItemCourseDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemCourseDetailBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val courseDetailItem = courseDetailList[position]
        holder.bind(holder, courseDetailItem)
    }

    override fun getItemCount(): Int = courseDetailList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: TrxCourseCardsItem)
    }
}