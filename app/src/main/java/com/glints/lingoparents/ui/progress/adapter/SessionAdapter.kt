package com.glints.lingoparents.ui.progress.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.data.model.SessionItem
import com.glints.lingoparents.databinding.ItemSessionBinding

class SessionAdapter(private val listSession: ArrayList<SessionItem>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(session: SessionItem)
    }

    inner class SessionViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: SessionItem) {
            with(binding) {

                tvSession.text = session.session
                tvShortdesc.text = session.shortDesc
                tvSessiondesc.text = session.desc
                tvSessionscore.text = session.score

                itemView.setOnClickListener {
                    onItemClickCallback?.onItemClicked(session)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(listSession[position])
    }

    override fun getItemCount(): Int = listSession.size

}