package com.blinklab.nedofinder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.blinklab.nedofinder.databinding.ActivityMainBinding
import com.blinklab.nedofinder.fragment.AddShopFragment
import com.blinklab.nedofinder.fragment.FavoriteFragment
import com.blinklab.nedofinder.fragment.HomeFragment
import com.blinklab.nedofinder.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
       /* window.statusBarColor = ContextCompat.getColor(this, R.color.greyScreen )
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true*/

        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        if (navigateTo == "PROFILE_FRAGMENT") {
            loadFragment(ProfileFragment())
            /*binding.bottomNav.selectedItemId = R.id.profile*/
        } else {
            loadFragment(HomeFragment())
        }
       /* loadFragment(HomeFragment())*/
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.message -> {
                    loadFragment(FavoriteFragment())
                    true
                }
                R.id.add -> {
                    loadFragment(AddShopFragment())
                    true
                }
                R.id.settings -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> {
                    loadFragment(HomeFragment())
                    true
                }
            }

        }

    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()}

    }



