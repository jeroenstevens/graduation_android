package com.example.jeroenstevens.graduation_android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.authentication.AccountGeneral;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private String authToken;
    private Account mConnectedAccount;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccountManager = AccountManager.get(this);

        findViewById(R.id.hit_me_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_STANDARD_ACCESS);

//                RestClient.get().getCollections("1", new Callback<List<Collection>>() {
//                    @Override
//                    public void success(List<Collection> collections, Response response) {
//                        Log.d(TAG, "collections : " + collections);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.d(TAG, "error : " + error);
//                    }
//                });
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
                        mConnectedAccount = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);
//                        refreshSyncStatus();
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
