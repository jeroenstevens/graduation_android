package com.example.jeroenstevens.graduation_android.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.object.ApiKey;
import com.example.jeroenstevens.graduation_android.rest.RestClient;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserAuthenticateRequestBody;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The Authenticator activity.
 *
 * Called by the Authenticator and in charge of identifing the user.
 *
 * It sends back to the Authenticator the result.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_STANDARD_ACCESS;

        if (accountName != null) {
            ((TextView)findViewById(R.id.email)).setText(accountName);
        } else {
            ((TextView)findViewById(R.id.email)).setText(getPossibleEmail());
        }

        ((EditText) findViewById(R.id.password)).requestFocus();
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    private String getPossibleEmail() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {

        final String email = ((TextView) findViewById(R.id.email)).getText().toString();
        final String password = ((TextView) findViewById(R.id.password)).getText().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        final Bundle data = new Bundle();
        Log.d(TAG, "Started authenticating");
        RestClient.get().authenticateUser(new UserAuthenticateRequestBody(email, password), new Callback<ApiKey>() {

            @Override
            public void success(ApiKey apiKey, Response response) {
                data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, apiKey.getAccessToken());
                data.putString(PARAM_USER_PASS, password);

                final Intent intent  = new Intent();
                intent.putExtras(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishLogin(intent);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "authentication error : " + error.toString());
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void finishLogin(Intent intent) {
        Log.d(TAG, "finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d(TAG,"setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
