package com.stockmaster.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.stockmaster.app.databinding.FragmentPlaceholderBinding

class PlaceholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val b = FragmentPlaceholderBinding.inflate(inflater, container, false)
        b.title.text = arguments?.getString("title") ?: ""
        b.subtitle.text = "This module arrives in Part ${arguments?.getInt("part", 3)} of the build — keep going! 🚀"
        return b.root
    }

    companion object {
        fun newInstance(title: String, part: Int) = PlaceholderFragment().apply {
            arguments = bundleOf("title" to title, "part" to part)
        }
    }
}
