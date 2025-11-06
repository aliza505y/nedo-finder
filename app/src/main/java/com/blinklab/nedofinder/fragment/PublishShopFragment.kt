package com.blinklab.nedofinder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentPublishShopBinding


class PublishShopFragment : Fragment() {
    private lateinit var binding: FragmentPublishShopBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublishShopBinding.inflate(inflater, container, false)

        binding.backButtonPublish.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }
}