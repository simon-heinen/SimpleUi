package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.googlecode.simpleui.library.R;

public abstract class M_Button implements ModifierInterface, UiDecoratable {

	private String myText;
	private UiDecorator myDecorator;
	private Integer myIconId;
	private Drawable myIcon;
	private Button button;
	private boolean enabled = true;
	private OnLongClickListener longClickListener;
	private OnClickListener clickListener;
	private UiCreateListener<Button> uiListener;
	private boolean borderless = false;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_Button(String buttonText) {
		myText = buttonText;
	}

	/**
	 * @param buttonText
	 * @param borderless
	 *            pass true to do not show the normal button border
	 */
	public M_Button(String buttonText, boolean borderless) {
		this(buttonText);
		this.borderless = borderless;
	}

	public void setBorderless(boolean borderless) {
		this.borderless = borderless;
	}

	public M_Button(Integer iconIdOnLeftSideOfText, String buttonText) {
		this(buttonText);
		myIconId = iconIdOnLeftSideOfText;
	}

	public M_Button(Drawable iconOnLeftSideOfText, String buttonText) {
		this(buttonText);
		myIcon = iconOnLeftSideOfText;
	}

	public String getText() {
		return myText;
	}

	public void setLongClickListener(OnLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
		if (button != null) {
			button.setOnLongClickListener(longClickListener);
		}
	}

	public void setClickListener(OnClickListener clickListener) {
		this.clickListener = clickListener;
		if (button != null) {
			button.setOnClickListener(clickListener);
		}
	}

	public void setUiCreateListener(UiCreateListener<Button> l) {
		this.uiListener = l;
	}

	public OnLongClickListener getLongClickListener() {
		return longClickListener;
	}

	@Override
	public View getView(Context context) {
		if (borderless
				&& android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			// the button should have no borders so use the borderless button
			// theme when inflating from the XML:
			context = new ContextThemeWrapper(context,
					R.style.BorderlessButtonStyle);
		}
		button = (Button) View.inflate(context,
				R.layout.material_factory_button, null);

		Drawable drawable = null;
		if (myIconId != null) {
			try {
				drawable = context.getResources().getDrawable(myIconId);
			} catch (NotFoundException e) {
				e.printStackTrace();
				drawable = myIcon;
			}
		} else if (myIcon != null) {
			drawable = myIcon;
		}
		if (drawable != null) {
			button.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
					null, null);
		}
		button.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		if (clickListener == null) {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					M_Button.this.onClick(v.getContext(), (Button) v);
				}

			};
		}
		button.setOnClickListener(clickListener);
		if (longClickListener != null) {
			button.setOnLongClickListener(longClickListener);
		}
		if (myText != null) {
			button.setText(myText);
		}
		button.setEnabled(enabled);
		if (button.getPaddingLeft() == 0) {
			int p = 12;
			button.setPadding(p, p, p, p);
		}
		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, button, level + 1,
					UiDecorator.TYPE_BUTTON);
		}
		if (uiListener != null) {
			uiListener.onUiCreated(button);
		}

		return button;
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

	public abstract void onClick(Context context, Button clickedButton);

	public void enable() {
		enabled = true;
		setButtonEnabledOrDisabled();
	}

	public void setText(String myText) {
		this.myText = myText;
		if (button != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (button != null) {
						button.setText(M_Button.this.myText);
					}
				}
			});
		}
	}

	private void setButtonEnabledOrDisabled() {
		if (button != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (button != null) {
						button.setEnabled(enabled);
					}
				}
			});
		}
	}

	public void disable() {
		enabled = false;
		setButtonEnabledOrDisabled();
	}

}