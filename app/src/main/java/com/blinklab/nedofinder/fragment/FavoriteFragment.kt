package com.blinklab.nedofinder.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blinklab.nedofinder.adapter.FavAdapter
import com.blinklab.nedofinder.dataclass.FavDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentFavoriteBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favList = ArrayList<AddShopDataClass>()
    private lateinit var adapter: FavAdapter
    private val  database = FirebaseDatabase.getInstance()
    private  val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        binding.favoriteRecycler.setHasFixedSize(true)
        adapter = FavAdapter(favList)
        binding.favoriteRecycler.adapter = adapter

        fetchFavShop()

        return binding.root
    }

    private fun fetchFavShop(){
        database.reference.child("favourite_shops").child(auth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    favList.clear()
                    for (snap in snapshot.children){
                        val favShop = snap.getValue(AddShopDataClass::class.java)
                        favList.add(favShop!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No shop in you fav gallery", Toast.LENGTH_SHORT).show()
                }

            })
    }
}