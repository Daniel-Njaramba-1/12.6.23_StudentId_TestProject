package com.example.studentid

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Startup : AppCompatActivity() {
    // Creating firebaseAuth object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in to Firebase
        if (auth.currentUser != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, Postlogin::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is not logged in
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }
    }

}
