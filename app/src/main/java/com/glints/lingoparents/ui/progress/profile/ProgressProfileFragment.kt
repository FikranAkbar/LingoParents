package com.glints.lingoparents.ui.progress.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressProfileBinding
import com.glints.lingoparents.databinding.ItemDashboardChildrenBinding
import com.glints.lingoparents.databinding.ItemInsightBinding
import com.glints.lingoparents.databinding.ItemPopupCharacterBinding
import com.glints.lingoparents.ui.progress.ProgressViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProgressProfileFragment : Fragment(R.layout.fragment_progress_profile) {
    private var _binding: FragmentProgressProfileBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressProfileBinding.bind(view)
        binding.ivCharacterBadge.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(activity as AppCompatActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_popup_character)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val closeIcon = dialog.findViewById(R.id.ivClose) as ImageView
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Subscribe
    fun collectStudentIdEvent(event: ProgressViewModel.EventBusAction.SendStudentId) {
        Log.d("EventCollected", event.studentId.toString())
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            when(b) {
                true -> {
                    shimmerLayout.visibility = View.VISIBLE
                    mainContent.visibility = View.GONE
                }
                false -> {
                    shimmerLayout.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}