package simpleui.util;

import java.util.ArrayList;
import java.util.Arrays;

import simpleui.SimpleUI;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Caption;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.googlecode.simpleui.library.R;

/**
 * Allows to give feedback that something is going on and that the user should
 * wait
 * 
 * @author Simon Heinen
 * 
 */
public abstract class ProgressScreen implements ModifierInterface,
		ActivityLifecycleListener {

	protected static final String LOG_TAG = "ProgressScreen";
	private static final ColorUtils BACKGROUND = ColorUtils
			.newBackRadialBackgroundWithTransparency(160, 230);
	private static final int MOST_OUTER_PADDING = 17;
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = android.graphics.Color
			.argb(90, 0, 0, 0);
	public static final int DEFAULT_SPEED = 700;

	private ArrayList<Integer> mImageIds;
	private final int mIterateSpeedInMs;
	/**
	 * Set is as early as possible to true and as late as possible to false!
	 */
	private volatile boolean mKeepRunning;
	private Activity mActivityToClose;
	private final String mText;
	private int currentImageNr;
	private ImageView mImageView;
	private M_Caption mCaption;
	/**
	 * This is true if the start and finish requests were executed right after
	 * another
	 */
	private boolean mActivityAlreadyClosedAgain;
	private volatile static Handler myHandler = new Handler(
			Looper.getMainLooper());

	public ProgressScreen() {
		this(null, DEFAULT_SPEED);
	}

	public ProgressScreen(String text, int iterateSpeedInMs) {
		this.mText = text;
		this.mIterateSpeedInMs = iterateSpeedInMs;
	}

	public synchronized void start(Context context) {
		if (!mKeepRunning) {
			mKeepRunning = true;
			mActivityAlreadyClosedAgain = false;
			Log.i(LOG_TAG, "Starting screen " + this + " normally");
			SimpleUI.showUi(context, this);
		} else {
			Log.w(LOG_TAG,
					"Progress screen was already running, no need to start it");
		}
	}

	/**
	 * call {@link ProgressScreen#finish()}
	 */
	@Deprecated
	public void stop() {
		finish();
	}

	public synchronized void finish() {
		if (!mKeepRunning) {
			Log.w(LOG_TAG,
					"Progress screen wasnt running, no need to stop it, will do so anyway");
		} else {
			Log.i(LOG_TAG, "Stoped progress screen normally");
		}
		mActivityAlreadyClosedAgain = true;
		if (mActivityToClose != null) {
			mActivityToClose.finish();
		} else {
			Log.e(LOG_TAG, "mActivityToClose was null, cant close screen");
		}
	}

	/**
	 * @param imageIds
	 *            these will be the images which will be iterated
	 */
	public void setProgressImageIds(Integer... imageIds) {
		mImageIds = new ArrayList<Integer>(Arrays.asList(imageIds));
	}

	@Override
	public View getView(Context context) {
		Log.d(LOG_TAG, "Displaying screen " + this + " with context=" + context);
		if (context instanceof Activity) {
			mActivityToClose = (Activity) context;
			if (mActivityAlreadyClosedAgain) {
				mActivityToClose.finish();
				return null;
			}
		} else {
			Log.e(LOG_TAG, "Passed context=" + context
					+ " was not an activity!");
		}

		LinearLayout mostOuterBox = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mostOuterBox.setLayoutParams(params);
		LinearLayout l = mostOuterBox;
		l.setGravity(Gravity.CENTER);
		l.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);
		int p = MOST_OUTER_PADDING;
		l.setPadding(p, p, p, p);

		LinearLayout innerBox = new LinearLayout(context);
		l = innerBox;
		innerBox.setOrientation(LinearLayout.VERTICAL);
		p = 2 * MOST_OUTER_PADDING;
		l.setPadding(p, (int) (p * 1.5f), p, p);
		mostOuterBox.addView(innerBox);
		BACKGROUND.applyBackgroundTo(innerBox);

		getProgressUi(context, innerBox);
		return mostOuterBox;
	}

	protected void getProgressUi(Context context, LinearLayout container) {
		if (mImageIds == null || mImageIds.isEmpty()) {
			container.addView(View.inflate(context,
					R.layout.material_factory_progressbar, null));
		} else {
			mImageView = new ImageView(context);

			container.addView(mImageView);
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (mKeepRunning) {
						myHandler.post(new Runnable() {
							@Override
							public void run() {
								onProgressUpdate();
							}
						});
						try {
							// Log.e(LOG_TAG, "Will sleep " +
							// mIterateSpeedInMs);
							Thread.sleep(mIterateSpeedInMs);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					finish();
				}
			}).start();
		}
		if (mText != null) {
			mCaption = new M_Caption(mText);
			container.addView(mCaption.getView(context));
		}
	}

	protected void onProgressUpdate() {
		mImageView.setImageResource(mImageIds.get(currentImageNr));
		currentImageNr++;
		if (currentImageNr >= mImageIds.size()) {
			currentImageNr = 0;
		}
		String s = onUpdateTextRequest(currentImageNr);
		if (mCaption != null && s != null) {
			mCaption.setMyText(s);
		}
	}

	protected String onUpdateTextRequest(int loopStep) {
		return null;
	}

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {
	}

	@Override
	public void onStop(Activity activity) {
		mKeepRunning = false;
	}

	@Override
	public boolean onCloseWindowRequest(Activity activity) {
		if (onAbortRequest()) {
			return true;
		}
		return false;
	}

	/**
	 * @return true if the waiting screen should be allowed to close
	 */
	public abstract boolean onAbortRequest();

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
	}

}
