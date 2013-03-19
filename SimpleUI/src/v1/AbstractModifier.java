package v1;

import v2.simpleUi.ModifierInterface;

public abstract class AbstractModifier implements ModifierInterface, HasTheme {

	private Theme myTheme;

	/* (non-Javadoc)
	 * @see gui.simpleUI.v1.HasTheme#setTheme(gui.simpleUI.v1.Theme)
	 */
	@Override
	public void setTheme(Theme myTheme) {
		this.myTheme = myTheme;
	}

	/* (non-Javadoc)
	 * @see gui.simpleUI.v1.HasTheme#getTheme()
	 */
	@Override
	public Theme getTheme() {
		return myTheme;
	}

}