package simpleui.customViews;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Simon on 10.03.14.
 */
public class TimeView extends TextView {

	android.os.Handler uiThread = new android.os.Handler(Looper.getMainLooper());

	private long creationTime;
	private String lastTimeDif;

	public TimeView(Context context) {
		super(context);
	}

	public TimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static void main(String[] args) {
		System.out.println(calcTimeDif(new Date().getTime() + 10 * 60 * 1000));
	}

	public static String calcTimeDif(long creationTime) {
		int dif = (int) ((new Date().getTime() - creationTime) / 1000 / 60); // in
																				// minutes

		if (dif < 0) {
			return "";
		}

		if (dif < 60) {
			return dif + "m";
		}
		dif = dif / 60; // in hours
		if (dif < 24) {
			return dif + "h";
		}
		dif = dif / 24; // in days
		return dif + "d";
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
		uiThread.post(new Runnable() {
			@Override
			public void run() {
				refreshUi();
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		refreshUi();
		super.onDraw(canvas);
	}

	private void refreshUi() {
		String timeDif = calcTimeDif(creationTime);
		if (!timeDif.equals(lastTimeDif)) {
			lastTimeDif = timeDif;
			setText(timeDif); // TODO
		}
	}
}
