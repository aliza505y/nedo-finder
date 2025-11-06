package com.blinklab.nedofinder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.blinklab.nedofinder.adapter.AllShopAdapter
import com.blinklab.nedofinder.dataclass.AllShopDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {
    private lateinit var  binding: FragmentHomeBinding
    private lateinit var arrayList: ArrayList<AllShopDataClass>
    private lateinit var adapter: AllShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        val mySpinner: Spinner = binding.mySpinner
        ArrayAdapter.createFromResource(requireContext(), R.array.spinner_items, R.layout.spinner_font_item)
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_font_item)
                mySpinner.adapter = adapter
            }



        binding.allShopsRecycler.setHasFixedSize(true)
        arrayList = ArrayList()
        adapter = AllShopAdapter(arrayList)
        binding.allShopsRecycler.adapter = adapter
        arrayList.add(AllShopDataClass(R.drawable.store_1,R.drawable.profile_7, "Kiryana Stores", "Grocery", "Street/20km stand","4.5"))
        arrayList.add(AllShopDataClass(R.drawable.store_3,R.drawable.profile_8, "Electric Store", "Electric", "Right Corner/stand","3.0"))
        arrayList.add(AllShopDataClass(R.drawable.store_5,R.drawable.profile_1, "Dollar Shop", "Money Hub", "Street/20km stand","4.4"))
        arrayList.add(AllShopDataClass(R.drawable.store_1,R.drawable.profile_8, "Kiryana Stores", "Category", "Street/20km stand","4.1"))
        arrayList.add(AllShopDataClass(R.drawable.store_3,R.drawable.profile_9, "Electronics", "Category", "Street/20km stand","3.9"))
        arrayList.add(AllShopDataClass(R.drawable.store_3,R.drawable.profile_8, "Electric Store", "Electric", "Right Corner/stand","3.0"))
        arrayList.add(AllShopDataClass(R.drawable.store_5,R.drawable.profile_1, "Dollar Shop", "Money Hub", "Street/20km stand","3.5"))
        arrayList.add(AllShopDataClass(R.drawable.store_1,R.drawable.profile_8, "Kiryana Stores", "Category", "Street/20km stand","3.3"))
        arrayList.add(AllShopDataClass(R.drawable.store_3,R.drawable.profile_9, "Electronics", "Category", "Street/20km stand","5.0"))


        return binding.root
    }

}