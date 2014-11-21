package util;

public class SystemUtil {

	private static Boolean isAndroid;
	private static Boolean isGlass;

	public static boolean isGlass() {
		if (isAndroid()) {
			if (isGlass == null) {
				try {
					Class<?> Build = Class.forName("android.os.Build");
					String Build_MANUFACTURER = (String) Build
							.getDeclaredField("MANUFACTURER").get(String.class);
					String Build_MODEL = (String) Build.getDeclaredField(
							"MODEL").get(String.class);
					isGlass = "Google".equalsIgnoreCase(Build_MANUFACTURER)
							&& Build_MODEL.startsWith("Glass");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return isGlass;
		}
		return false;
	}

	public static boolean isAndroid() {
		if (isAndroid == null) {
			isAndroid = System.getProperties().get("java.vm.name")
					.equals("Dalvik");
		}
		return isAndroid;
	}

}
