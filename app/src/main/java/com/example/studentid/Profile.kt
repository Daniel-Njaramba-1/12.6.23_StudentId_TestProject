package com.example.studentid

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

private const val TAG = "Profile"
class Profile : AppCompatActivity() {
    private lateinit var firstname: EditText
    private lateinit var surname: EditText
    private lateinit var emailtxt: TextView
    private lateinit var save: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        currentUser = firebaseAuth.currentUser!!

        firstname = findViewById(R.id.firstnametxt)
        surname = findViewById(R.id.surname_txt)
        emailtxt = findViewById(R.id.email_profile_txt)
        save = findViewById(R.id.save_profile)

        retrieveEmail()

        save.setOnClickListener {
            saveProfile()
        }
    }

    private fun retrieveEmail() {
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email

        if (email != null) {
            emailtxt.text = email
        } else {
            Toast.makeText(this@Profile, "Email not found", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Email not found for the current user")
        }
    }
    private fun saveProfile() {
        val email = emailtxt.text.toString()
        val firstname = firstname.text.toString()
        val surname = surname.text.toString()

        if (email.isNotEmpty()) {
            val url = "http://127.0.0.1.8000/submitStudentAttendance/" // Replace with your Django API endpoint URL

            val requestBody = JSONObject()
            requestBody.put("email", email)
            requestBody.put("firstname", firstname)
            requestBody.put("surname", surname)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .post(requestBody.toString().toRequestBody(mediaType))
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (response.isSuccessful) {
                        // Handle the successful response
                        Toast.makeText(this@Profile, responseData, Toast.LENGTH_SHORT).show()
                        // Parse the responseData JSON or perform other actions
                    } else {
                        // Handle the unsuccessful response
                        Toast.makeText(this@Profile, "Fail", Toast.LENGTH_SHORT).show()
                        // Display an error message or perform other actions
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Handle the failure case
                    e.printStackTrace()
                }
            })

            // You can redirect the user to another activity or perform any other necessary action
        } else {
            Toast.makeText(this, "Please enter a course", Toast.LENGTH_SHORT).show()
        }
    }
}

