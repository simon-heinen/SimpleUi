package v2.simpleUi;

import v2.simpleUi.customViews.ExpandableLinearLayout;
import v2.simpleUi.customViews.ExpandableLinearLayout.OnExpandListener;
import v2.simpleUi.uiDecoration.UiDecoratable;
import v2.simpleUi.uiDecoration.UiDecorator;
import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A collapsible container
 * 
 * @author Spobo
 * 
 */
public class M_Container2 extends M_Collection implements UiDecoratable {

	private static final float CAPTION_SIZE = 1.1f;
	protected static final String LOG_TAG = "M_Container2";
	private OnExpandListener listener;
	private ExpandableLinearLayout expandablePanel;
	private boolean collapsed = false;
	private UiDecorator decorator;
	private Drawable backgroundDrawable;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_Container2(String title, boolean collapsed) {
		this(title);
		this.collapsed = collapsed;
	}

	public M_Container2(String title) {

		final M_IconButtonWithText expandButton = new M_IconButtonWithText(
				R.drawable.arrow_up_float) {
			@Override
			public void onClick(Context context, ImageView clickedButton) {
				expandablePanel.switchBetweenCollapsedAndExpandedMode();
			}
		};

		final M_Caption caption = new M_Caption(title, CAPTION_SIZE);
		caption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				expandablePanel.switchBetweenCollapsedAndExpandedMode();
			}
		});

		listener = new OnExpandListener() {

			@Override
			public void onExpandFinished(Context context, View content) {
				expandButton.setIconId(R.drawable.arrow_up_float);
			}

			@Override
			public void onCollapseFinished(Context context, View content) {
				expandButton.setIconId(R.drawable.arrow_down_float);
			}

			@Override
			public void onCollapseStart(Context context,
					ExpandableLinearLayout v) {
				ImageView b = expandButton.getImageButton();
				if (b != null) {
					b.setAnimation(newRotateAnimation(b, 0, 180));
				}
			}

			@Override
			public void onExpandStart(Context context, ExpandableLinearLayout v) {
				ImageView b = expandButton.getImageButton();
				if (b != null) {
					b.setAnimation(newRotateAnimation(b, 0, -180));
				}
			}

			private Animation newRotateAnimation(ImageView b, float x, float y) {
				// Create an animation instance
				RotateAnimation a = new RotateAnimation(x, y,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				// a.setRepeatCount(0);
				// Set the animation's parameters
				a.setDuration(600); // duration in ms
				a.setInterpolator(new LinearInterpolator());
				a.setFillAfter(true);
				return a;
			}

			@Override
			public void onViewWasDrawnFirstTime(Context context,
					ExpandableLinearLayout expandableLinearLayout) {
				int height = caption.getHeightInPixels() + 13;
				expandableLinearLayout.setCollapsedHeight(height);
				if (collapsed) {
					expandablePanel.collapse();
				}
			}
		};
		initHeaderOfContainer(expandButton, caption);
	}

	public M_Container2() {
	}

	public void initHeaderOfContainer(final M_IconButtonWithText expandButton,
			final M_Caption caption) {
		add(new M_LeftRight(expandButton, 1, caption, 5));
	}

	@Override
	public boolean isEmpty() {
		// the first item is the caption
		return size() < 2;
	}

	/**
	 * removes all views for the modifiers and creates new ones, this is usefull
	 * when the container is changed for example
	 */
	public void rebuiltUI() {
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				if (!invalidate()) {
					Log.w(LOG_TAG, "Could not invalidate the ui");
				}
			}
		});

	}

	/**
	 * removes all views for the modifiers and creates new ones, this is usefull
	 * when the container is changed for example
	 */
	public boolean invalidate() {
		if (expandablePanel != null) {
			if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
				removeAllOldViewsAndAddAllModifiersAgain();
			} else {
				myHandler.post(new Runnable() {

					@Override
					public void run() {
						removeAllOldViewsAndAddAllModifiersAgain();
					}
				});
			}
			return true;
		}
		return false;
	}

	private void removeAllOldViewsAndAddAllModifiersAgain() {
		expandablePanel.removeAllViews();
		fillPanel(expandablePanel.getContext(), expandablePanel);
	}

	@Override
	public View getView(Context context) {
		expandablePanel = new ExpandableLinearLayout(context, null, listener);
		fillPanel(context, expandablePanel);
		if (backgroundDrawable != null) {
			expandablePanel.setBackgroundDrawable(backgroundDrawable);
		}

		// LinearLayout outerContainer = new LinearLayout(context);
		// ScrollView scrollContainer = new ScrollView(context);
		// outerContainer.addView(scrollContainer);
		// scrollContainer.addView(expandablePanel);

		return expandablePanel;
	}

	protected void fillPanel(Context context, LinearLayout targetBox) {

		UiDecorator uiDecorator = this.decorator;

		if (uiDecorator != null) {
			int level = uiDecorator.getCurrentLevel();
			uiDecorator.decorate(context, targetBox, level,
					UiDecorator.TYPE_CONTAINER);
			uiDecorator.setCurrentLevel(level + 1);
		}

		for (int i = 0; i < this.size(); i++) {
			ModifierInterface m = this.get(i);
			if (m != null) {
				View v = m.getView(context);
				targetBox.addView(v);
			}
		}

		if (uiDecorator != null) {
			/*
			 * Then reduce level again to the previous value
			 */
			uiDecorator.setCurrentLevel(uiDecorator.getCurrentLevel() - 1);
		}
	}

	@Override
	public boolean save() {
		for (ModifierInterface m : this) {
			if (m != null && !m.save()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		boolean result = true;
		this.decorator = decorator;
		for (ModifierInterface m : this) {
			if (m instanceof UiDecoratable) {
				result &= ((UiDecoratable) m).assignNewDecorator(decorator);
			} else {
				/*
				 * if not all children are UiDecoratables the overall result
				 * will be false
				 */
				result = false;
			}
		}
		return result;
	}

	public void setBackgroundDrawable(final Drawable drawable) {
		backgroundDrawable = drawable;
		if (expandablePanel != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					expandablePanel.setBackgroundDrawable(drawable);
				}
			});
		}
	}

}
