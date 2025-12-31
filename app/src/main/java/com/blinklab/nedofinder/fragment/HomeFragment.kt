package com.blinklab.nedofinder.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.blinklab.nedofinder.adapter.AllShopAdapter
import com.blinklab.nedofinder.databinding.FragmentHomeBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var arrayList: ArrayList<AddShopDataClass>      // full list
    private lateinit var filteredList: ArrayList<AddShopDataClass>   // shown list
    private lateinit var adapter: AllShopAdapter
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.allShopsRecycler.setHasFixedSize(true)

        arrayList = ArrayList()
        filteredList = ArrayList()

        adapter = AllShopAdapter(filteredList)   
        binding.allShopsRecycler.adapter = adapter

        setupSearchView()
        fetchShopsFromDatabase()

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    private fun setupSearchView() {
        val searchView = binding.searchView1
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.clearFocus()

        val searchEditText = searchView.findViewById<SearchView.SearchAutoComplete>(
            androidx.appcompat.R.id.search_src_text
        )
        searchEditText.hint = "Search shops, cities..."
        searchEditText.isCursorVisible = true
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterShops(newText.orEmpty())
                return true
            }
        })
    }

    private fun filterShops(query: String) {
        val q = query.trim().lowercase()

        filteredList.clear()

        if (q.isEmpty()) {
            filteredList.addAll(arrayList)
        } else {
            for (shop in arrayList) {
                val shopName = shop.shopName.orEmpty().lowercase()
                val category = shop.category.orEmpty().lowercase()
                val city = shop.city.orEmpty().lowercase()
                val address = shop.address.orEmpty().lowercase()

                if (
                    shopName.contains(q) ||
                    category.contains(q) ||
                    city.contains(q) ||
                    address.contains(q)
                ) {
                    filteredList.add(shop)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun fetchShopsFromDatabase() {
        database.reference.child("approved_shops")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayList.clear()

                    for (uidSnap in snapshot.children) {
                        val uidKey = uidSnap.key ?: continue

                        for (shopSnap in uidSnap.children) {
                            val shop = shopSnap.getValue(AddShopDataClass::class.java) ?: continue
                            shop._uidKey = uidKey
                            shop._shopKey = shopSnap.key
                            arrayList.add(shop)
                        }
                    }

                    filteredList.clear()
                    filteredList.addAll(arrayList)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
