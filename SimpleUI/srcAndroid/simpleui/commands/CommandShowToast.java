package simpleui.commands;

import simpleui.util.Command;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class CommandShowToast implements Command {

	private final String mTextToDisplay;
	private final Context mContext;

	public CommandShowToast(Context context, String textToDisplay) {
		mTextToDisplay = textToDisplay;
		mContext = context;
	}

	@Override
	public boolean execute() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, mTextToDisplay, Toast.LENGTH_LONG)
						.show();
			}
		});
		return true;
	}

	public static void show(Activity a, String textToShow) {
		new CommandShowToast(a, textToShow).execute();
	}
}
