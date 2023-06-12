package com.example.studentid

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentid.lecturermodule.NFCwrite
import com.example.studentid.studentmodule.StudentNFCread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainMenu : AppCompatActivity() {
    private lateinit var attendanceactivitybtn: Button
    private lateinit var profileactivtybtn: Button

    private lateinit var logoutbtn: Button
    private lateinit var exitbtn: Button

    // Creating firebaseAuth object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        // View Binding
        attendanceactivitybtn = findViewById(R.id.attendance_activity)

        profileactivtybtn = findViewById(R.id.profile_activity)
        logoutbtn = findViewById(R.id.logout_btn)
        exitbtn = findViewById(R.id.exit_app_btn)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        attendanceactivitybtn.setOnClickListener {
            redirectToAttendanceActivity()
            // using finish() to end the activity
            finish()
        }



        profileactivtybtn.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        logoutbtn.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() //Finish the current activity to prevent going back to the main menu
        }

        exitbtn.setOnClickListener {
            finishAffinity()
        }

    }

    private fun redirectToAttendanceActivity() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    when (user?.role) {
                        "student" -> {
                            val intent = Intent(this@MainMenu, StudentNFCread::class.java)
                            startActivity(intent)
                        }

                        "lecturer" -> {
                            val intent = Intent(this@MainMenu, NFCwrite::class.java)
                            startActivity(intent)
                        }

                        else -> {
                            // Handle the case where the user role is missing or not recognized
                            // You can display an error message or perform any other necessary action
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to retrieve role
                    Toast.makeText(this@MainMenu, "Failed to retrieve User Role", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // User ID is null, handle the case where user is not authenticated or logged in
            // You can redirect the user to the login screen or perform any other necessary action
            val intent = Intent(this@MainMenu, Login::class.java)
            startActivity(intent)
        }
    }
}