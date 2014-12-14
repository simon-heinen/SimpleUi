package v2.simpleUi;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_CardView extends M_Collection {

	protected LinearLayout listItemContainer;
	protected LinearLayout outerContainer;

	@Override
	public CardView getView(Context context) {
		CardView card = new CardView(context);

		card.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		int shaddowSize = 20;
		card.setMaxCardElevation(shaddowSize);
		card.setCardElevation(shaddowSize);

		// container for header, list of items and footer:
		outerContainer = new LinearLayout(context);
		outerContainer.setOrientation(LinearLayout.VERTICAL);

		ScrollView scrollContainer = new ScrollView(context);

		listItemContainer = new LinearLayout(context);
		listItemContainer.setOrientation(LinearLayout.VERTICAL);
		if (size() > 0) {
			boolean firstEntryIsToolbar = get(0) instanceof M_Toolbar;
			createViewsForAllModifiers(context, listItemContainer,
					firstEntryIsToolbar);
			if (firstEntryIsToolbar) {
				outerContainer.addView(get(0).getView(context), 0);
			}
		}
		scrollContainer.addView(listItemContainer);
		outerContainer.addView(scrollContainer);
		card.addView(outerContainer);
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
