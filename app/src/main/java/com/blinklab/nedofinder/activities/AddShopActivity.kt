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

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private var imageUri: Uri? = null

    private val binding: ActivityAddShopBinding by lazy {
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

        binding.shopImage.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.proceedToNextBtn.setOnClickListener { submitShopStep1() }

        val shopTypes = resources.getStringArray(R.array.shop_categories).toList()
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shopTypes) {
            override fun isEnabled(position: Int): Boolean = position != 0
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(
                    ContextCompat.getColor(
                        this@AddShopActivity,
                        if (position == 0) android.R.color.darker_gray else android.R.color.black
                    )
                )
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMealType.adapter = adapter
    }

    private fun submitShopStep1() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val shopName = binding.shopName.text.toString().trim()
        val selectedCategory = if (binding.spinnerMealType.selectedItemPosition > 0)
            binding.spinnerMealType.selectedItem.toString()
        else ""

        var hasError = false
        if (shopName.isEmpty()) {
            binding.shopName.error = "Shop name is required"
            hasError = true
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            hasError = true
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select shop image", Toast.LENGTH_SHORT).show()
            hasError = true
        }
        if (hasError) return

        //Create unique shopId under user's node
        val shopRef = database.reference.child("pending_shops").push()
        val shopId = shopRef.key ?: run {
            Toast.makeText(this, "Failed to create shop id", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload shop image (unique path so it will not overwrite)
        val imgRef = storage.reference.child("shops/$uid/$shopId/shop.jpg")
        imgRef.putFile(imageUri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                imgRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val shopModel = AddShopDataClass(
                    shopId = shopId,
                    ownerId = uid,
                    shopName = shopName,
                    category = selectedCategory,
                    shopImage = downloadUrl.toString(),
                    status = "draft"
                )
 val updates = hashMapOf<String, Any?>(
            "pending_shops/$shopId" to shopModel,
            "user_shops/$uid/$shopId" to true
        )

        database.reference.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Step 1 saved", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(this, AddShopLocationActivity::class.java)
                        .putExtra("SHOP_ID", shopId)
                )
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
        if (it != null) binding.shopImage.setImageURI(it)
    }
}
