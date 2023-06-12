package com.example.studentid

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.Toast



class User {
     var email: String? = null
     var role: String? = null


    constructor() {
        // Required empty constructor for Firebase deserialization
    }

    constructor(email: String?, role: String?) {
        this.email = email
        this.role = role
    }
}


class Postlogin : AppCompatActivity() {
    // Creating firebaseAuth object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_login)

        auth = FirebaseAuth.getInstance()

         retrieveUserRole()

    }

    private fun retrieveUserRole() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    val role = user?.role

                    if (role != null) {
                        // Role retrieved successfully, do something with it
                        Toast.makeText(this@Postlogin, "User Role: $role", Toast.LENGTH_SHORT).show()
                        val intent = Intent (this@Postlogin, MainMenu::class.java)
                        startActivity(intent)
                    } else {
                        // Role is null, handle the case where role is missing or not set
                        // You can redirect the user to the login screen or perform any other necessary action
                        val intent = Intent(this@Postlogin, SelectRole::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to retrieve role
                    Toast.makeText(this@Postlogin, "Failed to retrieve User Role", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // User ID is null, handle the case where user is not authenticated or logged in
            // You can redirect the user to the login screen or perform any other necessary action
            val intent = Intent(this@Postlogin, Login::class.java)
            startActivity(intent)
        }
    }

}