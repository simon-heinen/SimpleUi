package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("JavascriptInterface")
public abstract class M_WebView implements ModifierInterface {

	protected static final String LOG_TAG = "M_WebView";
	private final boolean useDefaultZoomControls;
	private final boolean useTransparentBackground;
	private WebView webView;

	public M_WebView(boolean useDefaultZoomControls,
			boolean useTransparentBackground) {
		this.useDefaultZoomControls = useDefaultZoomControls;
		this.useTransparentBackground = useTransparentBackground;
	}

	@Override
	public View getView(final Context context) {
		webView = new WebView(context) {
			private boolean is_gone = false;

			@Override
			public void onWindowVisibilityChanged(int visibility) {
				super.onWindowVisibilityChanged(visibility);
				try {
					if (visibility == View.GONE) {
						WebView.class.getMethod("onPause").invoke(this);
						this.pauseTimers();
						this.is_gone = true;
					} else if (visibility == View.VISIBLE) {
						WebView.class.getMethod("onResume").invoke(this);
						this.resumeTimers();
						this.is_gone = false;
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				if (this.is_gone) {
					try {
						this.destroy();
					} catch (Exception e) {
					}
				}
			}

		};
		webView.getSettings().setBuiltInZoomControls(useDefaultZoomControls);
		webView.getSettings().setSaveFormData(true);
		if (useTransparentBackground) {
			webView.setBackgroundColor(0x00000000);
		}
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				onPageLoadProgress(progress * 100);
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.startsWith("market://")) {
					try {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(url));
						marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
								| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						context.startActivity(marketIntent);
						return true;
					} catch (Exception e) {
					}
				}
				view.loadUrl(url);
				// then it is not handled by the default action:
				return dontLoadUrlInWebview(url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Log.e(LOG_TAG, "Error " + errorCode + ":" + description
						+ " while loading page " + failingUrl);
				if (onErrorInHtmlResponse(view, errorCode, failingUrl)) {
					hideWebView = true;
				}
			}

			private boolean hideWebView = false;

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(LOG_TAG, "Finished loading " + url);
				if (hideWebView) {
					view.setVisibility(View.GONE);
				} else {
					webView.setVisibility(View.VISIBLE);
				}
				hideWebView = false;
				CookieSyncManager.getInstance().sync();
				view.loadUrl("javascript:window.HTMLOUT.processHTML("
						+ "document.getElementsByTagName("
						+ "'body')[0].innerHTML);");
			}
		});

		webView.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			public void processHTML(String html) {
				onPageLoaded(html);
			}
		}, "HTMLOUT");
		webView.clearView();
		String url = getUrlToDisplay();
		if (!url.contains("://")) { // no protocol defined
			url = "http://" + url; // use default http
		}
		Log.i(LOG_TAG, "Loading page " + url);
		webView.loadUrl(url);
		return webView;
	}

	/**
	 * @param view
	 * @param errorCode
	 * @param description
	 * @param failingUrl
	 * @return true to not show the web view when an error occurs
	 */
	public boolean onErrorInHtmlResponse(WebView view, int errorCode,
			String failingUrl) {
		return false;
	}

	protected abstract void onPageLoaded(String html);

	/**
	 * @param url
	 * @return true if the new loaded url should not be loaded in the web-view
	 */
	protected boolean dontLoadUrlInWebview(String url) {
		return false;
	}

	public abstract void onPageLoadProgress(int progressInPercent);

	/**
	 * @return e.g. "www.google.de" or "file:///android_asset/" + "myFile.htm"
	 */
	public abstract String getUrlToDisplay();

	@Override
	public boolean save() {
		return true;
	}

}
