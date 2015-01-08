package simpleui.modifiers.v3;

import java.util.ArrayList;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class M_Collection extends ArrayList<ModifierInterface>
		implements ModifierInterface {
	protected void createViewsForAllModifiers(Context context,
			ViewGroup containerForAllItems) {
		createViewsForAllModifiers(context, containerForAllItems, false);
	}

	protected void createViewsForAllModifiers(Context context,
			ViewGroup containerForAllItems, boolean skipFirst) {
		for (int i = 0; i < this.size(); i++) {
			if (i > 0 || !skipFirst) {
				ModifierInterface m = get(i);
				if (m != null) {
					View v = m.getView(context);
					if (v != null) {
						containerForAllItems.addView(v);
					}
				}
			}
		}
	}
}
