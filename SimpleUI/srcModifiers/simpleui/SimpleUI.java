package simpleui;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Collection;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.util.ActivityLifecycleListener;
import simpleui.util.AnalyticsHelperNoOp;
import simpleui.util.ErrorHandler;
import simpleui.util.IAnalyticsHelper;
import simpleui.util.SimpleUiApplication;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Don't forget to add<br>
 * <br>
 * < activity android:name="v2.simpleUi.SimpleUI" android:theme=
 * "@android:style/Theme.Translucent"/> <br>
 * <br>
 * to your Manifest.xml file! Or you define your custom theme (see
 * https://android
 * .googlesource.com/platform/frameworks/base/+/refs/heads/master/
 * core/res/res/values/themes.xml for code examples)<br>
 * <br>
 * If you want to use the {@link SimpleUI} activity by implementing your own
 * subactivity of {@link SimpleUI} then override the
 * {@link SimpleUI#loadStaticElementToDisplay()} and return a modifier or a view
 * to be displayed. This has some advantages over extending the normal
 * {@link Activity} but is not required to be able to use
 * {@link ModifierInterface}s.
 * 
 * <br>
 * <br>
 * To enable analytics include a analytics.xml file in your project as
 * instructed in
 * https://developers.google.com/analytics/devguides/collection/android/v3/
 * events
 * 
 * 
 * @author Simon Heinen
 * 
 */
public class SimpleUI extends ActionBarActivity implements SimpleUIInterface {

	public interface OptionsMenuListener {

		/**
		 * See {@link Activity#onCreateOptionsMenu(Menu)}
		 * 
		 * @param simpleUI
		 * @param menu
		 * @return
		 */
		boolean onCreateOptionsMenu(Activity a, Menu menu);

		/**
		 * See {@link Activity#onCreateOptionsMenu(Menu)}
		 * 
		 * just return true if you alread handled everything in
		 * {@link OptionsMenuListener#onCreateOptionsMenu(Activity, Menu)}
		 * 
		 * @param simpleUI
		 * @param menu
		 * @return
		 */
		boolean onPrepareOptionsMenu(Activity a, Menu menu);

		/**
		 * See {@link Activity#onOptionsItemSelected(MenuItem)}
		 * 
		 * @param simpleUI
		 * @param item
		 * @return
		 */
		boolean onOptionsItemSelected(Activity a, MenuItem item);

		/**
		 * See {@link Activity#onOptionsItemSelected(MenuItem)}
		 * 
		 * @param simpleUI
		 * @param menu
		 */
		void onOptionsMenuClosed(Activity a, Menu menu);

	}

	private static final String TRANSFAIR_KEY_ID = "transfairKey";
	private static final String LOG_TAG = "SimpleUI v2";
	private static boolean DEBUG = true;
	private static SimpleUiApplication application;

	public static IAnalyticsHelper IAnalyticsHelper = initIAnalyticsHelper();

	private View myViewToShow;
	private ModifierInterface myModifier;

	private static class SimpleUIRestoreContainer {
		View ViewToShow;
		ModifierInterface myModifier;

		@Override
		public String toString() {
			if (myModifier != null) {
				return myModifier.toString();
			}
			return super.toString();
		}
	}

	/**
	 * Will save changes when the close button is pressed
	 * 
	 * @param context
	 * @param closeButtonText
	 *            e.g. "Save & Close"
	 * @param itemsToDisplay
	 * @return
	 */
	public static boolean showInfoDialog(Context context,
			String closeButtonText, final M_Collection itemsToDisplay) {
		addOkButton(itemsToDisplay, closeButtonText);
		return showUi(context, itemsToDisplay);
	}

	private static simpleui.util.IAnalyticsHelper initIAnalyticsHelper() {
		try {
			ClassLoader classLoader = SimpleUI.class.getClassLoader();

			// check if google analytics jar included:
			classLoader
					.loadClass("com.google.analytics.tracking.android.EasyTracker");
			Class analyticsHelperClass = classLoader
					.loadClass("tools.AnalyticsHelper");
			Log.i(LOG_TAG, "Found tools.AnalyticsHelper and "
					+ "will create instance now");
			return (simpleui.util.IAnalyticsHelper) analyticsHelperClass
					.newInstance();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		Log.i(LOG_TAG, "Google Analytics not supported in this "
				+ "application, injecting AnalyticsHelperNoOp");
		return new AnalyticsHelperNoOp();
	}

	public static boolean showCancelOkDialog(Context context,
			String cancelText, String okText, final M_Collection itemsToDisplay) {
		addCancelOkButtons(itemsToDisplay, cancelText, okText);
		return showUi(context, itemsToDisplay);
	}

	public static ModifierInterface newOkButton(String okButtonText,
			final ModifierInterface targetContainer) {

		return new M_Button(okButtonText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (targetContainer.save() && context instanceof Activity) {
					IAnalyticsHelper.track(context, "okSucc", "OkPress "
							+ getTrackText(targetContainer));
					((Activity) context).finish();
				} else {
					IAnalyticsHelper.track(context, "okError", "OkPress "
							+ getTrackText(targetContainer));
				}
			}
		};
	}

	public static void addOkButton(final M_Collection targetContainer,
			String okButtonText) {
		if (targetContainer == null) {
			return;
		}
		ModifierInterface lastElement = getLastElement(targetContainer);
		if (lastElement instanceof M_Button) {
			M_Button b = (M_Button) lastElement;
			if (b.getText().equals(okButtonText)) {
				return;
			}
		}
		targetContainer.add(newOkButton(okButtonText, targetContainer));
	}

	private static ModifierInterface getLastElement(List<ModifierInterface> list) {
		if (list.size() == 0) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * uses a default {@link CancelOkListener} which just closes the window on
	 * cancel and executes the save command on ok
	 * 
	 * @param targetContainer
	 * @param cancelText
	 * @param okText
	 */
	public static void addCancelOkButtons(final M_Collection targetContainer,
			String cancelText, String okText) {
		addCancelOkButtons(targetContainer, cancelText, okText,
				new CancelOkListener() {

					@Override
					public void onCancel(Context context,
							M_Collection targetContainer) {
						if (context instanceof Activity) {
							IAnalyticsHelper.track(context, "cancelPress",
									"Cancelled "
											+ getTrackText(targetContainer));
							((Activity) context).finish();
						}
					}

					@Override
					public void onOk(Context context,
							M_Collection targetContainer) {

						if (targetContainer.save()
								&& context instanceof Activity) {
							IAnalyticsHelper.track(context, "saveSucc",
									"SavePress "
											+ getTrackText(targetContainer));
							((Activity) context).finish();
						} else {
							IAnalyticsHelper.track(context, "saveError",
									"SavePress "
											+ getTrackText(targetContainer));
						}
					}

				});
	}

	public interface CancelOkListener {
		void onCancel(Context context, M_Collection targetContainer);

		void onOk(Context context, M_Collection targetContainer);
	}

	public static void addCancelOkButtons(final M_Collection targetContainer,
			String cancelText, String okText, final CancelOkListener l) {
		if (targetContainer == null) {
			return;
		}
		ModifierInterface lastElement = getLastElement(targetContainer);
		if (lastElement instanceof M_HalfHalf) {
			M_HalfHalf b = (M_HalfHalf) lastElement;
			if (b.getMyLeft() instanceof M_Button
					&& b.getMyRight() instanceof M_Button) {
				if (((M_Button) b.getMyLeft()).getText().equals(cancelText)
						&& ((M_Button) b.getMyRight()).getText().equals(okText)) {
					return;
				}
			}
		}
		ModifierInterface left = new M_Button(cancelText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				l.onCancel(context, targetContainer);
			}
		};
		ModifierInterface right = new M_Button(okText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				l.onOk(context, targetContainer);
			}
		};
		targetContainer.add(new M_HalfHalf(left, right));
	}

	/**
	 * @param currentActivity
	 * @param contentToShow
	 *            e.g. a {@link M_Container} which is filled with all the items
	 * @return
	 */
	public static boolean showUi(Context context,
			ModifierInterface modifierToDisplay) {
		return showUi(context, modifierToDisplay, SimpleUI.class);
	}

	public static boolean showUi(Context context,
			ModifierInterface modifierToDisplay, Class<?> simpleUiClass) {
		if (modifierToDisplay != null) {
			Intent intent = new Intent(context, simpleUiClass);
			try {
				String key = storeObjectInTransfairList(context,
						modifierToDisplay);
				/*
				 * The key to the object will be stored in the extras of the
				 * intent:
				 */
				intent.putExtra(TRANSFAIR_KEY_ID, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (DEBUG) {
				Log.i(LOG_TAG, "Starting activity " + simpleUiClass
						+ " with modifier " + modifierToDisplay);
			}
			startActivity(context, intent);
			return true;
		}
		if (DEBUG) {
			Log.w(LOG_TAG,
					"modifierToDisplay was null. Will NOT start the activity");
		}
		return false;
	}

	public static void startActivity(Context context, Intent intent) {
		if (context instanceof Activity) {
			context.startActivity(intent);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		if (DEBUG) {
			Log.i(LOG_TAG, "onPause" + " by " + this);
		}
		ActivityLifecycleListener m = getIfCorrectListener(
				ActivityLifecycleListener.class, getMyModifier());
		if (m != null) {
			m.onPause(this);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (DEBUG) {
			Log.v(LOG_TAG, "onResume" + " by " + this);
		}
		super.onResume();
		ActivityLifecycleListener m = getIfCorrectListener(
				ActivityLifecycleListener.class, getMyModifier());
		if (m != null) {
			m.onResume(this);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (DEBUG) {
			Log.v(LOG_TAG, "onRestart" + " by " + this);
		}
	}

	@Override
	protected void onDestroy() {
		if (DEBUG) {
			Log.v(LOG_TAG, "onDestroy" + " by " + this);
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG) {
			Log.v(LOG_TAG, "onStart" + " by " + this);
		}

		if (myModifier != null) {
			IAnalyticsHelper.trackStart(this, getTrackText(myModifier));
		}
	}

	public static String getTrackText(ModifierInterface m) {
		if (m == null) {
			return null;
		}
		return "" + m.getClass() + ":" + m;
	}

	@Override
	protected void onStop() {
		if (DEBUG) {
			Log.v(LOG_TAG, "onStop" + " by " + this);
		}
		ActivityLifecycleListener m = getIfCorrectListener(
				ActivityLifecycleListener.class, getMyModifier());
		if (m != null) {
			// moveTaskToBack(true);
			m.onStop(this);
		}
		super.onStop();
		IAnalyticsHelper.trackStop(this);
	}

	/**
	 * @param itemToDisplay
	 * @return the key for the location where it is stored
	 */
	private static String storeObjectInTransfairList(Context c,
			Object itemToDisplay) {

		String newKey = new Date().toString() + itemToDisplay.toString();
		getApplication(c).getTransferList().put(newKey, itemToDisplay);
		if (DEBUG) {
			getApplication(c).getTransferList();
		}
		return newKey;
	}

	public static SimpleUiApplication getApplication(Context c) {
		if (c instanceof Activity) {
			SimpleUiApplication app = SimpleUiApplication
					.getApplication((Activity) c);
			if (app != null) {
				HashMap<String, Object> tr = null;
				if (application != null && app != application) {
					if (DEBUG) {
						Log.w(LOG_TAG, "new application and already "
								+ "loaded application were not "
								+ "equal! Replacing old reference");
					}
					tr = application.getTransferList();
				}
				application = app;
				// try to resque all the objects from the old list:
				if (tr != null) {
					application.getTransferList().putAll(tr);
				}
			}
		}
		if (application == null) {
			// create the backup singleton
			application = new SimpleUiApplication();
		}
		return application;
	}

	private static Object loadObjectFromTransfairList(Activity a, String key) {
		HashMap<String, Object> transfairList = getApplication(a)
				.getTransferList();
		if (key == null) {
			if (DEBUG) {
				Log.i(LOG_TAG, "passed key was null, will"
						+ " try to load content from static method");
			}
			return null;
		}
		if (transfairList == null) {
			if (DEBUG) {
				Log.i(LOG_TAG, "transfairList object was null, so "
						+ "storeObjectInTransfairList was "
						+ "never called before!");
			}
			return null;
		}
		Object o = transfairList.get(key);
		if (DEBUG) {
			Log.v(LOG_TAG, "Returning " + o + " for the passed key=" + key);
		}
		// transfairList.remove(key);
		return o;
	}

	// private View myView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleUI.onCreate(this, savedInstanceState);
	}

	@Override
	public void setMyViewToShow(View myViewToShow) {
		this.myViewToShow = myViewToShow;
	}

	@Override
	public void setMyModifier(ModifierInterface myModifier) {
		this.myModifier = myModifier;
	}

	@Override
	public ModifierInterface getMyModifier() {
		return myModifier;
	}

	@Override
	public View getMyViewToShow() {
		return myViewToShow;
	}

	public static void onCreate(Activity a, Bundle savedInstanceState) {
		// try {
		// a.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		if (DEBUG) {
			Log.i(LOG_TAG, "onCreate" + " by " + a);
		}
		try {

			String key = null;
			Intent intent = a.getIntent();
			if (savedInstanceState != null) {
				key = savedInstanceState.getString(TRANSFAIR_KEY_ID);
			} else if (intent != null && intent.getExtras() != null) {
				key = intent.getExtras().getString(TRANSFAIR_KEY_ID);
			} else {
				Log.i(LOG_TAG, "On create got no information what to display");
			}

			if (DEBUG) {
				Log.i(LOG_TAG, "onCreate got key=" + key);
			}
			if (a instanceof SimpleUIInterface) {
				SimpleUIInterface sUI = (SimpleUIInterface) a;
				sUI.setMyViewToShow(loadContentToViewField(a, sUI, key));
				View myViewToShow = sUI.getMyViewToShow();
				if (DEBUG) {
					Log.d(LOG_TAG, "Loaded " + myViewToShow);
				}
				if (myViewToShow != null) {
					try {
						((ViewGroup) myViewToShow.getParent())
								.removeView(myViewToShow);
					} catch (Exception e) {
					}
					a.setContentView(myViewToShow);
				} else {
					M_Container c = createErrorInfo(a);
					a.setContentView(c.getView(a));
				}
			}
		} catch (Exception e) {
			ErrorHandler.showErrorActivity(a, e, true);
			a.finish();
		}
	}

	private static View loadContentToViewField(Activity a,
			SimpleUIInterface sUI, String key) {
		Object o = loadObjectFromTransfairList(a, key);
		if (o == null) {
			o = sUI.loadStaticElementToDisplay();
		}
		if (o instanceof SimpleUIRestoreContainer) {
			sUI.setMyModifier(((SimpleUIRestoreContainer) o).myModifier);
			View v = ((SimpleUIRestoreContainer) o).ViewToShow;
			sUI.setMyViewToShow(v);
			return v;
		}
		if (o instanceof ModifierInterface) {
			sUI.setMyModifier((ModifierInterface) o);
			return sUI.getMyModifier().getView(a);
		}
		if (o instanceof View) {
			sUI.setMyViewToShow((View) o);
			return sUI.getMyViewToShow();
		}
		return null;
	}

	@Override
	public Object loadStaticElementToDisplay() {
		return null;
	}

	/**
	 * This can happen if the complete application is killed by the system, the
	 * content is loaded dynamically so this is necessary to switch the
	 * application back to a valid state.
	 * 
	 * @return
	 */
	private static M_Container createErrorInfo(final Activity a) {
		M_Container c = new M_Container();
		c.add(new M_InfoText(R.drawable.ic_dialog_alert,
				"The application was closed by Android, it has to be reopened! "
						+ "Please reopen the application by "
						+ "clicking the icon in the application list."));
		c.add(new M_Button("Restart App") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					a.finish();
					Intent i = new Intent(Intent.ACTION_MAIN);
					PackageManager manager = a.getPackageManager();
					i = manager.getLaunchIntentForPackage(context
							.getPackageName());
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					int FLAG_ACTIVITY_CLEAR_TASK = 32768;
					i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
							| Intent.FLAG_ACTIVITY_NEW_TASK
							| FLAG_ACTIVITY_CLEAR_TASK);
					a.startActivity(i);
				} catch (Exception e) {
					Log.e(LOG_TAG, "" + e);
				}
				// if (DEBUG)
				// Log.w(LOG_TAG, "Killing complete process");
				// System.gc();
				// android.os.Process.killProcess(android.os.Process.myPid());
				// System.exit(1);
			}
		});
		return c;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		ActivityLifecycleListener m = getIfCorrectListener(
				ActivityLifecycleListener.class, getMyModifier());
		if (m != null && keyCode == KeyEvent.KEYCODE_BACK) {
			// moveTaskToBack(true);
			if (!m.onCloseWindowRequest(this)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DEBUG) {
			Log.d(LOG_TAG, "onSaveInstanceState" + " by " + this);
			Log.v(LOG_TAG, "    myModifier=" + getMyModifier());
			Log.v(LOG_TAG, "    requestCode=" + requestCode);
			Log.v(LOG_TAG, "    resultCode=" + resultCode);
			Log.v(LOG_TAG, "    data=" + data);
		}
		ActivityLifecycleListener m = getIfCorrectListener(
				ActivityLifecycleListener.class, getMyModifier());
		if (m != null) {
			m.onActivityResult(this, requestCode, resultCode, data);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		SimpleUI.onSaveInstanceState(this, outState);
		super.onSaveInstanceState(outState);
	}

	public static void onSaveInstanceState(SimpleUIInterface a, Bundle outState) {
		if (DEBUG) {
			Log.v(LOG_TAG, "onSaveInstanceState" + " by " + a);
		}
		if (a.getMyViewToShow() != null || a.getMyModifier() != null) {
			/*
			 * http://stackoverflow.com/questions/151777/how-do-i-save-an-android
			 * - applications-state
			 */
			SimpleUIRestoreContainer container = new SimpleUIRestoreContainer();
			container.myModifier = a.getMyModifier();
			container.ViewToShow = a.getMyViewToShow();
			String key = storeObjectInTransfairList((Context) a, container);
			if (DEBUG) {
				Log.i(LOG_TAG, "onSaveInstanceState - storing the UI ("
						+ container + ") via the key: " + key);
			}
			outState.putString(TRANSFAIR_KEY_ID, key);
		} else {
			Log.e(LOG_TAG, "Could not save the modifierToShow "
					+ "field because it was null!");
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getIfCorrectListener(Class<T> t,
			ModifierInterface startModifier) {
		try {
			if (t.isInstance(startModifier)) {
				return (T) startModifier;
			} else if (startModifier instanceof Collection) {
				@SuppressWarnings("rawtypes")
				Collection c = (Collection) startModifier;
				if (c.size() > 0) {
					Object firstElement = c.iterator().next();
					if (t.isInstance(firstElement)) {
						return (T) firstElement;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		OptionsMenuListener m = getIfCorrectListener(OptionsMenuListener.class,
				myModifier);
		if (m != null) {
			return m.onCreateOptionsMenu(this, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		OptionsMenuListener m = getIfCorrectListener(OptionsMenuListener.class,
				myModifier);
		if (m != null) {
			return m.onPrepareOptionsMenu(this, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		OptionsMenuListener m = getIfCorrectListener(OptionsMenuListener.class,
				myModifier);
		if (m != null) {
			return m.onOptionsItemSelected(this, item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		OptionsMenuListener m = getIfCorrectListener(OptionsMenuListener.class,
				myModifier);
		if (m != null) {
			m.onOptionsMenuClosed(this, menu);
		}
		super.onOptionsMenuClosed(menu);
	}

}
