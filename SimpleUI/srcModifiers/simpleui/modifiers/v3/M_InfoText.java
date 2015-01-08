package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import simpleui.util.ImageTransform;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class M_InfoText implements ModifierInterface, UiDecoratable {

	private int TEXT_WEIGHT = 10;
	private int CAPTION_WEIGHT = 15;
	private String myCaptionText;
	private String myText;
	private Bitmap myIcon;
	private int myIconId;
	private float myTextSize;
	private UiDecorator myDecorator;
	private LinearLayout container;
	private boolean containsUrls;
	private TextView textView;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_InfoText(String text) {
		myText = text;
	}

	public M_InfoText(Bitmap icon, String text) {
		this(text);
		myIcon = icon;
	}

	public M_InfoText(Integer iconId, String text) {
		this(text);
		if (iconId != null) {
			myIconId = iconId;
		}
	}

	public M_InfoText(int iconId, String text, float manualTextSize) {
		this(iconId, text);
		myTextSize = manualTextSize;
	}

	public M_InfoText(String caption, String description) {
		this(description);
		myCaptionText = caption;
	}

	/**
	 * @param caption
	 * @param description
	 * @param captionWeight
	 *            less means more space for the caption
	 * @param textWeight
	 *            less means more space for the text
	 */
	public M_InfoText(String caption, String description, int captionWeight,
			int textWeight) {
		this(caption, description);
		CAPTION_WEIGHT = captionWeight;
		TEXT_WEIGHT = textWeight;
	}

	public void setContainsUrls(boolean containsUrls) {
		this.containsUrls = containsUrls;
		if (textView != null && containsUrls) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					textView.setAutoLinkMask(Linkify.ALL);
					textView.setText(textView.getText());
				}
			});
		}
	}

	@Override
	public View getView(Context context) {

		int textPadding = (int) ImageTransform.dipToPixels(
				context.getResources(), 5);
		int iconPadding = (int) ImageTransform.dipToPixels(
				context.getResources(), 5);

		container = new LinearLayout(context);

		// container.setPadding(0, bottomAndTopPadding, 0, bottomAndTopPadding);

		ImageView i = null;
		if (myIcon != null || myIconId != 0) {
			i = new ImageView(context);
			i.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
			if (myIcon != null) {
				i.setImageBitmap(myIcon);
			} else if (myIconId != 0) {
				i.setImageResource(myIconId);
			}
			container.addView(i);
		}

		textView = new TextView(context);
		if (containsUrls) {
			textView.setAutoLinkMask(Linkify.ALL);
		}
		textView.setText(myText);
		TextView captionView = null;
		if (myCaptionText != null) {

			LinearLayout.LayoutParams pCaption = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					CAPTION_WEIGHT);
			LinearLayout.LayoutParams pText = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					TEXT_WEIGHT);

			captionView = new TextView(context);
			captionView.setLayoutParams(pCaption);
			captionView.setText(myCaptionText);
			captionView.setPadding(textPadding, textPadding, 0, textPadding);
			container.addView(captionView);

			textView.setLayoutParams(pText);
		}

		textView.setPadding(textPadding, textPadding, textPadding, textPadding);
		container.addView(textView);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		int maxWidth = displayMetrics.widthPixels;
		int maxHeight = 800;
		container.measure(
				MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST));
		if (captionView != null) {
			// use the default gravity for the container (so do nothing)
		} else if (i == null
				|| textView.getMeasuredHeight() < i.getMeasuredHeight()) {
			container.setGravity(Gravity.CENTER_VERTICAL);
		}

		if (myTextSize != 0) {
			textView.setTextSize(myTextSize);
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			if (i != null) {
				myDecorator.decorate(context, i, level + 1,
						UiDecorator.TYPE_ICON);
			}
			myDecorator.decorate(context, textView, level + 1,
					UiDecorator.TYPE_INFO_TEXT);
		}
		return container;
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

	public void hide() {
		if (container != null) {
			container.setVisibility(View.GONE);
		}
	}

	public void setText(final String text) {
		if (textView != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					myText = text;
					textView.setText(text);
				}
			});
		}
	}

	public String getText() {
		if (textView != null) {
			return (String) textView.getText();
		}
		return myText;
	}

}