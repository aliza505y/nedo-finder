package com.blinklab.nedofinder.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.activities.AddShopActivity
import com.blinklab.nedofinder.adapter.ManageShopAdapter
import com.blinklab.nedofinder.databinding.FragmentProfileBinding
import com.blinklab.nedofinder.dataclass.ManageDataClass


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var arrayList: ArrayList<ManageDataClass>
    private lateinit var adapter: ManageShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.ownerShopsRecyclerView.setHasFixedSize(true)
        arrayList = ArrayList()
        adapter = ManageShopAdapter(arrayList)
        binding.ownerShopsRecyclerView.adapter = adapter
        arrayList.add(ManageDataClass(R.drawable.store_3,"Electric Store","Electronics"  ))
        arrayList.add(ManageDataClass(R.drawable.store_3,"Kiryana Stores","Category"  ))
        arrayList.add(ManageDataClass(R.drawable.store_3,"Kiryana Stores","Category"  ))

        binding.addNewShopCard.setOnClickListener {
            startActivity(Intent(requireActivity(), AddShopActivity::class.java))
        }

        return binding.root

    }
}