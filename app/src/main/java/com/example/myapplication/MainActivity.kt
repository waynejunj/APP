package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.RequestParams

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Find Views By ID
        val signinbtn = findViewById<Button>(R.id.signin)
        val signupbtn = findViewById<Button>(R.id.signup)
        val progress = findViewById<ProgressBar>(R.id.progressbar)
        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val addbtn = findViewById<Button>(R.id.btnAddProduct)

        val url = "https://Atom19J60.pythonanywhere.com/api/get_products"
        val helper = ApiHelper(applicationContext)

        signinbtn.setOnClickListener {
            val intent = Intent(applicationContext, Signin::class.java)
            startActivity(intent)
        }

        signupbtn.setOnClickListener {
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }
        helper.loadProducts(url, recycler, progress )

        addbtn.setOnClickListener {
            val intent = Intent(applicationContext,AddProduct::class.java)
            startActivity(intent)
        }

    }
}