package com.blinklab.nedofinder.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.databinding.ActivityAddShopLocationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddShopLocationActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }

    private val binding: ActivityAddShopLocationBinding by lazy {
        ActivityAddShopLocationBinding.inflate(layoutInflater)
    }
    private lateinit var shopId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.proceedFinalizeBtn.setOnClickListener { submitLocationStep2() }

         shopId = intent.getStringExtra("SHOP_ID") ?: run {
             Toast.makeText(this, "Shop id missing", Toast.LENGTH_SHORT).show()
             return
         }

        binding.pickLocationBtn.setOnClickListener {
            startActivity(
                Intent(this, PickLocationActivity::class.java)
                    .putExtra("SHOP_ID", shopId)
            )

        }


    }


    private fun submitLocationStep2() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val address = binding.streetAddress.text.toString().trim()
        val city = binding.cityArea.text.toString().trim()
        val phone = binding.shopPhone.text.toString().trim()

        var hasError = false
        if (address.isEmpty()) { binding.streetAddress.error = "Address is required"; hasError = true }
        if (city.isEmpty()) { binding.cityArea.error = "City is required"; hasError = true }
        if (phone.isEmpty()) { binding.shopPhone.error = "Phone number is required"; hasError = true }
        if (hasError) return

        val updates = mapOf<String, Any>(
            "address" to address,
            "city" to city,
            "phone" to phone
        )

        database.reference.child("pending_shops").child(uid).child(shopId).push()
            .updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Step 2 saved", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(this, SubmitShopActivity::class.java)
                        .putExtra("SHOP_ID", shopId)
                )
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}
