package commands;

import util.Command;
import android.os.Handler;
import android.os.Looper;

/**
 * This command is useful if you want to change something in the Android UI
 * system. It is only allowed modify UI elements from the UI thread and this
 * {@link Command} will handle the access to this thread for you.
 * 
 * @author Simon Heinen
 * 
 */
public abstract class CommandInUiThread implements Command {

	private final static Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public final boolean execute() {
		if (Thread.currentThread().equals(mHandler.getLooper().getThread())) {
			executeInUiThread();
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					executeInUiThread();
				}

			});
		}
		return true;
	}

	public abstract void executeInUiThread();

}
