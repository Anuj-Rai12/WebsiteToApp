package com.example.prcactice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.prcactice.databinding.ActivityMainBinding


const val URL = "https://thetechuniqueacademy.com/"
const val TAG="MYTAG"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val progressBar: ProgressDialog by lazy {
        ProgressDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        } else {
            checkConnection()
        }
        progressBar.setMessage("Please Wait...")
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.progressBar.isVisible = true
                binding.progressBar.progress = newProgress
                title = "Loading..."
                progressBar.show()
                if (newProgress == 100) {
                    binding.progressBar.isVisible = false
                    title = view.title
                    progressBar.dismiss()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        binding.btnNoConnection.setOnClickListener {
            checkConnection()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymeu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_next->{
                if (binding.webView.canGoForward()){
                    binding.webView.goForward()
                }
            }
            R.id.nav_previous->{
                onBackPressed()
            }
            R.id.nav_reload->{
                checkConnection()
            }
            else-> Log.i(TAG, "onOptionsItemSelected: Wrong Selection")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to Exit?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes"
                ) { _, _ ->
                    finishAffinity()
                }.show()
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setData() {
        binding.webView.apply {
            webViewClient = WebViewClient()
                loadUrl(URL)
        }
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkConnection() {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        when {
            wifi!!.isConnected -> {
                setData()
                binding.webView.isVisible = true
                binding.relativeLayout.isVisible = false
            }
            mobileNetwork!!.isConnected -> {
                setData()
                binding.webView.isVisible = true
                binding.relativeLayout.isVisible = false
            }
            else -> {
                binding.webView.isVisible = false
                binding.relativeLayout.isVisible = true
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }
}