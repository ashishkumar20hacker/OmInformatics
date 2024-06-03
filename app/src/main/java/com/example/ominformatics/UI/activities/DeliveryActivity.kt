package com.example.ominformatics.UI.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ominformatics.DataSource.BoolForDataBase
import com.example.ominformatics.DataSource.DbOrderModel
import com.example.ominformatics.DataSource.DeliveryStatus
import com.example.ominformatics.DataSource.MyApplication.Companion.getOrderDao
import com.example.ominformatics.R
import com.example.ominformatics.UI.Utils.isCameraPermissionGranted
import com.example.ominformatics.UI.Utils.requestCameraPermission
import com.example.ominformatics.databinding.ActivityDeliveryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeliveryActivity : AppCompatActivity() {

    lateinit var binding: ActivityDeliveryBinding
    var orderId: Int = 0
    lateinit var order: DbOrderModel
    private val REQUEST_IMAGE_CAPTURE = 1
    private var capturedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getIntExtra("orderId", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            order = getOrderDao().getOrderById(orderId)
            withContext(coroutineContext) {
                binding.orderNo.text = "Order No.: ${order.order_no}"
                binding.customerName.text = "Customer Name:  ${order.customer_name}"
                binding.address.text = "Delivery Address: ${order.address}"
                binding.deliveryCharge.text = "Amount to be collected: ₹${order.delivery_cost}"
                binding.collectedAmount.setText("${order.delivery_cost}")

                if (order.imageUrl.isNotEmpty()) binding.imagePreview.setImageURI(Uri.parse(order.imageUrl))
                binding.damageDetails.setText(order.damageDesc)
                binding.damageAmount.setText(order.anotherAmt.toString())
                if (order.isDamaged == BoolForDataBase.TRUE.status) {
                    binding.radioGroupStatus.check(R.id.radioDamaged)
                    binding.damageDetailsTitle.visibility = View.VISIBLE
                    binding.damageDetails.visibility = View.VISIBLE
                    binding.damageAmount.visibility = View.VISIBLE
                } else {
                    binding.radioGroupStatus.check(R.id.radioGood)
                    binding.damageDetailsTitle.visibility = View.GONE
                    binding.damageDetails.visibility = View.GONE
                    binding.damageAmount.visibility = View.GONE
                }


                // Handle radio button change
                binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
                    if (checkedId == R.id.radioDamaged) {
                        binding.damageDetailsTitle.visibility = View.VISIBLE
                        binding.damageDetails.visibility = View.VISIBLE
                        binding.damageAmount.visibility = View.VISIBLE
                    } else {
                        binding.damageDetailsTitle.visibility = View.GONE
                        binding.damageDetails.visibility = View.GONE
                        binding.damageAmount.visibility = View.GONE
                    }
                }

                // Validate collected amount
                binding.collectedAmount.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validateCollectedAmount()
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })

                binding.btnTakePicture.setOnClickListener {
                    openCamera()
                }

                // Submit button click
                binding.btnSubmit.setOnClickListener {
                    if (validateCollectedAmount()) {
                        if (order.imageUrl.isEmpty()) {
                            saveImageToCacheDirectory()
                        } else {
                            updateDataInDB()
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun openCamera() {
        if (isCameraPermissionGranted(this@DeliveryActivity)) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
//            if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
        } else {
            requestCameraPermission(this@DeliveryActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras: Bundle? = data?.extras
            capturedBitmap = extras?.get("data") as Bitmap
            binding.imagePreview.setImageBitmap(capturedBitmap)
            binding.btnTakePicture.text = "Retake"
        }
    }

    private fun saveImageToCacheDirectory() {
        if (capturedBitmap == null) {
            Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val file = File(cacheDir, "delivered_parcel_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            capturedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            order.imageUrl = file.absolutePath
            updateDataInDB()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDataInDB() {
        lifecycleScope.launch(Dispatchers.IO) {
            order.damageDesc = binding.damageDetails.text.toString()
            order.anotherAmt = binding.damageAmount.text.toString().toDouble()
            order.isDamaged =
                if (binding.radioGroupStatus.checkedRadioButtonId == R.id.radioDamaged) {
                    BoolForDataBase.TRUE.status
                } else {
                    BoolForDataBase.FALSE.status
                }
            getOrderDao().update(order)
            getOrderDao().updateDeliveryStatus(orderId, DeliveryStatus.Delivered.status)
        }
        // Proceed with submission
        Toast.makeText(
            applicationContext,
            "Form submitted successfully",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun validateCollectedAmount(): Boolean {
        val collectedAmountValue = binding.collectedAmount.text.toString().toDoubleOrNull()
        val damageAmountValue = binding.damageAmount.text.toString().toDoubleOrNull()

        return when {
            collectedAmountValue == null -> {
                binding.collectedAmount.error = "Enter a valid amount"
                false
            }

            binding.radioDamaged.isChecked && damageAmountValue != null -> {
                if (collectedAmountValue > order.delivery_cost) {
                    binding.collectedAmount.error =
                        "Amount collected cannot exceed the delivery charge"
                    false
                } else {
                    binding.collectedAmount.error = null
                    true
                }
            }

            binding.radioGood.isChecked && collectedAmountValue != order.delivery_cost -> {
                binding.collectedAmount.error = "Amount must be exactly ₹${order.delivery_cost}"
                false
            }

            else -> {
                binding.collectedAmount.error = null
                true
            }
        }
    }
}