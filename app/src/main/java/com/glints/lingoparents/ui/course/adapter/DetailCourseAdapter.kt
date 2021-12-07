package com.glints.lingoparents.ui.course.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.TrxCourseCardsItem
import com.glints.lingoparents.databinding.ItemCourseDetailBinding
import com.glints.lingoparents.databinding.ItemCourseDetailSecondBinding


class DetailCourseAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val courseDetailList = ArrayList<TrxCourseCardsItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<TrxCourseCardsItem>) {
        courseDetailList.clear()
        courseDetailList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = courseDetailList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: TrxCourseCardsItem)
    }

    val leftView: Int = 1
    val rightView: Int = 2
    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0) {
            //postView
            return leftView
        } else {
            //commentView
            return rightView
        }
    }

    inner class LeftViewType(
        private val itemCourseDetailBinding: ItemCourseDetailBinding,
    ) : RecyclerView.ViewHolder(itemCourseDetailBinding.root) {
        fun bind(
            holder: LeftViewType,
            item: TrxCourseCardsItem
        ) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
            itemCourseDetailBinding.apply {
                tvCourse.text = item.description
                ivCourseCard.load(item.cardPhoto)
            }
        }
    }

    inner class RightViewType(
        private val itemCourseDetailSecondBinding: ItemCourseDetailSecondBinding,
    ) : RecyclerView.ViewHolder(itemCourseDetailSecondBinding.root) {
        fun bind(
            holder: RightViewType,
            item: TrxCourseCardsItem
        ) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
            itemCourseDetailSecondBinding.apply {
                tvCourse.text = item.description
                ivCourseCard.load(item.cardPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == leftView) {
            return LeftViewType(
                ItemCourseDetailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return RightViewType(
                ItemCourseDetailSecondBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val courseDetailItem = courseDetailList[position]
        if (holder is LeftViewType) {
            holder.bind(holder, courseDetailItem)
        } else {
            (holder as RightViewType).bind(holder, courseDetailItem)
        }
    }


}