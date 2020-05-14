package com.example.inst

import android.app.Dialog
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    companion object {
        var url: String? = null
        var downloadUrl: String? = null
        var type: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-1278927663267942/3198187134"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        MobileAds.initialize(this)
        adView.adListener = object : AdListener() {
        }
        val adRequest =
            AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasPrimaryClip()) {
            paste_button.isEnabled = false
        }


        paste_button.setOnClickListener {
            val clipData = clipboardManager.primaryClip
            val item = clipData?.getItemAt(0)

            editText.setText(item?.text, TextView.BufferType.EDITABLE);
            Toast.makeText(this, "Pasted", Toast.LENGTH_SHORT).show()
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                button.isEnabled = true
            }
        })

        button.setOnClickListener {
            url = editText.text.toString()
            showAd()
            GetData().execute(url)
//
        }

    }

    inner class GetData : AsyncTask<String, Void, Void>() {

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (!downloadUrl.equals("null")) {
                Log.d("datatype", type)
                when (type) {
                    "photo" -> {
                        val imageActivity = Intent(baseContext, ImageActivity::class.java)
                        imageActivity.putExtra("imageurl", downloadUrl)
                        startActivity(imageActivity)
                    }
                    "video" -> {
                        val videoActivity = Intent(baseContext, VideoActivity::class.java)
                        videoActivity.putExtra("videourl", downloadUrl)
                        startActivity(videoActivity)
                    }
                }

            } else Toast.makeText(baseContext, "Limit is reached, Try again", Toast.LENGTH_SHORT)
                .show()
            button.isEnabled = true
            progressBar.visibility = ProgressBar.GONE
        }

        override fun doInBackground(vararg params: String?): Void? {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url("https://instagram-facebook-media-downloader.p.rapidapi.com/api?igurl=${params[0]}")
                .get()
                .addHeader("x-rapidapi-host", "instagram-facebook-media-downloader.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "d2e096b93cmsh0a24ee6ac1775dep1c9c4bjsnd37666ababed")
                .build()
            val response: Response = client.newCall(request).execute()
            val jsonData = response.body!!.string()
            val jsonObject = JSONObject(jsonData)
            if (jsonObject.has("downloadurl")) {
                downloadUrl = jsonObject.getString("downloadurl")
                type = jsonObject.getString("typedesc")
            } else downloadUrl = "null"
            return null
        }

        override fun onPreExecute() {
            button.isEnabled = false
            progressBar.visibility = ProgressBar.VISIBLE
            super.onPreExecute()
        }
    }

    fun instaInfoDialog(view: View) {
        val dialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.insta_dialog, null)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        val btnOk: Button = dialogView.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

    fun fbInfoDialog(view: View) {
        val dialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.fb_dialog, null)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        val btnOk: Button = dialogView.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

    fun showAd() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }
}

