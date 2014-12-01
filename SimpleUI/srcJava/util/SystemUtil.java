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
			try {
				String v = "" + System.getProperties().get("java.vendor");
				isAndroid = v.contains("Android");
				if (isAndroid) {
					return isAndroid;
				}
			} catch (Exception e) {
			}
			try {
				String v = "" + System.getProperties().get("java.vm.vendor");
				isAndroid = v.contains("Android");
				if (isAndroid) {
					return isAndroid;
				}
			} catch (Exception e1) {
			}
			try {
				isAndroid = System.getProperties().get("java.vm.name")
						.equals("Dalvik");
			} catch (Exception e2) {
				Log.e("Could not detect if running on Android!");
				isAndroid = false;
			}
		}
		return isAndroid;
	}

}
