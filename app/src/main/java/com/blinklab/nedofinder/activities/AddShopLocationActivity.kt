package com.blinklab.nedofinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivityAddShopLocationBinding
import com.blinklab.nedofinder.dataclass.AddLocationDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddShopLocationActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var database= FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private val binding : ActivityAddShopLocationBinding by lazy {
        ActivityAddShopLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.proceedFinalizeBtn.setOnClickListener {
            submitLocation()
        }

    }
    private fun submitLocation(){
        val address = binding.streetAddress.text.toString().trim()
        val city = binding.cityArea.text.toString().trim()
        val phone = binding.shopPhone.text.toString().trim()
        if (address.isEmpty()) {
            binding.streetAddress.error = "Address is required"
        }
        if (city.isEmpty()) {
            binding.cityArea.error = "City is required"
        }
        if (phone.isEmpty()) {
            binding.shopPhone.error = "Phone number is required"
        }
        else {
            // Create a map of the new data to be updated
            val locationUpdates = mapOf<String, Any>(
                "address" to address,
                "city" to city,
                "phone" to phone
            )
            database.reference.child("pending Shops").child(auth.currentUser!!.uid)
                .updateChildren(locationUpdates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Location added successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SubmitShopActivity::class.java))

                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }


        }

    }

}