package v1;

public interface HasTheme {

	/**
	 * This will set a theme and all newly added children will get this theme
	 * too. So first set a theme, then add the children!
	 * 
	 * @param theme
	 */
	public abstract void setTheme(Theme theme);

	public abstract Theme getTheme();

}