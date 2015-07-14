package simpleui.examples;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import simpleui.SimpleUI;
import simpleui.examples.bleTests.BleTest;
import simpleui.examples.modifiers.M_MakePhotoTests;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_SocialLogin;
import simpleui.modifiers.v3.M_SocialLogin.AccountType;
import simpleui.util.IOHelper;
import simpleui.util.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

public class M_CurrentNewFeatureTests extends M_Container {

	private static final String LOG_TAG = M_CurrentNewFeatureTests.class
			.getSimpleName();

	public M_CurrentNewFeatureTests() {

		add(new M_Button("Social Login Example") {

			@Override
			public void onClick(final Context c, Button arg1) {
				M_SocialLogin login = new M_SocialLogin(AccountType.google,
						AccountType.facebook, AccountType.twitter,
						AccountType.linkedIn) {
					@Override
					public boolean onAuthTokenReceived(String authToken,
							AccountType pickedAccountType, Activity activity) {
						String text = "The user " + pickedAccountType.userId
								+ " chose " + pickedAccountType.accountTypeName
								+ " and his authToken is " + authToken;
						Toast.makeText(c, text, Toast.LENGTH_LONG).show();
						return true;
					}

					@Override
					public void onAuthProviderDidNotReact(Activity a,
							AccountType pickedAccountType, Exception e) {
						Log.w(LOG_TAG, "onAuthProviderDidNotReact: "
								+ e.getClass().getSimpleName());
						e.printStackTrace();
					}

				};
				SimpleUI.showUi(c, login);
			}
		});

		add(new M_Button("Download file test") {

			@Override
			public void onClick(Context c, Button b) {
				ExecutorService network = Executors.newFixedThreadPool(1);
				network.execute(new Runnable() {

					@Override
					public void run() {
						try {
							URL source = new URL(
									"https://fbcdn-dragon-a.akamaihd.net/hphotos-ak-xap1/t39.2365-6/851565_602269956474188_918638970_n.png");
							File target = new File(Environment
									.getExternalStorageDirectory(),
									"armroDownloads");
							IOHelper.loadFileFromUri(source, target,
									"fallbackName.armro",
									new IOHelper.FileFromUriListener() {

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

										@Override
										public File onGetCachedVersion(
												String arg0, long arg1,
												Integer arg2) {
											// TODO Auto-generated method stub
											return null;
										}

										@Override
										public void onStop(File a) {
										}
									});
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				});

			}
		});
		add(new M_Button("BLE Command trigger tests (shown in logcat)") {

			@Override
			public void onClick(Context c, Button b) {
				BleTest.startTriggerTest();
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
