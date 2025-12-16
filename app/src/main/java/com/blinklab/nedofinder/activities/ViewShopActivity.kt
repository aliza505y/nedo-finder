package com.blinklab.nedofinder.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivityViewShopBinding
import com.blinklab.nedofinder.dataclass.ShopDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewShopBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var shopId: String
    private var currentShop: ShopDataClass? = null
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

        shopId = intent.getStringExtra("SHOP_ID").orEmpty()
        if (shopId.isBlank()) {
            Toast.makeText(this, "Shop id missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.fvrtBtn.isEnabled = false

        fetchShopDetailsFromApproved()

       
    }

    private fun fetchShopDetailsFromApproved() {
        database.child("approved_shops").child(shopId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val shop = snapshot.getValue(ShopDataClass::class.java)
                    if (shop == null) {
                        Toast.makeText(this@ViewShopActivity, "Shop not found", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }

                    currentShop = shop
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
                    binding.fvrtBtn.isEnabled = true
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.fvrtBtn.isEnabled = true
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
            binding.fvrtBtn.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.fvrtBtn.setImageResource(R.drawable.ic_heart_outline)
        }
    }
}
