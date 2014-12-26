package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Use {@link M_Button#M_Button(String, boolean)} instead
 *
 */
@Deprecated
public abstract class M_ButtonBorderless implements ModifierInterface,
		UiDecoratable {

	private String myText;
	private UiDecorator myDecorator;
	private Integer myIconId;
	private ImageView imageButton;

	public M_ButtonBorderless(Integer iconId) {
		myIconId = iconId;
	}

	public M_ButtonBorderless(Integer iconId, String buttonText) {
		this(iconId);
		myText = buttonText;
	}

	public M_ButtonBorderless(String buttonText) {
		this(null, buttonText);
	}

	@Override
	public View getView(final Context context) {

		final LinearLayout l = new LinearLayout(context);

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		l.setOrientation(LinearLayout.VERTICAL);
		l.setLayoutParams(params);

		// l.setGravity(Gravity.CENTER_HORIZONTAL);

		if (myIconId != null) {
			imageButton = new ImageView(context);
			LayoutParams imparams = new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			int p = 3;
			imparams.setMargins(p, p, p, p);
			imageButton.setLayoutParams(imparams);
			imageButton.setImageResource(myIconId);
			l.addView(imageButton);
		}

		l.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				M_ButtonBorderless.this.onClick(context, l);
			}
		});

		l.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return M_ButtonBorderless.this.onLongClick(context, l);
			}
		});

		TextView t = null;
		if (myText != null) {
			t = new TextView(context);
			t.setText(myText);
			t.setGravity(Gravity.CENTER_HORIZONTAL);
			l.addView(t);
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, imageButton, level + 1,
					UiDecorator.TYPE_ICON);
			if (t != null) {
				myDecorator.decorate(context, t, level + 1,
						UiDecorator.TYPE_INFO_TEXT);
			}
		}

		return l;

	}

	public ImageView getImageButton() {
		return imageButton;
	}

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	public abstract void onClick(Context context, View clickedButton);

	public boolean onLongClick(Context context, View clickedButton) {
		return false;
	};

	public void setIconId(int iconId) {
		myIconId = iconId;
		if (imageButton != null) {
			imageButton.setImageResource(myIconId);
		}
	}

}