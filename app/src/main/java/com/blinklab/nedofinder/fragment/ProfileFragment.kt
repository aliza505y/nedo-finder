package com.blinklab.nedofinder.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.activities.AddShopActivity
import com.blinklab.nedofinder.activities.AuthenticationActivity
import com.blinklab.nedofinder.adapter.ManageShopAdapter
import com.blinklab.nedofinder.databinding.FragmentProfileBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.blinklab.nedofinder.dataclass.ManageDataClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userList: ArrayList<AddShopDataClass>
    private lateinit var userAdapter: ManageShopAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val database = Firebase.database


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        //firebase initialization
        auth = FirebaseAuth.getInstance()
        //google initialization
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        binding.ownerShopsRecyclerView.setHasFixedSize(true)
        userList = ArrayList()
        userAdapter = ManageShopAdapter(userList)
        binding.ownerShopsRecyclerView.adapter = userAdapter

        binding.addNewShopCard.setOnClickListener {
            startActivity(Intent(requireActivity(), AddShopActivity::class.java))
        }

        binding.logOutBtn.setOnClickListener {
            signOut()
        }


        fetchShopsFromDatabase()

        return binding.root

    }

    private fun fetchShopsFromDatabase() {
        database.reference.child("pending_shops").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                        for (shopSnap in snapshot.children) {
                            val shop = shopSnap.getValue(AddShopDataClass::class.java) ?: continue
                            userList.add(shop)
                        }

                    userAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun signOut(){
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireContext(),"Log Out Successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
                requireActivity().finish()
            }else{
                Toast.makeText(requireContext(),"Log Out Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}