package simpleui.modifiers.v1;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import simpleui.modifiers.v3.M_CardView;
import simpleui.modifiers.v3.M_Collection;
import simpleui.modifiers.v3.M_Container;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Use {@link M_Container} or {@link M_CardView} instead
 *
 */
@Deprecated
public class M_Container3 extends M_Collection implements UiDecoratable {

	private static final float CAPTION_SIZE = 1.1f;
	protected static final String LOG_TAG = "M_Container2";
	private LinearLayout containerView;
	private UiDecorator decorator;
	private Drawable backgroundDrawable;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_Container3() {
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
		if (containerView != null) {
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
		containerView.removeAllViews();
		fillPanel(containerView.getContext(), containerView);
	}

	@Override
	public View getView(Context context) {

		LinearLayout outerContainer = new LinearLayout(context);
		outerContainer.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollContainer = new ScrollView(context);
		outerContainer.addView(scrollContainer);

		UiDecorator uiDecorator = this.decorator;

		if (uiDecorator != null) {
			uiDecorator.decorate(context, outerContainer,
					uiDecorator.getCurrentLevel(), UiDecorator.TYPE_CONTAINER);
			uiDecorator.decorate(context, scrollContainer,
					uiDecorator.getCurrentLevel() + 1,
					UiDecorator.TYPE_CONTAINER);
			uiDecorator.setCurrentLevel(uiDecorator.getCurrentLevel() + 2);
		}

		containerView = new LinearLayout(context);
		containerView.setOrientation(LinearLayout.VERTICAL);
		fillPanel(context, containerView);
		if (backgroundDrawable != null) {
			containerView.setBackgroundDrawable(backgroundDrawable);
		}
		scrollContainer.addView(containerView);

		if (uiDecorator != null) {
			/*
			 * Then reduce level again to the previous value
			 */
			uiDecorator.setCurrentLevel(uiDecorator.getCurrentLevel() - 2);
		}

		return outerContainer;
	}

	protected void fillPanel(Context context, LinearLayout targetBox) {

		UiDecorator uiDecorator = this.decorator;

		if (uiDecorator != null) {
			uiDecorator.decorate(context, targetBox,
					uiDecorator.getCurrentLevel(), UiDecorator.TYPE_CONTAINER);
			uiDecorator.setCurrentLevel(uiDecorator.getCurrentLevel() + 1);
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
		if (containerView != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					containerView.setBackgroundDrawable(drawable);
				}
			});
		}
	}

}
