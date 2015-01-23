package simpleui.examples;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import simpleui.SimpleUI;
import simpleui.examples.bleTests.BleTest;
import simpleui.examples.modifiers.M_MakePhotoTests;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Container;
import simpleui.util.IO;
import simpleui.util.IO.FileFromUriListener;
import simpleui.util.Log;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Button;

public class M_CurrentNewFeatureTests extends M_Container {

	private static final String LOG_TAG = M_CurrentNewFeatureTests.class
			.getSimpleName();

	public M_CurrentNewFeatureTests() {
		add(new M_Button("Download file test") {

			@Override
			public void onClick(Context c, Button b) {
				ExecutorService network = Executors.newFixedThreadPool(1);
				network.execute(new Runnable() {

					@Override
					public void run() {
						Uri source = Uri
								.parse("https://bitstars-armro.appspot.com/v1/file/serve?blob-key=AMIfv95d18HIVQm-UNieXP1aTR08q6EtkQrGpG4UM7C9MjYzodXvPqUTRCuHNtBs7MqhScZ7WlavSE_jSkFJXzqJvOW--0WYrDCK30-Z36Hm8Yu8oSFG1c-8z_odq62Fh4t7WksHsZ453uDDq6-oRd_WHau-1zCZEJ5XF4dMljT-UjG62HadNyI");
						File target = new File(Environment
								.getExternalStorageDirectory(),
								"armroDownloads");
						IO.loadFileFromUri(source, target,
								"fallbackName.armro",
								new FileFromUriListener() {

									@Override
									public void onProgress(int percent) {
										Log.i(LOG_TAG, "percent=" + percent);
									}

									@Override
									public void onError(Exception e) {
										e.printStackTrace();
									}

									@Override
									public boolean cancelDownload() {
										return false;
									}
								});
					}
				});

			}
		});
		add(new M_Button("BLE Command trigger tests (shown in logcat)") {

			@Override
			public void onClick(Context c, Button b) {
				BleTest.startCommandTriggerSystem();
			}
		});
		add(new M_Button("Photo selection test") {

			M_MakePhotoTests photoTests = new M_MakePhotoTests();

			@Override
			public void onClick(Context arg0, Button arg1) {
				SimpleUI.showCancelOkDialog(arg0, "Cancel", "Ok", photoTests);
			}
		});
	}
}
