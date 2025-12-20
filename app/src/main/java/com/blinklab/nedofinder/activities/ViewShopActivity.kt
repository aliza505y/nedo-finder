package com.blinklab.nedofinder.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil3.Uri
import coil3.load
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivityViewShopBinding
import com.blinklab.nedofinder.dataclass.AddShopDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewShopBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var shopId: String
    private lateinit var uid: String
    private lateinit var phoneNumber: String
    private var currentShop: AddShopDataClass? = null
    private var isFavourite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        shopId = intent.getStringExtra("shopId").orEmpty()
        uid = intent.getStringExtra("uid").orEmpty()

        if (shopId.isBlank()) {
            Toast.makeText(this, "Shop id missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.fvShopBtn.isEnabled = false
        binding.fvShopBtn.setOnClickListener {
            favouriteShops()

        }


                binding.callBtn.setOnClickListener { openDialer(phoneNumber) }

        fetchShopDetailsFromApproved()

       
    }

    private fun openDialer(rawNumber: String) {
    val cleaned = rawNumber.trim()
        .replace("\\s+".toRegex(), "")
        .replace("[^0-9+]".toRegex(), "")

    if (cleaned.isBlank()) {
        Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = Uri.parse("tel:$cleaned")
    val intent = Intent(Intent.ACTION_DIAL, uri)

    try {
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "Dialer not found on device", Toast.LENGTH_SHORT).show()
    }
}



    private fun fetchShopDetailsFromApproved() {
        database.child("approved_shops").child(uid).child(shopId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val shop = snapshot.getValue(AddShopDataClass::class.java)
                    if (shop == null) {
                        Toast.makeText(this@ViewShopActivity, "Shop not found", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    binding.viewShopStore.load(shop.shopImage)
                    binding.viewShopProfileImage.load(shop.ownerImage)
                    binding.viewShopName.text= shop.shopName
                    binding.allStoreCategory.text = shop.category
                    binding.shopDescription.text = shop.shopDescription
                    binding.ownerName.text= shop.ownerName
                    binding.contact.text = shop.phone
                    binding.address.text = shop.address
                    phoneNumber=shop.phone!!

                    currentShop = shop
                     binding.callBtn.isEnabled = phoneNumber.isNotBlank()
                    checkFavouriteStatus()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ViewShopActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkFavouriteStatus() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("favourite_shops").child(uid).child(shopId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFavourite = snapshot.exists()
                    updateHeartIcon()
                    binding.fvShopBtn.isEnabled = true
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.fvShopBtn.isEnabled = true
                }
            })
    }

    private fun favouriteShops() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val favRef = database.child("favourite_shops").child(uid).child(shopId)

        if (isFavourite) {
            favRef.removeValue()
                .addOnSuccessListener {
                    isFavourite = false
                    updateHeartIcon()
                    Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            val shop = currentShop ?: run {
                Toast.makeText(this, "Shop data not loaded yet", Toast.LENGTH_SHORT).show()
                return
            }

            val favouriteData = hashMapOf<String, Any?>(
                "shopId" to shop.shopId,
                "ownerId" to shop.ownerId,

                "shopName" to shop.shopName,
                "category" to shop.category,
                "shopImage" to shop.shopImage,

                "address" to shop.address,
                "city" to shop.city,
                "phone" to shop.phone,

                "ownerName" to shop.ownerName,
                "shopDescription" to shop.shopDescription,
                "ownerImage" to shop.ownerImage,

                "status" to shop.status,
                "createdAt" to shop.createdAt,
                "addedAt" to System.currentTimeMillis()
            )

            favRef.setValue(favouriteData)
                .addOnSuccessListener {
                    isFavourite = true
                    updateHeartIcon()
                    Toast.makeText(this, "Added to favourites ❤️", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateHeartIcon() {
        if (isFavourite) {
            binding.fvShopBtn.setImageResource(R.drawable.ic_favourite_filled)
        } else {
            binding.fvShopBtn.setImageResource(R.drawable.outline_favorite_24)
        }
    }
}
