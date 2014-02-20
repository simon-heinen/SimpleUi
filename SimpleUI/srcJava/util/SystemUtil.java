/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * 
 * @author Spobo
 */
public class SystemUtil {

	public static boolean isAndroid() {
		return System.getProperties().get("java.vm.name").equals("Dalvik");
	}

}
