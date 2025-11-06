package com.blinklab.nedofinder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinklab.nedofinder.adapter.FavAdapter
import com.blinklab.nedofinder.dataclass.FavDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favList = ArrayList<FavDataClass>()
    private lateinit var adapter: FavAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        binding.favoriteRecycler.setHasFixedSize(true)
        adapter = FavAdapter(favList)
        binding.favoriteRecycler.adapter = adapter
        favList.add(FavDataClass(R.drawable.store_1, R.drawable.profile_8,"Khizar Hayat","Grocery","4.6"))
        favList.add(FavDataClass(R.drawable.store_3, R.drawable.profile_8,"Umar Akhbar","Electric","4.7"))
        favList.add(FavDataClass(R.drawable.store_5, R.drawable.profile_7,"Ali Iqbal","Dollar","3.9"))
        favList.add(FavDataClass(R.drawable.store_3, R.drawable.profile_9,"Malik Nawaz","Electric","3.5"))
        favList.add(FavDataClass(R.drawable.store_5, R.drawable.profile_2,"Bahadur Khan","Monney","5.0"))
        favList.add(FavDataClass(R.drawable.store_5, R.drawable.profile_4,"Khizar Hayat","Grocery","4.6"))
        favList.add(FavDataClass(R.drawable.store_3, R.drawable.profile_8,"Khizar Hayat","Grocery","4.5"))
        favList.add(FavDataClass(R.drawable.store_1, R.drawable.profile_8,"Khizar Hayat","Grocery","4.2"))
        return binding.root
    }
}