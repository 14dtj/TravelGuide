package cn.edu.sjtu.travelguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baidu.location.LocationClient;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationClient mLocationClient = new LocationClient(getApplicationContext());
        WebView webView = this.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                return true;
            }
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                return true;
            }
            // 处理定位权限请求
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            @Override
            // 设置网页加载的进度条
            public void onProgressChanged(WebView view, int newProgress) {
//                TestJSLocation.this.getWindow().setFeatureInt(
//                        Window.FEATURE_PROGRESS, newProgress * 100);
                super.onProgressChanged(view, newProgress);
            }
            // 设置应用程序的标题title
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.loadUrl("file:///android_asset/test.html");
        mLocationClient.start();
        mLocationClient.enableAssistantLocation(webView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
