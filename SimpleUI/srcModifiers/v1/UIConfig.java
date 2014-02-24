package v1;

import v2.simpleUi.ModifierInterface;
import android.app.Activity;

public interface UIConfig {

	public Theme loadTheme();

	public ModifierInterface loadCloseButtonsFor(
			final Activity currentActivity, final ModifierGroup group);
}
