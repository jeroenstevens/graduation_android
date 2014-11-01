package com.example.jeroenstevens.graduation_android.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.object.ApiKey;
import com.example.jeroenstevens.graduation_android.rest.RestClient;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserRegisterRequestBody;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.example.jeroenstevens.graduation_android.authentication.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static com.example.jeroenstevens.graduation_android.authentication.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static com.example.jeroenstevens.graduation_android.authentication.AuthenticatorActivity.PARAM_USER_PASS;

/**
 * In charge of the Sign up process. Since it's not an AuthenticatorActivity decendent,
 * it returns the result back to the calling authentication, which is an AuthenticatorActivity,
 * and it return the result back to the Authenticator
 */
public class SignUpActivity extends Activity {

    private String TAG = getClass().getSimpleName();
    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        ((EditText) findViewById(R.id.accountName)).setText(getPossibleEmail());

        ((EditText) findViewById(R.id.accountPassword)).requestFocus();
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

    private void createAccount() {
        Log.d(TAG, "createAccount");

        // Validation!
        new AsyncTask<String, Void, Void>() {

            String email = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
            String password = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

            @Override
            protected Void doInBackground(String... params) {

                Log.d(TAG, "Started authenticating");

                String authtoken = null;
                final Bundle data = new Bundle();
                try {
                    RestClient.get().registerUser(new UserRegisterRequestBody(email, password), new Callback<ApiKey>() {
                        @Override
                        public void success(ApiKey apiKey, Response response) {
                            data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                            data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                            data.putString(AccountManager.KEY_AUTHTOKEN, apiKey.getAccessToken());
                            data.putString(PARAM_USER_PASS, password);

                            final Intent intent = new Intent();
                            intent.putExtras(data);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) { }

        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}
