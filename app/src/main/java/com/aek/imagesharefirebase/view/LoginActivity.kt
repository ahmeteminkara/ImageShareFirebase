package com.aek.imagesharefirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aek.imagesharefirebase.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val actionBar = supportActionBar
        actionBar!!.title = "Login - Register"

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun login(view: View) {

        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Welcome, $email", Toast.LENGTH_LONG).show()
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }

        /*
      */
    }

    fun register(view: View) {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Toast.makeText(this, "User successfully created", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}