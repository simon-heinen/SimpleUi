package simpleui.examples.account;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AccountManagerLogin extends Activity {
	private static final String LOG_TAG = "AccountManagerLogin";
	public AccountManager am;
	private final Handler mHandler;

	public AccountManagerLogin() {
		am = AccountManager.get(this);
		mHandler = new Handler();
	}

	@SuppressLint("NewApi")
	/**
	 * based on 
	 * http://stackoverflow.com/questions/4593061/how-to-retrieve-an-facebook-authtoken-from-the-accounts-saved-on-android/6392598#6392598
	 * broken, for testing only
	 * @param handler
	 */
	public void getFacebookAuthToken() {
		Account[] accounts = am.getAccountsByType("com.facebook.auth.login");
		if (accounts.length > 0) {
			for (int j = 0; j < accounts.length; j++) {
				Account account = accounts[j];
				if (account.type != null
						&& account.type.equals("com.facebook.auth.login")) {
					Log.d(LOG_TAG, "FACEBOOK-TYPE FOUND");
					final AccountManagerFuture<Bundle> amf = am.getAuthToken(
							account, "com.facebook.auth.login", null, true,
							new AccountManagerCallback<Bundle>() {
								@Override
								public void run(
										AccountManagerFuture<Bundle> arg0) {
									Log.d(LOG_TAG, "callback");
									try {
										Bundle b = arg0.getResult();
										Log.d(LOG_TAG,
												"THIS AUTHTOKEN: "
														+ b.getString(AccountManager.KEY_AUTHTOKEN));
									} catch (Exception e) {
										Log.d(LOG_TAG, "EXCEPTION@AUTHTOKEN");
									}
								}
							}, mHandler);

					if (amf != null) {
						Log.d(LOG_TAG, "amf not null");

						Thread t = new Thread() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();
								Log.d(LOG_TAG, "thread triggered");
								Bundle b = null;
								try {
									Log.d(LOG_TAG, "thread triggered2");
									b = amf.getResult();
									Log.d(LOG_TAG, "thread triggered3");
									Log.d(LOG_TAG,
											"THIS AUTHTOKEN: "
													+ b.getString(AccountManager.KEY_AUTHTOKEN));
								} catch (OperationCanceledException
										| AuthenticatorException | IOException e) {
									// TODO Auto-generated catch block
									Log.d(LOG_TAG, e.toString());
								}
								Log.d(LOG_TAG, "thread triggered4");
								Log.d(LOG_TAG,
										"THIS AUTHTOKEN: "
												+ b.getString(AccountManager.KEY_AUTHTOKEN));
							}
						};
						t.start();

					}
				}
			}
		}
	}
}
