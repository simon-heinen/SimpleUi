package simpleui.modifiers.v3.maps;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SimpleGestureListener extends
		GestureDetector.SimpleOnGestureListener {

	public interface SimpleTouchEventInterface {

		void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY);

		void onLongPress(MotionEvent e);

		void onSingleTab(MotionEvent e);

		void onDoubleTap(MotionEvent e);

	}

	private SimpleTouchEventInterface myListener;

	public SimpleGestureListener(SimpleTouchEventInterface listener) {
		myListener = listener;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		/*
		 * return true so that the GestureListener nows he can go on and detect
		 * the gesture..
		 */
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		myListener.onScroll(e1, e2, distanceX, distanceY);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		myListener.onLongPress(e);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		myListener.onSingleTab(e);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		myListener.onDoubleTap(e);
		return true;
	}

}
