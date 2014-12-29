package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.googlecode.simpleui.library.R;

public abstract class M_Slider implements ModifierInterface,
		OnSeekBarChangeListener {

	private static final String LOG_TAG = M_Slider.class.getSimpleName();
	private int mProgress;
	private final int maxValue;
	private UiCreateListener<SeekBar> uiListener;

	public M_Slider(int maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public View getView(Context context) {
		SeekBar s = (SeekBar) View.inflate(context,
				R.layout.material_factory_seekbar, null);
		s.setMax(maxValue);
		this.mProgress = loadCurrentValue();
		s.setProgress(mProgress);
		s.setOnSeekBarChangeListener(this);
		if (uiListener != null) {
			uiListener.onUiCreated(s);
		}
		return s;
	}

	public void setUiListener(UiCreateListener<SeekBar> uiListener) {
		this.uiListener = uiListener;
	}

	@Override
	public boolean save() {
		return save(mProgress);
	}

	public abstract int loadCurrentValue();

	public abstract boolean save(int progress);

	@Override
	public final void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.mProgress = progress;
		if (fromUser) {
			onProgressUpdate(mProgress, true);
		}
	}

	/**
	 * Override to get constant input updates
	 * 
	 * @param progressValue
	 * @param userHasFingerStillDown
	 *            if false the user released the finger from the touchscreen, so
	 *            this will be only the case once the user picked the final
	 *            value
	 */
	public void onProgressUpdate(int progressValue,
			boolean userHasFingerStillDown) {
		if (!userHasFingerStillDown) {
			Log.i(LOG_TAG, "Progress set to " + progressValue);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		onProgressUpdate(mProgress, false);
	}

}
