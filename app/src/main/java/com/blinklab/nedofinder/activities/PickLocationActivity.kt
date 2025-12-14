package com.blinklab.nedofinder.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    private lateinit var googleMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private var marker: Marker? = null

    private val shopId: String by lazy {
        intent.getStringExtra("SHOP_ID") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnSaveLocation).setOnClickListener {
            saveLocationToFirebase()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // When user taps the map, place/move marker
        googleMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng

            if (marker == null) {
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Selected Shop Location")
                        .draggable(true)
                )
            } else {
                marker?.position = latLng
            }
        }

        // If user drags marker update selectedLatLng
        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker) {}
            override fun onMarkerDrag(p0: Marker) {}
            override fun onMarkerDragEnd(p0: Marker) {
                selectedLatLng = p0.position
            }
        })

        // move camera to some default location
        val defaultLatLng = LatLng(29.3956, 71.6836) // (Bahawalpur)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14f))
    }

    private fun saveLocationToFirebase() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (shopId.isBlank()) {
            Toast.makeText(this, "Shop id missing", Toast.LENGTH_SHORT).show()
            return
        }

        val latLng = selectedLatLng ?: run {
            Toast.makeText(this, "Please select location on map", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf<String, Any>(
            "latitude" to latLng.latitude,
            "longitude" to latLng.longitude
        )

        db.child("pending_shops").child(shopId)
            .updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}
