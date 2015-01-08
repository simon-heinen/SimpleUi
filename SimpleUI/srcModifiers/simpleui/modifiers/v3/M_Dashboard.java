package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Dashboard design with 2 elements per row, useful for Buttons e.g.
 */
public class M_Dashboard extends M_Container {
	@Override
	protected void createViewsForAllModifiers(Context target,
			ViewGroup containerForAllItems) {
		boolean sizeIsMod2 = this.size() % 2 == 0;
		int size = sizeIsMod2 ? this.size() : this.size() - 1;
		for (int i = 0; i < size; i += 2) {
			containerForAllItems.addView(get2ItemViewFor(get(i), get(i + 1),
					target));
		}
		if (!sizeIsMod2) {
			// if its not a mod 2 == 0 number then add the last one manually
			containerForAllItems.addView(this.get(this.size() - 1).getView(
					target));
		}
	}

	private View get2ItemViewFor(ModifierInterface left,
			ModifierInterface right, Context context) {
		M_HalfHalf line = new M_HalfHalf(left, right, 70, true);
		return line.getView(context);
	}
}
