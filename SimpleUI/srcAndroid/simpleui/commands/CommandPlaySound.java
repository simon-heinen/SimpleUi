package simpleui.commands;

import simpleui.util.Command;
import android.content.Context;
import android.media.MediaPlayer;

public class CommandPlaySound implements Command {

	private int mySoundId;
	private Context myContext;
	private String mySoundPath;

	/**
	 * To play a sound located in teh ressources folder
	 * 
	 * @param context
	 * @param soundId
	 *            id from the ressources
	 */
	public CommandPlaySound(Context context, int soundId) {
		myContext = context;
		mySoundId = soundId;
	}

	/**
	 * @param soundPath
	 *            e.g. "/sdcard/test.mp3"
	 */
	public CommandPlaySound(String soundPath) {
		mySoundPath = soundPath;
	}

	@Override
	public boolean execute() {
		if (myContext != null && mySoundId != 0) {
			MediaPlayer.create(myContext, mySoundId).start();
			return true;
		}
		if (mySoundPath != null) {
			try {
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(mySoundPath);
				mp.prepare();
				mp.start();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
