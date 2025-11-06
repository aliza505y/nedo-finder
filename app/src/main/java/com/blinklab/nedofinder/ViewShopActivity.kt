package com.blinklab.nedofinder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.adapter.ReviewAdapter
import com.blinklab.nedofinder.databinding.ActivityViewShopBinding
import com.blinklab.nedofinder.dataclass.ReviewDataClass

class ViewShopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewShopBinding
    private val reviewList = ArrayList<ReviewDataClass>()
    private lateinit var reviewAdapter: ReviewAdapter

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackScreen)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false

        binding.backArrow.setOnClickListener {
            finish()
        }
        /*reviewAdapter = ReviewAdapter(reviewList)
        binding.reviewRecycler.adapter = reviewAdapter
        reviewList.add(ReviewDataClass(R.drawable.profile_8,"John Cena","2 Days ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
        reviewList.add(ReviewDataClass(R.drawable.profile_7,"Kenya  Dophr","3 Days ago","Well manners!"))
        reviewList.add(ReviewDataClass(R.drawable.profile_7,"Ballam Jaba","5 Days ago","Every product is good within resonable \n price range"))
        reviewList.add(ReviewDataClass(R.drawable.profile_9,"Erhtra Jora","2 Weeks ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
        reviewList.add(ReviewDataClass(R.drawable.profile_7,"John Cena","2 Days ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
        reviewList.add(ReviewDataClass(R.drawable.profile_9,"John Cena","2 Days ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
        reviewList.add(ReviewDataClass(R.drawable.profile_8,"John Cena","2 Days ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
        reviewList.add(ReviewDataClass(R.drawable.profile_9,"John Cena","2 Days ago","Awesome selection and friendly staff! Recomended store for all of your daily needs"))
*/
    }
}