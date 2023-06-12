package com.example.studentid.lecturermodule

import android.os.Bundle
import android.util.Log
import android.widget.Button
//import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.example.studentid.R
import org.json.JSONObject


private const val TAG = "LecturerAttendance"
class LecturerAttendance : AppCompatActivity() {
    private lateinit var attendanceCodeTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var courseCodeText: TextView
    private lateinit var submitButton: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.os.StrictMode.setThreadPolicy(android.os.StrictMode.ThreadPolicy.Builder().permitAll().build())
        setContentView(R.layout.lecturer_attendance)
        // Retrieve the intent that started this activity
        val studentAttendanceIntent = intent
        Log.d(TAG, "Intent extras: ${intent.extras}")
        // Retrieve data from the intent
        val attendanceCode = studentAttendanceIntent.getStringExtra("attendanceCode")
        val courseCode = studentAttendanceIntent.getStringExtra("courseCode")
        // Use the retrieved data as needed
        Log.d(TAG, "Attendance code: $attendanceCode")
        Log.d(TAG, "Course code: $courseCode")

        attendanceCodeTextView = findViewById(R.id.attendance_code_txt_lec)
        emailTextView = findViewById(R.id.email_txt_lec)
        courseCodeText = findViewById(R.id.coursecodelec)
        submitButton = findViewById(R.id.post_btn_lec)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        currentUser = firebaseAuth.currentUser!!

        retrieveEmail()
        displayAttendanceCode()
        displayCourseCode()

        submitButton.setOnClickListener {
            submitAttendance()
        }
    }

    private fun retrieveEmail() {
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email

        if (email != null) {
            emailTextView.text = email
        } else {
            Toast.makeText(this@LecturerAttendance, "Email not found", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Email not found for the current user")
        }
    }

    private fun displayAttendanceCode() {
        val lecturerAttendanceIntent = intent
        val attendanceCode = lecturerAttendanceIntent.getStringExtra("attendanceCode")
        Log.d(TAG, "Attendance code: $attendanceCode")
        attendanceCodeTextView.text = attendanceCode
    }

    private fun displayCourseCode() {
        val courseCode = intent.getStringExtra("courseCode")
        Log.d(TAG, "Course code: $courseCode")
        courseCodeText.text = courseCode
    }

    private fun submitAttendance() {
        val email = emailTextView.text.toString()
        val course = courseCodeText.text.toString().trim()
        val attendanceCode = attendanceCodeTextView.text.toString()

        if (course.isNotEmpty()) {
            val url = "http://127.0.0.1:8000/submitLecturerAttendance/" // Replace with your Django API endpoint URL

            val requestBody = JSONObject()
            requestBody.put("code", attendanceCode)
            requestBody.put("course", course)
            requestBody.put("lecturer", email)

            val requestQueue = Volley.newRequestQueue(this)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                { response ->
                    // Handle the successful response
                    val responseData = response.toString()
                    Toast.makeText(this@LecturerAttendance, responseData, Toast.LENGTH_SHORT).show()
                    // Parse the responseData JSON or perform other actions
                },
                {
                    // Handle the error case
                    Toast.makeText(this@LecturerAttendance, "Fail", Toast.LENGTH_SHORT).show()
                    // Display an error message or perform other actions
                }
            )

            requestQueue.add(jsonObjectRequest)

            // You can redirect the user to another activity or perform any other necessary action
        } else {
            Toast.makeText(this, "Please enter a course", Toast.LENGTH_SHORT).show()
        }
    }
}
