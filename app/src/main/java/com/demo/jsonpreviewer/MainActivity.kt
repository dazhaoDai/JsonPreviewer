package com.demo.jsonpreviewer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.demo.jsonpreviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: EditJsonViewModel
    private var contentChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            viewModelStore,
            ViewModelProvider.AndroidViewModelFactory(this.application)
        )[EditJsonViewModel::class.java]
        initWebView()
        viewModel.jsonData.observe(this) { str ->
            if (str?.isNotBlank() == true) {
                binding.webView.loadUrl("javascript:showJson($str)")
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewModel.loadAdConfig(this@MainActivity)
            }
        }
        binding.webView.loadUrl("file:///android_asset/preview_json.html")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            useWideViewPort = true
            builtInZoomControls = true
        }
        binding.webView.addJavascriptInterface(JsInterface(this@MainActivity), "json_parse")
    }

    inner class JsInterface(context: Context) {
        private val mContext: Context

        init {
            mContext = context
        }

        @JavascriptInterface
        fun configContentChanged() {
            runOnUiThread {
                contentChanged = true
            }
        }

        @JavascriptInterface
        fun toastJson(msg: String?) {
            runOnUiThread { Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show() }
        }

        @JavascriptInterface
        fun saveConfig(jsonString: String?) {
            runOnUiThread {
                contentChanged = false
                Toast.makeText(mContext, "verification succeed", Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun parseJsonException(e: String?) {
            runOnUiThread {
                e?.takeIf { it.isNotBlank() }?.let { alert(it) }
            }
        }
    }

    private fun alert(
        message: String,
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert").setMessage(message)
            .setPositiveButton("OK", null)
            .setCancelable(false)
            .create()
            .show()
    }

}