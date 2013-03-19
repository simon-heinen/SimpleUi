package v1;


import java.util.ArrayList;

import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ModifierGroup extends ArrayList<ModifierInterface> implements
		HasTheme, ModifierInterface {

	public interface SaveListener {
		public void onSaveSuccessfull();

		public void onSaveFailed(ModifierInterface modifierThatRejectedSave);
	}

	private SaveListener mySaveListener;
	private Theme myTheme;

	public ModifierGroup() {
	}

	public ModifierGroup(Theme myTheme) {
		setTheme(myTheme);
	}

	public void addModifier(ModifierInterface groupElement) {
		if (getTheme() != null && groupElement instanceof HasTheme) {
			if (((HasTheme) groupElement).getTheme() == null) {
				((HasTheme) groupElement).setTheme(getTheme());
			}
		}
		this.add(groupElement);
	}

	@Override
	public View getView(Context context) {

		LinearLayout linLayout = new LinearLayout(context);

		linLayout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < this.size(); i++) {
			linLayout.addView(this.get(i).getView(context));
		}

		ScrollView sv = new ScrollView(context);
		sv.addView(linLayout);
		sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		if (getTheme() != null) {
			getTheme().applyOuter1(sv);
		}

		return sv;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (int i = 0; i < this.size(); i++) {
			boolean saveSuccessfull = this.get(i).save();
			result &= saveSuccessfull;
			if (!saveSuccessfull && mySaveListener != null)
				mySaveListener.onSaveFailed(this.get(i));
		}
		if (result && mySaveListener != null)
			mySaveListener.onSaveSuccessfull();
		return result;
	}

	public void setSaveListener(SaveListener saveListener) {
		mySaveListener = saveListener;
	}

	@Override
	public void setTheme(Theme theme) {
		myTheme = theme;
	}

	@Override
	public Theme getTheme() {
		return myTheme;
	}

}