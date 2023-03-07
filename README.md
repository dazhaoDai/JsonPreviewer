## A simple & light json previewer repository on android
![](https://github.com/dazhaoDai/JsonPreviewer/blob/main/images/img.png)
### How to use
1. import the assets to your project
2. init Webview
``` kotlin
binding.webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            useWideViewPort = true
            builtInZoomControls = true
        }
        binding.webView.addJavascriptInterface(JsInterface(this@MainActivity), "json_parse")
```
then load the `preview_json.html`
``` kotlin
webView.loadUrl("file:///android_asset/preview_json.html")
``` 
3. Load your json file and convert to string
``` kotlin
webView.loadUrl("javascript:showJson($str)")
```

4. Save your json
``` kotlin
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

        // save json string
        @JavascriptInterface
        fun saveConfig(jsonString: String?) {
            runOnUiThread {
                contentChanged = false
                Toast.makeText(mContext, "verification succeed", Toast.LENGTH_SHORT).show()
            }
        }
        
        //format failed
        @JavascriptInterface
        fun parseJsonException(e: String?) {
            runOnUiThread {
                e?.takeIf { it.isNotBlank() }?.let { alert(it) }
            }
        }
    }

```