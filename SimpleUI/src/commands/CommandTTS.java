package commands;

import java.util.Locale;

import util.Command;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class CommandTTS implements Command, OnInitListener {
	private static final String LOG_TAG = "CommandTTS";
	private TextToSpeech tts;
	private String text;
	private int mode = TextToSpeech.QUEUE_FLUSH;
	private boolean alwaysSpeak;

	public CommandTTS(Context context) {
		tts = new TextToSpeech(context, this);
	}

	@Override
	public boolean execute() {
		if (tts != null && (!tts.isSpeaking() || alwaysSpeak)) {
			tts.speak(text, mode, null);
			return true;
		}
		return false;
	}

	/**
	 * @param alwaysSpeak
	 *            if true the tts engine will execute the speak command no
	 *            matter if it is still speaking or not. should be used together
	 *            with {@link CommandTTS#setMode(int)} set to
	 *            {@link TextToSpeech#QUEUE_ADD}
	 */
	public void setAlwaysSpeak(boolean alwaysSpeak) {
		this.alwaysSpeak = alwaysSpeak;
	}

	/**
	 * @param mode
	 *            can be {@link TextToSpeech#QUEUE_FLUSH} or
	 *            {@link TextToSpeech#QUEUE_ADD}. When
	 *            {@link TextToSpeech#QUEUE_ADD} the
	 *            {@link CommandTTS#setAlwaysSpeak(boolean)} should also be set
	 *            to true
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	public void stop() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
	}

	public boolean execute(String text) {
		setText(text);
		return execute();
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());
		} else {
			Log.w(LOG_TAG, "Failed to initialize TTS engine");
			tts = null;
		}
	}

}
