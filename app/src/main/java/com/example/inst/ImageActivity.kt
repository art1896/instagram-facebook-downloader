package com.example.inst

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image.*
import java.util.*


class ImageActivity : AppCompatActivity() {

    internal var url: String? = null

    private val PERMISSION_REQUEST_CODE: Int = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)


        MobileAds.initialize(this)
        imageadView.adListener = object : AdListener() {
        }
        val adRequest =
            AdRequest.Builder().build()
        imageadView.loadAd(adRequest)
        url = intent.getStringExtra("imageurl")
        Picasso.get().load(url).into(image)
        button.setOnClickListener {
            saveImage()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }


    }

    fun saveImage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf<String>(), PERMISSION_REQUEST_CODE)
        } else {

            val fileName = UUID.randomUUID().toString() + ".jpg"
            Picasso.get().load(url).into(
                SaveImageHelper(
                    baseContext,
                    applicationContext.contentResolver,
                    fileName,
                    "Image description"
                )
            )
        }
    }
}