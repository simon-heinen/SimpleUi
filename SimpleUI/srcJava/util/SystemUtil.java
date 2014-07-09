package util;

public class SystemUtil {

	private static Boolean isAndroid;

	public static boolean isAndroid() {
		if (isAndroid == null) {
			isAndroid = System.getProperties().get("java.vm.name")
					.equals("Dalvik");
		}
		return isAndroid;
	}

}
