package module.meta;

/**
 * test your regex with: https://www.debuggex.com/
 * 
 * find common regex expr. here: http://regexlib.com/
 */
public class RegexUtils {

	/**
	 * TODO implement
	 * 
	 * “?between ”+”x y”
	 */
	@Deprecated
	public static final String betweenXY = "?between ";
	/**
	 * TODO implement
	 * 
	 * “?< “+ “x”
	 */
	@Deprecated
	public static final String smallerThanX = "?< ";
	/**
	 * TODO implement
	 * 
	 * “?>= “+”x”
	 */
	@Deprecated
	public static final String biggerThanX = "?>= ";

	public static final String EMAIL_ADDRESS = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String ZIP_CODE = "^\\d{5}$";

	// TODO :
	// public static final String CREDIT_CARD_NR_VISA = ;
	// public static final String CREDIT_CARD_NR_MASERCARD = ;
	// public static final String MAC_ADDR = ;
	// public static final String DOLLAR = ;

	public static final String USERNAME = "^[a-z0-9_-]{3,16}$";
	public static final String URL = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
	public static final String IP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	public static final String PHONE_NR = "^\\+?[\\d\\s]{3,}$";

	public static final String NUMBER_INTEGER = "^-{0,1}\\d+$";
	public static final String NUMBER_DECIMAL = "^-{0,1}\\d*\\.{0,1}\\d+$";
	public static final String IMAGE_FILE = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
	public static final String COLOR_HEX_RGB = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
	public static final String COLOR_HEX_RGBA = "^#([A-Fa-f0-9]{8}|[A-Fa-f0-9]{4})$";

	/**
	 * (dd mm yyyy, d/m/yyyy, etc.)
	 */
	public static final String DATEddmmyyyy = "^([1-9]|0[1-9]|[12][0-9]|3[01])\\D([1-9]|0[1-9]|1[012])\\D(19[0-9][0-9]|20[0-9][0-9])$";
	public static final String DATEmmddyyyy = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)";
	public static final String TIME_12h = "(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
	public static final String TIME_24h = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

	/**
	 * A list of special characters to be used in regex expressions
	 * 
	 * TODO is it necessary add \\ for ,-
	 */
	private static final String SPECIAL_CHARS = "@&'#%;:_,-\\.\\*\\+\\$\\^\\?\\{\\}\"\\[\\]\\(\\)";

	/**
	 * min. length=6 and 1 character and one number
	 * 
	 * http://en.wikipedia.org/wiki/Password_strength#Common_guidelines
	 * 
	 */
	public static final String PASSWORD_STRENGTH1 = "((?=.*\\d)(?=.*[a-z]).{6,99})";
	/**
	 * min. length=9 and 1 character, 1 upper character and one number
	 * 
	 * http://en.wikipedia.org/wiki/Password_strength#Common_guidelines
	 * 
	 */
	public static final String PASSWORD_STRENGTH2 = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{9,99})";

	/**
	 * min. length=12 and 1 character, 1 upper character, one special character
	 * and one number
	 * 
	 * http://en.wikipedia.org/wiki/Password_strength#Common_guidelines
	 * 
	 */
	public static final String PASSWORD_STRENGTH3 = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*["
			+ SPECIAL_CHARS + "]).{12,99})";

}
