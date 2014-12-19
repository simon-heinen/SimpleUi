package v2.simpleUi;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_CardView extends M_Collection {

	private static Handler myHandler = new Handler(Looper.getMainLooper());
	private Integer backgroundColor;
	private CardView card;

	@Override
	public CardView getView(Context context) {

		// container for header, list of items and footer:
		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		card = newCardViewWithContainers(context, outerContainer, listItemContainer);
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
			LinearLayout outerContainer, LinearLayout listItemContainer) {
		CardView card = newCardView(context, 20);
		outerContainer.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollContainer = new ScrollView(context);
		listItemContainer.setOrientation(LinearLayout.VERTICAL);
		scrollContainer.addView(listItemContainer);
		outerContainer.addView(scrollContainer);
		card.addView(outerContainer);
		return card;
	}

	public static CardView newCardView(Context context, int shaddowSize) {
		CardView card = new CardView(context);
		card.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		card.setMaxCardElevation(shaddowSize);
		card.setCardElevation(shaddowSize);
		try {
			TypedArray array = context.getTheme().obtainStyledAttributes(
					new int[] { android.R.attr.colorBackground });
			card.setCardBackgroundColor(array.getColor(0, 0xFF00FF));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
