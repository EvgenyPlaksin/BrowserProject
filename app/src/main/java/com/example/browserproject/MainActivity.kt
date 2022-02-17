package com.example.browserproject
import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*


var uploadMessage: ValueCallback<Array<Uri>>? = null
val REQUEST_SELECT_FILE = 100
var url_2: String? = null

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Internet!")
            .setMessage("Check connection and try again")
            .setPositiveButton("Ok") {
                    dialog, id ->  dialog.cancel()
            }
        val cld = ConnectionCheck(application)

        cld.observe(this) { isConnected ->

            if (isConnected) {
                builder.create()
                search_btn?.setOnClickListener {
                    val URL = url_txt.text.toString()
                    val pmmCookies: CookieManager = CookieManager.getInstance()
                    CookieManager.setAcceptFileSchemeCookies(true)
                    pmmCookies.setAcceptThirdPartyCookies(web_browser, true)
                    web_browser.settings.apply {
                        useWideViewPort = true
                        javaScriptEnabled = true
                        mixedContentMode = 0
                        loadWithOverviewMode = true
                        allowFileAccess = true
                        domStorageEnabled = true
                        defaultTextEncodingName = "utf-8"
                        databaseEnabled = true
                        allowFileAccessFromFileURLs = true
                        setAppCacheEnabled(true)
                        javaScriptCanOpenWindowsAutomatically = true
                    }

                    web_browser.loadUrl(URL)
                    back_btn.setOnClickListener {
                        web_browser.goBack()
                    }
                }

                if(url_2 != null){
                    web_browser.loadUrl("$url_2")
                }else{
                    web_browser?.loadUrl("http://www.google.com")
                }

                web_browser?.settings?.javaScriptEnabled = true // we need to enable javascript
                web_browser?.canGoBack()
                web_browser?.webViewClient = WebClient(this)
                web_browser?.setWebChromeClient(object: WebChromeClient() {
                    private var mUploadMessage: ValueCallback<Uri>? = null

                    private val FILECHOOSER_RESULTCODE = 1



                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        if (uploadMessage != null) {
                            uploadMessage!!.onReceiveValue(null)
                            uploadMessage = null
                        }
                        uploadMessage = filePathCallback
                        try {
                            startActivityForResult(
                                fileChooserParams!!.createIntent(),
                                REQUEST_SELECT_FILE
                            )
                        } catch (e: ActivityNotFoundException) {
                            uploadMessage = null
                            return false
                        }
                        return true
                    }

                })


            } else {
                builder.create()
                builder.show()
            }
        }

    }
    override fun onBackPressed() {
        if (web_browser.canGoBack()) {
            web_browser.goBack()
        } else {
            Log.d("TAG", "can`t go back")
        }
    }



    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return
            uploadMessage!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    intent
                )
            )
            uploadMessage = null
        }
    }

}
    class WebClient internal constructor(private val activity: Activity): WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url?.startsWith("tel:") == true || url?.startsWith("mailto:") == true || url?.startsWith("tg:") == true) {
             //   startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) тут странная ошибка
                return true
            } else {
                if (url != null) {
                    view?.loadUrl(url)
                }
            }
            return false
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            Log.e("onPageFinished", "$url")
            CookieManager.getInstance().flush()
            url_2 = url

        }
    }





