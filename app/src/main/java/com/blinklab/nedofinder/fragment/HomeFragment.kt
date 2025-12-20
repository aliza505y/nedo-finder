package com.blinklab.nedofinder.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.blinklab.nedofinder.adapter.AllShopAdapter
import com.blinklab.nedofinder.dataclass.AllShopDataClass
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.FragmentHomeBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var  binding: FragmentHomeBinding
    private lateinit var arrayList: ArrayList<AddShopDataClass>
    private lateinit var adapter: AllShopAdapter
    private val database =  FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        setupSearchView()

        binding.allShopsRecycler.setHasFixedSize(true)
        arrayList = ArrayList()
        adapter = AllShopAdapter(arrayList)
        binding.allShopsRecycler.adapter = adapter
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
        searchEditText.hint = "Search Shops in city..."
        searchEditText.isCursorVisible = true

        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)

        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })
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
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}