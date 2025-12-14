package com.blinklab.nedofinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivityAddShopBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddShopActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var database= FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private val binding : ActivityAddShopBinding by lazy {
        ActivityAddShopBinding.inflate(layoutInflater)
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

        binding.shopImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.proceedToNextBtn.setOnClickListener {
            submitShop()
        }


        val shopTypes = resources.getStringArray(R.array.shop_categories).toList()
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shopTypes) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item (placeholder)
                return position != 0
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(ContextCompat.getColor(this@AddShopActivity, android.R.color.darker_gray))
                } else {
                    view.setTextColor(ContextCompat.getColor(this@AddShopActivity, android.R.color.black))
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMealType.adapter = adapter

    }

    private fun submitShop() {
        val shopName = binding.shopName.text.toString().trim()
        val selectedCategory = if (binding.spinnerMealType.selectedItemPosition > 0) {
            binding.spinnerMealType.selectedItem.toString()
        } else {
            ""
        }
        if (shopName.isEmpty()) {
            binding.shopName.error = "Shop name is required"
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
        }
        if (imageUri == null) {
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show()
        } else {
            val fileRef = storage.reference.child("shop Images/${auth.currentUser!!.uid}")
            fileRef.putFile(imageUri!!)
            fileRef.downloadUrl.addOnCompleteListener { url ->
                //upload to realtime database
                val shopModel = AddShopDataClass(shopName, selectedCategory, url.result.toString())
                database.reference.child("pending Shops").child(auth.currentUser!!.uid).setValue(shopModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Shop added successfully", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, AddShopLocationActivity::class.java))


                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        try {
            binding.shopImage.setImageURI(imageUri)
        } catch (e:Exception){
            e.printStackTrace()
        }
    }


}