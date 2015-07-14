package simpleui.modifiers.v3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import simpleui.util.ActivityLifecycleListener;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.AccountPicker;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class M_SocialLogin extends M_Container implements
		ActivityLifecycleListener {

	private static final String LOG_TAG = M_SocialLogin.class.getSimpleName();
	private static final int CODE_AUTH_SCREEN = 2425;
	private static final int CODE_ACCOUNT_PICKER = 2135;
	private static final int WAIT_TIME_IN_SEC_BEFORE_ABORT = 3;

	public static class AccountType {

		/**
		 * https://developers.google.com/+/api/oauth
		 */
		public final static AccountType google = new AccountType("Google+",
				"com.google", "oauth2:profile");

		public final static AccountType facebook = new AccountType("Facebook",
				"com.facebook.auth.login", "com.facebook.auth.login");

		public final static AccountType twitter = new AccountType("Twitter",
				"com.twitter.android.auth.login",
				"com.twitter.android.oauth.token");

		public final static AccountType twitterSecret = new AccountType(
				"TwitterSecret", "com.twitter.android.auth.login",
				"com.twitter.android.oauth.token.secret");

		public final static AccountType linkedIn = new AccountType("LinkedIn",
				"com.linkedin.android", "com.linkedin.android");

		public final String accountTypeId;
		public final String accountTypeName;
		public final String authTokenType;
		/**
		 * The id/name/email picked by the user, content depends on provider
		 */
		public String userId;
		private String pickedAccountType;

		public AccountType(String name, String accountType, String authTokenType) {
			this.accountTypeName = name;
			this.accountTypeId = accountType;
			this.authTokenType = authTokenType;
		}

		public void setAccount(String pickedAccountName,
				String pickedAccountType) {
			this.userId = pickedAccountName;
			this.pickedAccountType = pickedAccountType;
		}

	}

	private final Handler mainThread = new Handler(Looper.getMainLooper());
	private final ExecutorService networkThread = Executors
			.newFixedThreadPool(1);
	private AccountType helperField;
	private Activity a;

	/**
	 * If you use this constructor you will have to fill the
	 * {@link M_SocialLogin} container manually and call
	 * {@link M_SocialLogin#loginWith(AccountType)}
	 */
	public M_SocialLogin() {
	}

	/**
	 * @param accountTypes
	 *            you can pass multiple accounts here to let the user choose
	 *            with which provider he wants to authenticate e.g.
	 *            {@link AccountType#google}, {@link AccountType#facebook}
	 */
	public M_SocialLogin(AccountType... accountTypes) {
		add(new M_Toolbar("Login"));
		for (final AccountType accountType : accountTypes) {
			add(new M_Button(accountType.accountTypeName) {

				@Override
				public void onClick(Context arg0, Button arg1) {
					loginWith(accountType);
				}
			});
		}
	}

	@Override
	public View getView(Context c) {
		this.a = (Activity) c;
		return super.getView(c);
	}

	public void loginWith(AccountType accType) {
		Account[] acc = AccountManager.get(a).getAccountsByType(
				accType.accountTypeId);
		if (acc.length == 1) {
			Account ac = acc[0];
			accType.setAccount(ac.name, ac.type);
			getAuthTokenFor(a, accType);
		} else {
			openAccountPicker(a, accType);
		}

	}

	private void openAccountPicker(Activity a, AccountType accType) {
		this.helperField = accType;
		a.startActivityForResult(AccountPicker.newChooseAccountIntent(null,
				null, new String[] { accType.accountTypeId }, true, null, null,
				null, null), CODE_ACCOUNT_PICKER);
	}

	private void getAuthTokenFor(final Activity a, final AccountType type) {
		networkThread.execute(new Runnable() {
			@Override
			public void run() {
				AccountManager accountManager = AccountManager.get(a);
				Account account = getAccountFor(accountManager,
						type.pickedAccountType, type.userId);
				getAuthResult(a, type, accountManager.getAuthToken(account,
						type.authTokenType, new Bundle(), false, null, null));
			}
		});
	}

	private void getAuthResult(final Activity a, final AccountType type,
			AccountManagerFuture<Bundle> future) {
		Log.i(LOG_TAG, "processFuture called: " + future);
		String authToken = null;
		try {
			Bundle bundle = future.getResult(WAIT_TIME_IN_SEC_BEFORE_ABORT,
					TimeUnit.SECONDS);
			try {
				authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (authToken == null) {
				Log.d(LOG_TAG, "User not authenticated");
				showAuthScreenToUser(a, type, bundle);
			} else {
				final String token = authToken;
				mainThread.post(new Runnable() {

					@Override
					public void run() {
						if (onAuthTokenReceived(token, type, a)) {
							a.finish();
						}
					}
				});
			}
		} catch (Exception e) {
			onAuthProviderDidNotReact(a, type, e);
		}
	}

	public abstract boolean onAuthTokenReceived(String authToken,
			AccountType pickedAccountType, Activity activity);

	private void showAuthScreenToUser(Activity a, AccountType accType,
			Bundle bundle) {
		this.helperField = accType;
		Intent intent = (Intent) bundle
				.getParcelable(AccountManager.KEY_INTENT);
		intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
		a.startActivityForResult(intent, CODE_AUTH_SCREEN);
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode,
			int resultCode, Intent data) {
		AccountType accountType = helperField;
		if (requestCode == CODE_ACCOUNT_PICKER) {
			if (resultCode == Activity.RESULT_OK) {
				String pickedAccountType = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
				String pickedAccountName = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				accountType.setAccount(pickedAccountName, pickedAccountType);
				getAuthTokenFor(activity, accountType);
			} else {
				onUserCancelledAccountPicking();
			}
		} else if (requestCode == CODE_AUTH_SCREEN) {
			if (resultCode == Activity.RESULT_OK) {
				getAuthTokenFor(activity, accountType);
			} else {
				onUserDidNotGrandAccess();
			}
		}
	}

	protected void onUserDidNotGrandAccess() {
		Log.w(LOG_TAG, "onUserDidNotGrandAccess()");
	}

	protected void onUserCancelledAccountPicking() {
		Log.w(LOG_TAG, "onUserCancelledAccountPicking()");
	}

	public abstract void onAuthProviderDidNotReact(Activity a,
			AccountType pickedAccountType, Exception e);

	private Account getAccountFor(AccountManager accountManager,
			String pickedAccountType, String pickedAccountName) {
		Account[] accs = accountManager.getAccounts();
		for (final Account acc : accs) {
			if (acc.type.equals(pickedAccountType)
					&& acc.name.equals(pickedAccountName)) {
				return acc;
			}
		}
		return null;
	}

	@Override
	public boolean onCloseWindowRequest(Activity arg0) {
		return true;
	}

	@Override
	public void onPause(Activity arg0) {
	}

	@Override
	public void onResume(Activity arg0) {
	}

	@Override
	public void onStop(Activity arg0) {
	}
}
