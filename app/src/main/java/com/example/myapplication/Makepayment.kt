package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Makepayment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_makepayment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Receive/Retrieve Extras Data the product_name and product_cost
        //This data is passed via Intent
        val name = intent.getStringExtra("product_name")
        val cost = intent.getIntExtra("product_cost", 0)
        

        //Find the Name and Cost TextViews
        val txtName: TextView = findViewById(R.id.txtProductName)
        val txtCost: TextView = findViewById(R.id.txtProductCost)

        //Update TextViews with Values Passed Via Intent
        txtName.text = name
        txtCost.text = "Ksh $cost"

        //Find Pay/Purchase Button
        val btnPay: Button = findViewById(R.id.pay)
        //Find Phone number Edit Text
        val edtPhone: EditText = findViewById(R.id.phone)
        //Set Click CListener
        btnPay.setOnClickListener {
            //Set Api Endpoint
            val api = "https://modcom2.pythonanywhere.com/api/mpesa_payment"

            //Get the types phone number
            val phone = edtPhone.text.toString().trim()

            //Create data using RequestParams, put phone and cost as keyvalue pairs
            val data = RequestParams()
            data.put("amount", cost)  //Passed via Intent
            data.put("phone", phone)  // Entered by User in phone EditText

            //Access API helper
            val helper = ApiHelper(applicationContext)
            //Post data  to api endpoint
            helper.post(api, data)
        }
    }
}