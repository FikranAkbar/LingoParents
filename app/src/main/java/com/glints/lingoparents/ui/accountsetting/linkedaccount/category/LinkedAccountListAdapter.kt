package com.glints.lingoparents.ui.accountsetting.linkedaccount.category

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.LinkedAccountsResponse
import com.glints.lingoparents.databinding.ItemLinkedAccountBinding

class LinkedAccountListAdapter(
    private val listener: OnItemClickCallback,
    private val category: String
) : RecyclerView.Adapter<LinkedAccountListAdapter.CustomViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LinkedAccountsResponse.ChildrenData>() {
        override fun areItemsTheSame(
            oldItem: LinkedAccountsResponse.ChildrenData,
            newItem: LinkedAccountsResponse.ChildrenData,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LinkedAccountsResponse.ChildrenData,
            newItem: LinkedAccountsResponse.ChildrenData,
        ): Boolean {
            return oldItem.Master_student.fullname == newItem.Master_student.fullname
        }

    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    inner class CustomViewHolder(
        private val itemLinkedAccountBinding: ItemLinkedAccountBinding,
    ) : RecyclerView.ViewHolder(itemLinkedAccountBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            holder: CustomViewHolder,
            item: LinkedAccountsResponse.ChildrenData
        ) {
            itemLinkedAccountBinding.apply {
                tvChildName.text = item.Master_student.fullname
                tvChildAge.text = "${item.Master_student.age} years old"

                ivProfilePicture.load(
                    item.Master_student.photo
                )

                if (category == LinkedAccountListFragment.INVITED_TYPE) {
                    mbtnCancel.visibility = View.VISIBLE
                    mbtnAccept.visibility = View.INVISIBLE
                    mbtnDecline.visibility = View.INVISIBLE
                } else if (category == LinkedAccountListFragment.REQUESTED_TYPE) {
                    mbtnCancel.visibility = View.INVISIBLE
                    mbtnAccept.visibility = View.VISIBLE
                    mbtnDecline.visibility = View.VISIBLE
                }

                mbtnAccept.setOnClickListener {
                    listener.onAcceptClicked(item)
                }

                mbtnDecline.setOnClickListener {
                    listener.onDeclineClicked(item)
                }

                mbtnCancel.setOnClickListener {
                    listener.onCancelClicked(item)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onCancelClicked(item: LinkedAccountsResponse.ChildrenData)
        fun onAcceptClicked(item: LinkedAccountsResponse.ChildrenData)
        fun onDeclineClicked(item: LinkedAccountsResponse.ChildrenData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemLinkedAccountBinding =
            ItemLinkedAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemLinkedAccountBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(holder, differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<LinkedAccountsResponse.ChildrenData>) {
        differ.submitList(list)
    }
}