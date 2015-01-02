package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple "caption" which works well in combination with
 * {@link M_SeperatorLine}. You might also want to consider using a
 * {@link M_Toolbar} instead of an {@link M_Caption} to get closer to the normal
 * Android UI style which uses toolbars
 *
 */
public class M_Caption implements ModifierInterface, UiDecoratable {

	private String myText;
	private float mySizeFactor = 1.3f;
	private UiDecorator myDecorator;
	private OnClickListener myOnClickListener;
	private LinearLayout container;
	private TextView mTextView;
	private static Handler myHandler;

	public M_Caption(String text) {
		myText = text;
	}

	private static Handler getMyHandler() {
		if (myHandler == null) {
			myHandler = new Handler(Looper.getMainLooper());
		}
		return myHandler;
	}

	/**
	 * @param text
	 * @param sizeFactor
	 *            default is 1.1f (of normal size)
	 */
	public M_Caption(String text, float sizeFactor) {
		this(text);
		mySizeFactor = sizeFactor;
	}

	public void setMyText(final String newText) {
		this.myText = newText;
		if (mTextView != null) {
			getMyHandler().post(new Runnable() {

				@Override
				public void run() {
					mTextView.setText(newText);
				}
			});
		}
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.myOnClickListener = onClickListener;
	}

	@Override
	public View getView(Context context) {

		int bottomAndTopPadding = 4;
		int textPadding = 7;

		container = new LinearLayout(context);
		container.setGravity(Gravity.CENTER);

		container.setPadding(0, bottomAndTopPadding, 0, bottomAndTopPadding);

		mTextView = new TextView(context);
		mTextView.setText(myText);
		mTextView
				.setPadding(textPadding, textPadding, textPadding, textPadding);
		mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		if (myOnClickListener != null) {
			mTextView.setOnClickListener(myOnClickListener);
		}
		container.addView(mTextView);

		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mTextView.getTextSize() * mySizeFactor);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, container, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.decorate(context, mTextView, level + 1,
					UiDecorator.TYPE_CAPTION);
		}

		return container;
	}

	@Override
	public String toString() {
		if (myText != null) {
			return "Caption " + myText;
		}
		return super.toString();
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		return true;
	}

	public int getHeightInPixels() {
		if (container == null) {
			return 0;
		}
		return container.getHeight();
	}

}