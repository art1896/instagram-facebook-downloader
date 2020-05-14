package com.example.inst

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_video.*
import java.util.*


@Suppress("DEPRECATION")
class VideoActivity : AppCompatActivity() {

    var exoPlayer: SimpleExoPlayer? = null
    var queueid: Long? = null
    var dm: DownloadManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        MobileAds.initialize(this)
        videoadView.adListener = object: AdListener() {
        }
        val adRequest =
            AdRequest.Builder().build()
        videoadView.loadAd(adRequest)
        val url = intent.getStringExtra("videourl")
        initExo(url)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action!!
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {

                    val reqQuery = DownloadManager.Query()
                    reqQuery.setFilterById(queueid!!)
                    val c = dm!!.query(reqQuery)
                    if (c.moveToFirst()) {
                        val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        }
                    }

                }
            }
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        videoSave_button.setOnClickListener {
            downloadfile(url)
        }
        downloads_button.setOnClickListener {
            openDownloads()
            exoPlayer?.stop()
        }
    }


    fun initExo(url: String) {
        try {
            val bandwidthMeter = DefaultBandwidthMeter() as BandwidthMeter
            val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
            val videoUri = Uri.parse(url)
            val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")
            val extractorFactory = DefaultExtractorsFactory()
            val mediaSource =
                ExtractorMediaSource(videoUri, dataSourceFactory, extractorFactory, null, null)
            player_view.player = exoPlayer
            exoPlayer!!.prepare(mediaSource)
            exoPlayer!!.playWhenReady = true
        } catch (e: Exception) {
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDownloads() {
        val intent = Intent()
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS)
        startActivity(intent)
    }

    fun downloadfile(url: String) {
        dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(true)
        request.setAllowedOverRoaming(false)
        request.setVisibleInDownloadsUi(true)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DCIM.toString(),"/" + "${UUID.randomUUID()}.mp4")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        queueid = dm!!.enqueue(request)


    }

}

