package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Find Views By ID
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val signup = findViewById<Button>(R.id.signup)
        //Set Button Helper

        signup.setOnClickListener {
            //Set the API Endpoint URL
            val api = "https://modcom2.pythonanywhere.com/api/signup"

            //Put text/values from EditTexts to RequestParams - holds data
            val data = RequestParams()
            data.put("username", username.text.toString())
            data.put("password", password.text.toString())
            data.put("email", email.text.toString())
            data.put("phone", phone.text.toString())
            //Access ApiHelper Class
            val helper = ApiHelper(applicationContext)
            //Post the data to our API
            helper.post(api, data)
        }
    }
}