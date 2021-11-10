package com.glints.lingoparents.ui.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChildrenCoursesBinding

class ChildrenCoursesFragment : Fragment(R.layout.fragment_children_courses) {
    private var binding: FragmentChildrenCoursesBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChildrenCoursesBinding.bind(view)
        //binding!!.iddarixml
        //binding!!.textView2.setText("hahahaha")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}