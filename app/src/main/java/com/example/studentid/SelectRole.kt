package com.example.studentid

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SelectRole : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonStudent: RadioButton
    private lateinit var radioButtonLecturer: RadioButton
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        radioGroup = findViewById(R.id.role_radio_grp)
        radioButtonStudent = findViewById(R.id.student_role_btn)
        radioButtonLecturer = findViewById(R.id.lecturer_role_btn)
        saveButton = findViewById(R.id.save_role)

        saveButton.setOnClickListener {
            saveSelectedRole()
        }
    }

    private fun saveSelectedRole() {
        val selectedRole: String = when (radioGroup.checkedRadioButtonId) {
            R.id.student_role_btn -> "student"
            R.id.lecturer_role_btn -> "lecturer"
            else -> ""
        }

        if (selectedRole.isNotEmpty()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                val userData = HashMap<String, Any>()
                userData["role"] = selectedRole

                userRef.updateChildren(userData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Role saved successfully
                            Toast.makeText(this, "Selected Role Saved", Toast.LENGTH_SHORT).show()
                            // Proceed to the next activity or perform any other action
                            val intent = Intent(this, MainMenu::class.java)
                            startActivity(intent)
                        } else {
                            // Failed to save role
                            Toast.makeText(this, "Failed to save Selected Role", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
        }
    }
}
