package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MailAndroidIntentLogic {

	private static final String LOG_TAG = "MailAndroidIntentLogic";

	/**
	 * @param context
	 * @param myMailSubject
	 * @param mailText
	 * @param emailAddresses
	 * @param filesToSend
	 *            you can only send files which are located in the storage
	 *            (internal or external SD card e.g.)
	 */
	public static void sendMail(Context context, String myMailSubject,
			String mailText, String[] emailAddresses, List<File> filesToSend) {

		ArrayList<Uri> uris = new ArrayList<Uri>();
		if (filesToSend != null && !filesToSend.isEmpty()) {
			// convert from paths to Android friendly Parcelable Uri's
			for (File fileIn : filesToSend) {
				Uri u = Uri.fromFile(fileIn);
				if (fileIn.exists() && fileIn.canRead()) {
					uris.add(u);
				} else {
					Log.w(LOG_TAG, "Can't attach file: " + fileIn);
					Log.d(LOG_TAG, "file.exists()=" + fileIn.exists());
					Log.d(LOG_TAG, "file.canRead()=" + fileIn.canRead());
				}
			}
		}
		// need to "send multiple" to get more than one attachment
		Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		if (uris.size() <= 1) {
			// if no (or one) files appended use the default intent type
			emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		}
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				myMailSubject);
		// emailIntent.setType("plain/text");
		emailIntent.setType("message/rfc822");
		emailIntent
				.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
		emailIntent.putExtra(Intent.EXTRA_TEXT, mailText);
		if (!uris.isEmpty()) {
			if (uris.size() == 1) {
				// if only one file attach only the one file, otherwise problems
				// in gmail e.g.
				emailIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
			} else if (uris.size() > 1) {
				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
			}
		}
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

}
