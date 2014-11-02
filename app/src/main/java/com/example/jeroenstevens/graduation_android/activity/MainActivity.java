package com.example.jeroenstevens.graduation_android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.activity.CollectionActivity;
import com.example.jeroenstevens.graduation_android.authentication.AccountHelper;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String AUTHORITY = "ourContentProviderAuthorities";

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;

    public static final long SECONDS_PER_HOUR = 3600L;
    public static final long SYNC_INTERVAL_IN_HOURS = 6L;

    public static final long SYNC_SMALL_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    public static final long SYNC_LARGE_INTERVAL = SECONDS_PER_HOUR * SYNC_INTERVAL_IN_HOURS;

    private String authToken;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccountManager = AccountManager.get(this);

        findViewById(R.id.hit_me_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenForAccountCreateIfNeeded(AccountHelper.ACCOUNT_TYPE, AccountHelper.AUTHTOKEN_TYPE_STANDARD_ACCESS);
            }
        });
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null, new AccountManagerCallback<Bundle>() {

            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                Bundle bnd = null;
                try {
                    bnd = future.getResult();
                    authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    if (authToken != null) {
                        String accountName = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);

                        Account connectedAccount = new Account(accountName, AccountHelper.ACCOUNT_TYPE);
                        AccountHelper.setCurrentAccount(connectedAccount);
                        AccountHelper.setCurrentAuthtoken(authToken);

                        ContentResolver.addPeriodicSync(connectedAccount, AUTHORITY, Bundle.EMPTY, 10);

                        Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                        MainActivity.this.startActivity(intent);
                    }
                    Log.d(TAG, "authToken : " + ((authToken != null) ? "SUCCESS!\ntoken: " + authToken : "FAIL"));
                    Log.d(TAG, "GetTokenForAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }
                , null);
    }

    private void showMessage(final String msg) {
        // Returns true if msg is null or of 0-length
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
