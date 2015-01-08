package simpleui.modifiers.v3.maps;

import simpleui.SimpleUI;
import simpleui.SimpleUIInterface;
import simpleui.SimpleUI.OptionsMenuListener;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.util.ActivityLifecycleListener;
import simpleui.util.SimpleUiApplication;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public abstract class SimpleUIWithMaps extends FragmentActivity implements
		SimpleUIInterface {

	private static final String LOG_TAG = "SimpleUIWithMaps";
	private View ViewToShow;
	private ModifierInterface myModifier;

	public static boolean showUi(Context context,
			ModifierInterface modifierToDisplay,
			Class<? extends SimpleUIWithMaps> simpleUIWithMapsSubclass) {
		return SimpleUI.showUi(context, modifierToDisplay,
				simpleUIWithMapsSubclass);
	}

	public static boolean showCancelOkDialog(Context context,
			String cancelText, String okText, final M_Container itemsToDisplay,
			Class<? extends SimpleUIWithMaps> simpleUIWithMapsSubclass) {
		SimpleUI.addCancelOkButtons(itemsToDisplay, cancelText, okText);
		return SimpleUI.showUi(context, itemsToDisplay,
				simpleUIWithMapsSubclass);
	}

	public static boolean showInfoDialog(Context context,
			String closeButtonText, final M_Container itemsToDisplay,
			Class<? extends SimpleUIWithMaps> simpleUIWithMapsSubclass) {
		SimpleUI.addOkButton(itemsToDisplay, closeButtonText);
		return SimpleUI.showUi(context, itemsToDisplay,
				simpleUIWithMapsSubclass);
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		SimpleUiApplication.setContext(this);
		if (playServicesInstalled()) {
			SimpleUI.onCreate(this, icicle);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		SimpleUI.onSaveInstanceState(this, outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public ModifierInterface getMyModifier() {
		return myModifier;
	}

	@Override
	public View getMyViewToShow() {
		return ViewToShow;
	}

	@Override
	public void setMyModifier(ModifierInterface myModifier) {
		this.myModifier = myModifier;
	}

	@Override
	public void setMyViewToShow(View myViewToShow) {
		ViewToShow = myViewToShow;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (getMyModifier() instanceof ActivityLifecycleListener) {
			((ActivityLifecycleListener) getMyModifier()).onActivityResult(
					this, requestCode, resultCode, data);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& myModifier instanceof ActivityLifecycleListener) {
			// moveTaskToBack(true);
			if (!((ActivityLifecycleListener) myModifier)
					.onCloseWindowRequest(this)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		if (getMyModifier() instanceof ActivityLifecycleListener) {
			((ActivityLifecycleListener) getMyModifier()).onPause(this);
		}
		super.onPause();
	}

	@Override
	protected void onStart() {
		SimpleUI.IAnalyticsHelper.trackStart(this,
				SimpleUI.getTrackText(myModifier));

		try {
			super.onStart();
		} catch (NoClassDefFoundError e) {
			// this should never happen, NoClassDefFoundError can't be catched
			// TODO
			e.printStackTrace();
			showMapsNotInstalledError(this);
		}
	}

	private boolean playServicesInstalled() {
		int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (result == ConnectionResult.SUCCESS) {
			Log.d(LOG_TAG, "isGooglePlayServicesAvailable=true");
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
			int requestCode = 1337; // TODO does it matter which code?
			GooglePlayServicesUtil.getErrorDialog(result, this, requestCode)
					.show();
		} else {
			showMapsNotInstalledError(this);
		}
		Log.e(LOG_TAG, "isGooglePlayServicesAvailable=false");
		return false;
	}

	private void showMapsNotInstalledError(Context c) {
		M_Container infos = new M_Container();
		infos.add(new M_Caption("Setup"));
		infos.add(new M_InfoText(
				R.drawable.ic_dialog_alert,
				"Google Play services missing! This app needs Google Play services to run properly"));
		infos.add(new M_Button("Install Google Play services from Play Store") {

			@Override
			public void onClick(Context context, Button clickedButton) {

				if (context instanceof Activity) {
					Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri
							.parse("market://details?id=com.google.android.gms"));
					startActivity(goToMarket);
					finish();
				}
			}
		});
		SimpleUI.showInfoDialog(c, "Ok", infos);
	}

	@Override
	protected void onStop() {
		SimpleUI.IAnalyticsHelper.trackStop(this);
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getMyModifier() instanceof ActivityLifecycleListener) {
			((ActivityLifecycleListener) getMyModifier()).onResume(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (getMyModifier() instanceof OptionsMenuListener) {
			return ((OptionsMenuListener) getMyModifier()).onCreateOptionsMenu(
					this, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (getMyModifier() instanceof OptionsMenuListener) {
			return ((OptionsMenuListener) getMyModifier())
					.onPrepareOptionsMenu(this, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (getMyModifier() instanceof OptionsMenuListener) {
			return ((OptionsMenuListener) getMyModifier())
					.onOptionsItemSelected(this, item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (getMyModifier() instanceof OptionsMenuListener) {
			((OptionsMenuListener) getMyModifier()).onOptionsMenuClosed(this,
					menu);
		}
		super.onOptionsMenuClosed(menu);
	}
}
