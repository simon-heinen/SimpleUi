package simpleui.util;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_InfoText;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.googlecode.simpleui.library.R;

public class ToastV2 {

	public static int LENGTH_SHORT = 2500;
	public static int LENGTH_LONG = 5000;

	/**
	 * @param context
	 * @param deleteInfoText
	 *            something like "Item deleted"
	 * @param undoButtonText
	 *            something like "Undo"
	 * @param undoClickListener
	 *            notified when the user clicks undo
	 */
	public static PopupWindow showUndoToast(Context context,
			String deleteInfoText, String undoButtonText,
			final OnClickListener undoClickListener) {
		ModifierInterface text = new M_InfoText(
				android.R.drawable.ic_menu_delete, deleteInfoText);
		M_Button button = new M_Button(android.R.drawable.ic_menu_revert,
				undoButtonText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
			} // will not be called, default click listener overwritten below
		};
		ModifierInterface h = new M_HalfHalf(text, button, 1, 2);
		final PopupWindow toast = showToast(context, h, LENGTH_LONG);
		button.setClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				undoClickListener.onClick(v);
				toast.dismiss();
			}
		});
		return toast;
	}

	public static PopupWindow showToast(Context context,
			ModifierInterface modifierToShow, int durationInMs) {
		return showToast(context, modifierToShow.getView(context), durationInMs);
	}

	public static PopupWindow showToast(Context context, View viewToShow,
			int durationInMs) {
		return showToast(context, viewToShow, durationInMs, Gravity.BOTTOM,
				Gravity.CENTER_HORIZONTAL);
	}

	/**
	 * @param context
	 * @param viewToShow
	 * @param durationInMs
	 *            0=will never disappear
	 * @param verticGravity
	 *            e.g. {@link Gravity#BOTTOM}
	 * @param horizGravity
	 *            e.g. {@link Gravity#CENTER_HORIZONTAL}
	 * @return
	 */
	public static PopupWindow showToast(Context context, View viewToShow,
			int durationInMs, int verticGravity, int horizGravity) {
		CardView v = M_Container.newCardView(context, 20);
		v.addView(viewToShow);
		final PopupWindow mUndoPopup = new PopupWindow(v);
		mUndoPopup.setAnimationStyle(R.style.fade_animation);
		mUndoPopup.setWidth(LayoutParams.WRAP_CONTENT);
		mUndoPopup.setHeight(LayoutParams.WRAP_CONTENT);
		if (durationInMs > 0) {
			durationInMs = (durationInMs < 3) ? durationInMs * 2500
					: durationInMs;
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mUndoPopup.dismiss();
				}
			}, durationInMs);
		}
		mUndoPopup.showAtLocation(new View(context), horizGravity
				| verticGravity, 0, (int) (context.getResources()
				.getDisplayMetrics().density * 15));

		return mUndoPopup;
	}

}
