package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.ColorUtils;
import simpleui.util.ImageTransform;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_CardView extends M_Collection {

	private static final String LOG_TAG = M_CardView.class.getSimpleName();
	public static final int DEFAULT_CHILDREN_PADDING = 4;
	public static final int DEFAULT_SHADDOW_SIZE = 20;
	private static Handler myHandler = new Handler(Looper.getMainLooper());
	private Integer backgroundColor;
	private CardView card;

	@Override
	public CardView getView(Context context) {

		// container for header, list of items and footer:
		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		card = newCardViewWithContainers(context, outerContainer,
				listItemContainer, DEFAULT_SHADDOW_SIZE);
		if (backgroundColor != null) {
			card.setCardBackgroundColor(backgroundColor);
		}
		if (size() > 0) {
			boolean firstEntryIsToolbar = get(0) instanceof M_Toolbar;
			createViewsForAllModifiers(context, listItemContainer,
					firstEntryIsToolbar);
			if (firstEntryIsToolbar) {
				outerContainer.addView(get(0).getView(context), 0);
			}
		}

		return card;
	}

	public static CardView newCardViewWithContainers(Context context,
			LinearLayout outerContainer, LinearLayout listItemContainer,
			int shaddowSize) {
		CardView card = newCardView(context, shaddowSize);
		outerContainer.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollContainer = new ScrollView(context);
		listItemContainer.setOrientation(LinearLayout.VERTICAL);
		int p = (int) ImageTransform.dipToPixels(context,
				DEFAULT_CHILDREN_PADDING);
		listItemContainer.setPadding(p, p, p, p);
		scrollContainer.addView(listItemContainer);
		outerContainer.addView(scrollContainer);
		card.addView(outerContainer);
		return card;
	}

	public static CardView newCardView(Context context, int shaddowSize) {
		CardView card = new CardView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if (Build.VERSION_CODES.LOLLIPOP == Build.VERSION.SDK_INT) {
			params.setMargins(shaddowSize, shaddowSize, shaddowSize,
					shaddowSize);
		} else {
			card.setMaxCardElevation(shaddowSize);
		}
		card.setCardElevation(shaddowSize);
		card.setLayoutParams(params);
		card.setCardBackgroundColor(ColorUtils.getDefaultBackgroundColor(
				context, 0xFF00FF));
		return card;
	}

	public void setBackgroundColor(Integer newBackgroundColor) {
		this.backgroundColor = newBackgroundColor;
		if (card != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					card.setCardBackgroundColor(backgroundColor);
				}
			});
		}
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (ModifierInterface m : this) {
			if (m != null) {
				result &= m.save();
			}
		}
		return result;
	}

}
