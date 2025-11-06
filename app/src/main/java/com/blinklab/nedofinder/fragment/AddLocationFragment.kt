package com.blinklab.nedofinder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentAddLocationBinding

class AddLocationFragment : Fragment() {
    private lateinit var binding: FragmentAddLocationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddLocationBinding.inflate(inflater, container, false)

        binding.backButtonLocate.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.proceddFinalizeBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, PublishShopFragment())
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }
}