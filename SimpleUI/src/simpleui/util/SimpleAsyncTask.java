package simpleui.util;

import java.util.concurrent.Executors;

import android.os.AsyncTask;

/**
 * The system will never execute 2 {@link SimpleAsyncTask}s at once!! Do not use
 * this class for loops (e.g. for continous animations) use {@link Executors}
 * there. It should only be used for single tasks which will do something and
 * end afterwards.
 * 
 */
public abstract class SimpleAsyncTask extends
		AsyncTask<Object, Integer, Object> {

	@Override
	protected Object doInBackground(Object... params) {
		onRun();
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		onTaskFinished();
	}

	public abstract void onRun();

	/**
	 * Runs in the ui thread
	 */
	public void onTaskFinished() {
	}

	public void execute() {
		Object[] array = null;
		this.execute(array);
	}

	public void run() {
		Object[] array = null;
		this.execute(array);
	}

}
