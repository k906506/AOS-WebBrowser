package com.example.webbrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        bindViews()
    }

    private fun initView() {
        webView.apply {
            webViewClient = WebViewClient() // 외부 웹브라우져로 전환되는 것을 막음
            settings.javaScriptEnabled = true // 자바스크립트 사용을 허용
            loadUrl("https://www.google.com")
        }
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                webView.loadUrl(v.text.toString())
            }
            return@setOnEditorActionListener false
        }
    }
}