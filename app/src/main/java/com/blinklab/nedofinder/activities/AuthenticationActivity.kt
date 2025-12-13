package com.blinklab.nedofinder.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blinklab.nedofinder.R
import com.blinklab.nedofinder.dataclass.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleButton: LinearLayout
    private lateinit var googleText: TextView
    private lateinit var googleLogo: ImageView
    private lateinit var googleProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleButton = findViewById(R.id.continue_with_google)
        googleText = findViewById(R.id.google_text)
        googleLogo = findViewById(R.id.google_logo)
        googleProgress = findViewById(R.id.google_progress)


        googleButton.setOnClickListener {
            signInWithGoogle()
        }


    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data

        if (result.resultCode == RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                setGoogleButtonLoading(false)
                Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            setGoogleButtonLoading(false)
            Toast.makeText(this, "Google Sign-In Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val error = apiAvailability.isGooglePlayServicesAvailable(this)

        if (error != ConnectionResult.SUCCESS) {
            apiAvailability.getErrorDialog(this, error, 0)?.show()
            return
        }

        setGoogleButtonLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            setGoogleButtonLoading(false)

            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {

                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(user.uid)

                    val newUser = UserModel(
                        uid = user.uid,
                        username = user.displayName ?: "",
                        userEmail = user.email ?: "",
                        profileImgUrl = user.photoUrl?.toString() ?: "",
                    )

                    userRef.set(newUser)


                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setGoogleButtonLoading(loading: Boolean) {
        if (loading) {
            googleProgress.visibility = View.VISIBLE
            googleButton.isEnabled = false
            googleText.alpha = 0.5f
            googleLogo.alpha = 0.5f
        } else {
            googleProgress.visibility = View.GONE
            googleButton.isEnabled = true
            googleText.alpha = 1f
            googleLogo.alpha = 1f
        }
    }
    override fun onResume() {
        super.onResume()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
