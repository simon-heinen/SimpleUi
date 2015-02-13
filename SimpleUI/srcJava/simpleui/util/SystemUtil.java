package simpleui.util;

import java.util.Enumeration;
import java.util.Properties;

public class SystemUtil {

	private static final String LOG_TAG = SystemUtil.class.getSimpleName();
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
					return false;
				}
			}
			return isGlass;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("isAndroid=" + isAndroid());
	}

	public static boolean isAndroid() {
		if (isAndroid == null) {
			try {
				isAndroid = System.getProperties().get("java.vendor")
						.toString().toLowerCase().contains("android");
				if (isAndroid) {
					return isAndroid;
				}
			} catch (Exception e) {
			}
			try {
				isAndroid = System.getProperties().get("java.vm.vendor")
						.toString().toLowerCase().contains("android");
				if (isAndroid) {
					return isAndroid;
				}
			} catch (Exception e1) {
				try {
					isAndroid = System.getProperties().get("java.vm.name")
							.toString().toLowerCase().equals("dalvik");
				} catch (Exception e2) {
					try {
						Properties p = System.getProperties();
						Enumeration keys = p.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							String value = (String) p.get(key);
							System.out.println("  > " + key + "=" + value);
							if (value.toLowerCase().contains("android")) {
								isAndroid = true;
								return isAndroid;
							}
						}
					} catch (Exception e3) {
						Log.e("Could not detect if running on Android!");
						isAndroid = false;
					}
				}
			}
		}
		return isAndroid;
	}

	public static boolean isDesktop() {
		// TODO maybe better isPureJava? or isJavaEEServer?
		return !isAndroid();
	}

}
