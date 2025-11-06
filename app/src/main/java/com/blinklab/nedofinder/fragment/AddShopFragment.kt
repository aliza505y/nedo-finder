package com.blinklab.nedofinder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinklab.nedofinder.fragment.AddLocationFragment
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentAddShopBinding


class AddShopFragment : Fragment() {
    private lateinit var binding: FragmentAddShopBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddShopBinding.inflate(inflater, container, false)
        binding.proceddToLocation.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddLocationFragment())
                .addToBackStack(null)
                .commit()
        }



        return binding.root

    }

}