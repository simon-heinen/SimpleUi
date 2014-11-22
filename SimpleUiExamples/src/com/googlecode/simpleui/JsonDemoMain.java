package com.googlecode.simpleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import v1.ModifierGroup;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class JsonDemoMain extends Activity {
	private static final String LOG_TAG = "DemoMain";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			JSONObject jsonObj = new JSONObject(convertInputStreamToString(this
					.getAssets().open("vorlage")));
			ModifierGroup group = new ModifierGroup();
			new JsonEditorUI(jsonObj, this).customizeScreen(group, null);
			setContentView(group.getView(this));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SimpleUI.showEditScreen(this, new JsonEditorUI(jsonObj),
		// null);

	}

	public static String convertInputStreamToString(InputStream stream) {
		if (stream == null)
			return null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			stream.close();
			return sb.toString();

		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not convert input stream to string");
		}
		return null;
	}
}