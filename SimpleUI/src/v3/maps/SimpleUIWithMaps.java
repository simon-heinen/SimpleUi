package v3.maps;

import v2.simpleUi.ActivityLifecycleListener;
import v2.simpleUi.M_Container;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.SimpleUI;
import v2.simpleUi.SimpleUI.OptionsMenuListener;
import v2.simpleUi.SimpleUiApplication;
import v3.simpleUi.SimpleUIInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public abstract class SimpleUIWithMaps extends FragmentActivity implements
		SimpleUIInterface {

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
		SimpleUI.onCreate(this, icicle);
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
		SimpleUI.trackStart(this, SimpleUI.getTrackText(myModifier));
		super.onStart();
	}

	@Override
	protected void onStop() {
		SimpleUI.trackStop(this);
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
