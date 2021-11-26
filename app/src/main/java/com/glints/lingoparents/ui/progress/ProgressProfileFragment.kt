package com.glints.lingoparents.ui.progress

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

class ProgressProfileFragment : Fragment(R.layout.fragment_progress_profile) {
    private lateinit var badgeCharacterDialog: Dialog
    private var _binding: FragmentProgressProfileBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressProfileBinding.bind(view)
        binding.ivCharacterBadge.setOnClickListener {
            showDialog()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showDialog() {
        val bind: ItemPopupCharacterBinding = ItemPopupCharacterBinding.inflate(layoutInflater)

        val dialog = Dialog(activity as AppCompatActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_popup_character)
//        dialog.setContentView(bind.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog.window!!.setLayout(1000,1500)
        val closeIcon = dialog.findViewById(R.id.ivClose) as ImageView
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }

//        bind.ivCharacter.setOnClickListener {
//            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
//        }
        dialog.show()

    }
}