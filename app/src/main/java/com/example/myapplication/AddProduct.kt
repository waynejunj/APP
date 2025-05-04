package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.loopj.android.http.RequestParams
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class AddProduct : AppCompatActivity() {

    private lateinit var apiHelper: ApiHelper
    private var selectedImageUri: Uri? = null

    // UI Components
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var btnAddProduct: Button
    private lateinit var ivProductImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var progressBar: ProgressBar

    // Permission handling
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImageChooser()
        } else {
            showPermissionDeniedDialog()
        }
    }

    // Image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                ivProductImage.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        apiHelper = ApiHelper(this)
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        ivProductImage = findViewById(R.id.ivProductImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        progressBar = findViewById(R.id.progressbar)
    }

    private fun setupClickListeners() {
        btnSelectImage.setOnClickListener {
            checkPermissionAndOpenImageChooser()
        }

        btnAddProduct.setOnClickListener {
            uploadProduct()
        }
    }

    private fun checkPermissionAndOpenImageChooser() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImageChooser()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("We need access to your photos to select product images")
            .setPositiveButton("Allow") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Please enable storage permission in app settings")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun validateInputs(): Boolean {
        if (etProductName.text.toString().trim().isEmpty()) {
            etProductName.error = "Product name required"
            return false
        }

        if (etProductPrice.text.toString().trim().isEmpty()) {
            etProductPrice.error = "Price required"
            return false
        }

        if (etProductDescription.text.toString().trim().isEmpty()) {
            etProductDescription.error = "Description required"
            return false
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun uploadProduct() {
        if (!validateInputs()) return

        progressBar.visibility = View.VISIBLE
        btnAddProduct.isEnabled = false

        try {
            val params = RequestParams().apply {
                put("product_name", etProductName.text.toString().trim())
                put("product_cost", etProductPrice.text.toString().trim())
                put("product_description", etProductDescription.text.toString().trim())

                selectedImageUri?.let { uri ->
                    val file = File(getRealPathFromUri(uri))
                    if (file.exists()) {
                        put("product_photo", file, "image/jpeg")
                    } else {
                        throw FileNotFoundException("Image file not found")
                    }
                }
            }

            // Replace with your actual API endpoint
            val apiUrl = "https://atom19j60.pythonanywhere.com/api/add_product"

            apiHelper.post(apiUrl, params)

            // Alternative with callback handling:
            /*
            apiHelper.uploadProduct(apiUrl, params, object : ApiHelper.CallBack {
                override fun onSuccess(result: JSONArray?) = handleUploadSuccess()
                override fun onSuccess(result: JSONObject?) = handleUploadSuccess()
                override fun onFailure(result: String?) = handleUploadFailure(result)
            })
            */

        } catch (e: FileNotFoundException) {
            handleUploadFailure("Image file error: ${e.message}")
            progressBar.visibility = View.GONE
        } catch (e: Exception) {
            handleUploadFailure("Error: ${e.message}")
            progressBar.visibility = View.GONE
        }
    }

    private fun handleUploadSuccess() {
        runOnUiThread {
            progressBar.visibility = View.GONE
            btnAddProduct.isEnabled = true

            Toast.makeText(this, "Product uploaded successfully!", Toast.LENGTH_SHORT).show()
            resetForm()
        }
    }

    private fun handleUploadFailure(error: String?) {
        runOnUiThread {
            progressBar.visibility = View.GONE
            btnAddProduct.isEnabled = true
            Toast.makeText(this, "Upload failed: $error", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetForm() {
        etProductName.text.clear()
        etProductPrice.text.clear()
        etProductDescription.text.clear()
        ivProductImage.setImageResource(R.drawable.ic_launcher_background)
        selectedImageUri = null
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) } ?: ""
        cursor?.close()
        return path
    }
}