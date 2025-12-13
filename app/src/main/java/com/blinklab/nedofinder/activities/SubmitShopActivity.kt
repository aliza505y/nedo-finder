package com.blinklab.nedofinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivitySubmitShopBinding
import com.blinklab.nedofinder.dataclass.OwnerDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlin.getValue

class SubmitShopActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var database= FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null

    private val binding : ActivitySubmitShopBinding by lazy {
        ActivitySubmitShopBinding.inflate(layoutInflater)
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
        binding.ownerProfileImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.submitShopBtn.setOnClickListener {
            submitShop()
        }

    }


    private fun submitShop(){
        val ownerName = binding.ownerName.text.toString().trim()
        val shopDescription = binding.shopDescriptio.text.toString().trim()
        if (ownerName.isEmpty()) {
            binding.ownerName.error = "Owner name is required"
        }
        if (shopDescription.isEmpty()) {
            binding.shopDescriptio.error = "Shop description is required"
        }
        if (imageUri == null){
            Toast.makeText(this,"Please upload shop image",Toast.LENGTH_SHORT).show()
        }else{
            val fileRef = storage.reference.child("shop_images/${auth.currentUser!!.uid}")
            fileRef.putFile(imageUri!!)
            fileRef.downloadUrl.addOnCompleteListener { url ->
                //upload to realtime database
                val locationUpdates = mapOf<String, Any>(
                    "ownerName" to ownerName,
                    "shopDescription" to shopDescription,
                    "ownerImage" to url.result.toString()
                )
                database.reference.child("pendingShops").child(auth.currentUser!!.uid)
                    .updateChildren(locationUpdates)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Shop added for review", Toast.LENGTH_SHORT)
                            .show()
                    }

            }

        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
       try {
           binding.ownerProfileImage.setImageURI(imageUri)
       } catch (e:Exception){
           e.printStackTrace()
       }
    }

}