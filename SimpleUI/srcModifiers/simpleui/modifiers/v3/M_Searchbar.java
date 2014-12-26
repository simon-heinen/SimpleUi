package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Has a text input area and a name
 * 
 * @author Spobo
 * 
 */
public abstract class M_Searchbar implements ModifierInterface, UiDecoratable {
	private static final String LOG_TAG = "M_TextModifier";
	private EditText editText;
	private UiDecorator myDecorator;
	private boolean editable = true;
	private boolean isLongText;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;
	private boolean horizontalScrollable;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	private OnClickListener myNotEditableInfo;
	private String additionalInfoText;
	private boolean inPasswordInputMode;
	private boolean autoSuggestionsEnabled = true;

	public M_Searchbar() {
	}

	public M_Searchbar(boolean editable, boolean isLongText,
			boolean horizontalScrollable) {
		setLongText(isLongText);
		setEditable(editable);
		setHorizontalScrollable(horizontalScrollable);
	}

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfInputText = weightOfInputText;
	}

	public abstract String getVarName();

	public boolean isLongText() {
		return isLongText;
	}

	public void setLongText(boolean isLongText) {
		this.isLongText = isLongText;
	}

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfInputText);

		if (isLongText()) {
			int m = 8;
			p.setMargins(2 * m, m, 2 * m, m);
			l.setOrientation(LinearLayout.VERTICAL);
		}

		// TODO replace by better view representative:
		editText = (EditText) View
				.inflate(
						context,
						com.googlecode.simpleui.library.R.layout.material_factory_edittext,
						null);
		editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (horizontalScrollable) {
					editText.getParent().requestDisallowInterceptTouchEvent(
							true);
				}
				return false;
			}
		});
		String varName = this.getVarName();
		if (varName != null) {
			editText.setHint(varName);
		}

		if (inPasswordInputMode) {
			setEditTextToPWMode();
		}
		if (!autoSuggestionsEnabled) {
			setAutoSuggestions(autoSuggestionsEnabled);
		}
		editText.setLayoutParams(p);
		setInfoText(additionalInfoText);
		setEditable(isEditable());

		applyTextFilterIfNeeded(editText);
		l.addView(editText);

		ImageButton b = new ImageButton(context);
		b.setImageResource(getButtonIcon());
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSearchRequest(editText.getText().toString());
			}
		});

		l.addView(b);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, editText, level + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}

		return l;
	}

	public int getButtonIcon() {
		return R.drawable.ic_menu_search;
	}

	public abstract void onSearchRequest(String searchText);

	public void setHorizontalScrollable(boolean horizontalScrollable) {
		this.horizontalScrollable = horizontalScrollable;
	}

	public boolean isHorizontalScrollable() {
		return horizontalScrollable;
	}

	public void setInfoText(String infoText) {
		this.additionalInfoText = infoText;
		if (additionalInfoText != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					if (editText != null) {
						editText.setHint(additionalInfoText);
					}
				}
			});
		}
	}

	public void applyTextFilterIfNeeded(EditText e) {

	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (editText != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (!isEditable()) {
						if (myNotEditableInfo == null) {
							editText.setOnClickListener(null);
							editText.setEnabled(false);
						} else {
							editText.setEnabled(true);
							editText.getBackground().setColorFilter(Color.GRAY,
									PorterDuff.Mode.MULTIPLY);
							editText.setOnClickListener(myNotEditableInfo);
						}
					} else {
						editText.setOnClickListener(null);
						editText.getBackground().setColorFilter(Color.WHITE,
								PorterDuff.Mode.MULTIPLY);
						editText.setEnabled(true);
					}

					editText.setFocusable(isEditable());
					editText.setFocusableInTouchMode(isEditable());

				}
			});
		}
	}

	public static Handler getMyHandler() {
		return myHandler;
	}

	public EditText getEditText() {
		return editText;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public boolean save() {
		if (!editable) {
			return true;
		}
		if (save(editText.getText().toString())) {
			return true;
		}
		// Log.d(LOG_TAG, "Modifier for " + getVarName() +
		// " is requesting focus");
		getEditText().requestFocus();
		return false;
	}

	public boolean save(String newValue) {
		// on default do nothing, normally used via search button
		return true;
	}

	public void setNotEditableInfo(OnClickListener onClickListener) {
		myNotEditableInfo = onClickListener;
	}

	public void isPasswordInput(boolean passwordInputMode) {
		this.inPasswordInputMode = passwordInputMode;
		if (editText != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					if (inPasswordInputMode) {
						setEditTextToPWMode();
					} else {
						editText.setTransformationMethod(null);
						editText.setInputType(InputType.TYPE_CLASS_TEXT);
					}
				}
			});
		}
	}

	public void setAutoSuggestions(final boolean autoSuggestionsEnabled) {
		this.autoSuggestionsEnabled = autoSuggestionsEnabled;
		if (editText != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					if (!autoSuggestionsEnabled) {
						editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					} else {
						editText.setTransformationMethod(null);
						editText.setInputType(InputType.TYPE_CLASS_TEXT);
					}
				}
			});
		}
	}

	private void setEditTextToPWMode() {
		editText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		editText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}

	public String getCurrentTextValue() {
		if (editText != null) {
			return editText.getText().toString();
		}
		return null;
	}

}