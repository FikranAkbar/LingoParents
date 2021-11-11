package com.glints.lingoparents.ui.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentDetailCourseBinding

class DetailCourseFragment : Fragment(R.layout.fragment_detail_course) {

    private var binding: FragmentDetailCourseBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailCourseBinding.bind(view)
        val dataCourse = DetailCourseFragmentArgs.fromBundle(arguments as Bundle).course
        binding!!.tvCourse.text = dataCourse.name
        binding!!.tvCourse1.text = dataCourse.desc1
        binding!!.tvCourse2.text = dataCourse.desc2
        binding!!.tvCourse3.text = dataCourse.desc3
        binding!!.ivCourseCard1.setImageResource(dataCourse.card1)
        binding!!.ivCourseCard2.setImageResource(dataCourse.card2)
        binding!!.ivCourseCard3.setImageResource(dataCourse.card3)
        binding!!.cvBackButton.setOnClickListener {
            //findNavController().popBackStack()
            findNavController().navigate(DetailCourseFragmentDirections.actionDetailCourseFragmentToAllCoursesFragment())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}