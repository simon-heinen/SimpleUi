package simpleui.modifiers.v3;

import java.util.ArrayList;
import java.util.HashMap;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * A horizontal bar for items (e.g. icons or buttons). Use the
 * {@link M_ItemBar#add(float, ModifierInterface)} method to set custom weights
 * to each item
 * 
 * @author Spobo
 * 
 */
public class M_ItemBar extends ArrayList<ModifierInterface> implements
		ModifierInterface {

	HashMap<ModifierInterface, Float> weightMap;

	@Override
	public View getView(Context context) {
		HorizontalScrollView scrollContainer = new HorizontalScrollView(context);
		LinearLayout containerForAllItems = new LinearLayout(context);
		scrollContainer.addView(containerForAllItems);
		LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
		scrollContainer.setLayoutParams(params);
		scrollContainer.setFillViewport(true);
		containerForAllItems.setGravity(Gravity.CENTER);

		for (ModifierInterface m : this) {
			View view = m.getView(context);
			if (weightMap != null) {
				Float weight = getWeightMap().get(m);
				if (weight != null) {
					view.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, weight));
				} else {
					view.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, 1));
				}
			}
			containerForAllItems.addView(view);
		}

		return scrollContainer;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (ModifierInterface m : this) {
			result &= m.save();
		}
		return result;
	}

	public HashMap<ModifierInterface, Float> getWeightMap() {
		if (weightMap == null) {
			weightMap = new HashMap<ModifierInterface, Float>();
		}
		return weightMap;
	}

	public void add(float weight, ModifierInterface item) {
		getWeightMap().put(item, weight);
		add(item);
	}

}
