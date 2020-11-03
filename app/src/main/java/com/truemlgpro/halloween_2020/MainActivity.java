package com.truemlgpro.halloween_2020;

import android.app.*;
import android.os.*;
import android.webkit.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.net.*;
import android.graphics.*;
import android.print.*;

public class MainActivity extends Activity 
{
	
	private WebView webview_doodle;
	private AlertDialog alertNoConnection;
	private Boolean pageLoaded = false;
	private Boolean isNoConnectionDialogShown = false;
	
	private String google_halloween_doodle_2020 = "https://www.google.com/logos/2020/halloween20/rc1/halloween20.html?hl=en_GB";
	
	private BroadcastReceiver NetworkConnectivityReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		this.getWindow().getDecorView().setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		
		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		webview_doodle = (WebView) findViewById(R.id.webview_doodle);

		WebSettings ws = webview_doodle.getSettings();
		ws.setJavaScriptEnabled(true);
		webview_doodle.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webview_doodle.setScrollbarFadingEnabled(true);
		
		webview_doodle.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pageLoaded = false;
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				if (view.getUrl().equalsIgnoreCase(google_halloween_doodle_2020)) {
					pageLoaded = true;
				}
				super.onPageFinished(view, url);
			}
			
			@Override
			public void onLoadResource(WebView view, String url) {
				if (webview_doodle.getProgress() == 100) {
					if (view.getUrl().equalsIgnoreCase(google_halloween_doodle_2020)) {
						pageLoaded = true;
					}
				}
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				
				showErrorToast(MainActivity.this, errorCode);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				view.loadUrl(request.getUrl().toString());
				return false;
			}
		});
		
		webview_doodle.loadUrl("about:blank");
		checkNetworkConnectivity();
    }
	
	private void showErrorToast(Context mContext, int errorCode) {
		String message = null;
		if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
			message = "User authentication failed on server.";
		} else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
			message = "Connection timeout. Try again later.";
		} else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
			message = "Too many requests during this load.";
		} else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
			message = "Unknown error";
		} else if (errorCode == WebViewClient.ERROR_BAD_URL) {
			message = "Check entered URL.";
		} else if (errorCode == WebViewClient.ERROR_CONNECT) {
			message = "Failed to connect to the server.";
		} else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
			message = "Failed to perform SSL handshake.";
		} else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
			message = "Server or proxy hostname lookup failed.";
		} else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
			message = "User authentication failed on proxy.";
		} else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
			message = "Too many redirects.";
		} else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
			message = "Unsupported authentication scheme (not basic or digest).";
		} else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
			message = "Unsupported URI scheme.";
		} else if (errorCode == WebViewClient.ERROR_FILE) {
			message = "Generic file error.";
		} else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
			message = "File not found.";
		} else if (errorCode == WebViewClient.ERROR_IO) {
			message = "The server failed to communicate. Try again later.";
		}
		if (message != null) {
			Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
		}
	}
	
	class NetworkConnectivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			checkNetworkConnectivity();
		}
	}
	
	public void checkNetworkConnectivity() {
		ConnectivityManager CM = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo WiFiCheck = CM.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo CellularCheck = CM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (WiFiCheck.isConnected() || CellularCheck.isConnected()) {
			if (!pageLoaded) {
				if (webview_doodle.getUrl().equalsIgnoreCase("about:blank")) {
					webview_doodle.loadUrl(google_halloween_doodle_2020);
					pageLoaded = true;
					if (isNoConnectionDialogShown) {
						alertNoConnection.dismiss();
						isNoConnectionDialogShown = false;
					}
					// webview_doodle.loadUrl("file:///android_asset/www.google.com/logos/2020/halloween20/rc1/halloween208824.html");
				}
			}
			
			if (pageLoaded) {
				this.getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		} else {
			webview_doodle.loadUrl("about:blank");
			pageLoaded = false;
			showNoConnectionDialog();
			isNoConnectionDialogShown = true;
		}
	}
	
	public void showNoConnectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("No Connection")
			.setMessage("Network is not available\nConnect to Wi-Fi or Mobile Internet")
			.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					checkNetworkConnectivity();
				}
			})
			.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MainActivity.this.finish();
					isNoConnectionDialogShown = false;
				}
			});
		builder.setCancelable(false);
		alertNoConnection = builder.create();
		alertNoConnection.show();
	}
	
	@Override
	protected void onStart()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		NetworkConnectivityReceiver = new NetworkConnectivityReceiver();
		registerReceiver(NetworkConnectivityReceiver, filter);
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		unregisterReceiver(NetworkConnectivityReceiver);
		super.onStop();
	}
	
}
