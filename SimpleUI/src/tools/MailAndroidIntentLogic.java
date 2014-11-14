package tools;

import java.io.File;
import java.util.List;

import android.content.Context;

public class MailAndroidIntentLogic {

	private static final String LOG_TAG = "MailAndroidIntentLogic";

	/**
	 * use
	 * {@link IntentHelper#sendMailIntent(Context, String, String, String[], List)}
	 * instead
	 * 
	 * @param context
	 * @param myMailSubject
	 * @param mailText
	 * @param emailAddresses
	 * @param filesToSend
	 *            you can only send files which are located in the storage
	 *            (internal or external SD card e.g.)
	 */
	@Deprecated
	public static void sendMail(Context context, String myMailSubject,
			String mailText, String[] emailAddresses, List<File> filesToSend) {
		IntentHelper.sendMailIntent(context, myMailSubject, mailText,
				emailAddresses, filesToSend);
	}

}
