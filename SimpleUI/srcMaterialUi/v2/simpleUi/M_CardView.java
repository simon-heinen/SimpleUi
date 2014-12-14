package v2.simpleUi;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_CardView extends M_Collection {

	@Override
	public CardView getView(Context context) {

		// container for header, list of items and footer:
		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		CardView card = newCardView(context, outerContainer, listItemContainer);
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

	public static CardView newCardView(Context context,
			LinearLayout outerContainer, LinearLayout listItemContainer) {
		CardView card = new CardView(context);
		card.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		int shaddowSize = 20;
		card.setMaxCardElevation(shaddowSize);
		card.setCardElevation(shaddowSize);
		outerContainer.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollContainer = new ScrollView(context);
		listItemContainer.setOrientation(LinearLayout.VERTICAL);
		scrollContainer.addView(listItemContainer);
		outerContainer.addView(scrollContainer);
		card.addView(outerContainer);

		TypedArray array = context.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.colorBackground });
		card.setCardBackgroundColor(array.getColor(0, 0xFF00FF));

		return card;
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