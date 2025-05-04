package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Find Views By ID
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signin = findViewById<Button>(R.id.signin)

        signin.setOnClickListener {
            //Set API - Endpoint
            val api = "https://modcom2.pythonanywhere.com/api/signin"
            //Add texts from EditTexts to RequestParams, email, password
            val data = RequestParams()
            data.put("email", email.text.toString().trim())
            data.put("password", password.text.toString().trim())
            //Access helper and Post
            val helper = ApiHelper(applicationContext)
            helper.post_login(api, data)
        }
    }
}