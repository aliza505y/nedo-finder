package com.blinklab.nedofinder.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blinklab.nedofinder.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    private lateinit var googleMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private var marker: Marker? = null

    private val shopId: String by lazy { intent.getStringExtra("SHOP_ID").orEmpty() }
    private val ownerUid: String by lazy { intent.getStringExtra("UID").orEmpty() } 
    private val isViewMode: Boolean by lazy { intent.getBooleanExtra("VIEW_MODE", false) }

    private var viewLat: Double? = null
    private var viewLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val saveBtn = findViewById<Button>(R.id.btnSaveLocation)

        if (isViewMode) {
            saveBtn.visibility = View.GONE

            viewLat = intent.getDoubleExtra("LAT", Double.NaN).takeIf { !it.isNaN() }
            viewLng = intent.getDoubleExtra("LNG", Double.NaN).takeIf { !it.isNaN() }

        } else {
            saveBtn.setOnClickListener { saveLocationToFirebase() }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (isViewMode) {
            showShopMarkerOnMap()
            return
        }

        googleMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            if (marker == null) {
                marker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title("Selected Shop Location").draggable(true)
                )
            } else {
                marker?.position = latLng
            }
        }

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker) {}
            override fun onMarkerDrag(p0: Marker) {}
            override fun onMarkerDragEnd(p0: Marker) {
                selectedLatLng = p0.position
            }
        })

        val defaultLatLng = LatLng(29.3956, 71.6836) // Bahawalpur
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14f))
    }

    private fun showShopMarkerOnMap() {
        val lat = viewLat
        val lng = viewLng

        if (lat == null || lng == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            // Default camera
            val defaultLatLng = LatLng(29.3956, 71.6836)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14f))
            return
        }

        val shopLatLng = LatLng(lat, lng)
        marker = googleMap.addMarker(
            MarkerOptions().position(shopLatLng).title("Shop Location")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLatLng, 16f))
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

        db.child("pending_shops").child(uid).child(shopId)
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
