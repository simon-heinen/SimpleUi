package glassUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tools.GlassEventListener;
import util.SystemUtil;
import android.app.Instrumentation;
import android.content.Context;
import android.view.KeyEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

/**
 * Override these two methods in your activity: <br>
 * <br>
 * public boolean onKeyDown(int keycode, KeyEvent event) { if (mGestureDetector
 * != null && SimpleGlassEvents.onKeyDown( (GlassEventListener) l, keycode)) {
 * return true; } return super.onKeyDown(keycode, event); } <br>
 * <br>
 * <br>
 * public boolean onGenericMotionEvent(MotionEvent event) { if (mGestureDetector
 * != null) { return mGestureDetector.onMotionEvent(event); } return
 * super.onGenericMotionEvent(event); }
 * 
 * 
 *
 */
public class SimpleGlassEvents {

	public static GestureDetector createGestureDetector(Context context,
			final GlassEventListener armroSetup) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					return armroSetup.onGlassTab();
				} else if (gesture == Gesture.TWO_TAP) {
					return armroSetup.onGlassTwoFingerTab();
				} else if (gesture == Gesture.SWIPE_RIGHT) {
					return armroSetup.onGlassSwipeRight();
				} else if (gesture == Gesture.SWIPE_LEFT) {
					return armroSetup.onGlassSwipeLeft();
				}
				return false;
			}
		});
		return gestureDetector;
	}

	public static boolean onKeyDown(GlassEventListener l, int keycode) {
		if (SystemUtil.isGlass()) {
			return false;
		}
		if (keycode == KeyEvent.KEYCODE_BACK) {
			return l.onGlassBackPressed();
		} else if (keycode == KeyEvent.KEYCODE_CAMERA) {
			return l.onGlassCameraPressed();
		}
		return false;
	}

	private static ExecutorService exe = Executors.newFixedThreadPool(1);

	/**
	 * If you have a UI with multiple buttons e.g. this method can help you to
	 * navigate through them using the Glass touchpad. It can be called when the
	 * user swipes left or right e.g.
	 */
	public static void scrollDown() {
		exe.submit(new Runnable() {

			@Override
			public void run() {
				Instrumentation inst = new Instrumentation();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
			}
		});
	}

	/**
	 * see {@link SimpleGlassEvents#scrollDown()}
	 */
	public static void scrollUp() {
		exe.submit(new Runnable() {

			@Override
			public void run() {
				Instrumentation inst = new Instrumentation();
				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
			}
		});
	}

}
