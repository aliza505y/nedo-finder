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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SubmitShopActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private var imageUri: Uri? = null

    private val binding: ActivitySubmitShopBinding by lazy {
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

        binding.ownerProfileImage.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.submitShopBtn.setOnClickListener { submitStep3Final() }
    }

    private fun submitStep3Final() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val shopId = intent.getStringExtra("SHOP_ID") ?: run {
            Toast.makeText(this, "Shop id missing", Toast.LENGTH_SHORT).show()
            return
        }

        val ownerName = binding.ownerName.text.toString().trim()
        val shopDescription = binding.shopDescriptio.text.toString().trim()

        var hasError = false
        if (ownerName.isEmpty()) { binding.ownerName.error = "Owner name is required"; hasError = true }
        if (shopDescription.isEmpty()) { binding.shopDescriptio.error = "Shop description is required"; hasError = true }
        if (imageUri == null) {
            Toast.makeText(this, "Please upload owner image", Toast.LENGTH_SHORT).show()
            hasError = true
        }
        if (hasError) return

        val imgRef = storage.reference.child("shops/$uid/$shopId/owner.jpg")
        imgRef.putFile(imageUri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                imgRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val updates = mapOf<String, Any>(
                    "ownerName" to ownerName,
                    "shopDescription" to shopDescription,
                    "ownerImage" to downloadUrl.toString(),
                    "status" to "pending", //ready for admin review
                    "createdAt" to System.currentTimeMillis()
                )

                database.reference.child("pending_shops").child(uid).child(shopId).push()
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Shop submitted for review", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SubmitShopActivity, MainActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        if (it != null) binding.ownerProfileImage.setImageURI(it)
    }
}
