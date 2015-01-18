package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.ImageTransform;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.widget.LinearLayout;

/**
 * Use {@link M_Container} instead (Adding {@link M_Container}s to an
 * {@link M_Container} is possible to group information in cards)
 *
 */
@Deprecated
public class M_CardView extends M_Collection {

	private static Handler myHandler = new Handler(Looper.getMainLooper());
	private Integer backgroundColor;
	private CardView card;

	@Override
	public CardView getView(Context context) {

		// container for header, list of items and footer:
		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		int shadowSizeInDip = (int) ImageTransform.dipToPixels(context,
				M_Container.DEFAULT_SHADDOW_SIZE_IN_DIP);
		card = M_Container.newCardViewWithContainers(context, outerContainer,
				listItemContainer, shadowSizeInDip);
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
