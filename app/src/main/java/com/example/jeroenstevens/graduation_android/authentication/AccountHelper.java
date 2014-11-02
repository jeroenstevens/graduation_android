package com.example.jeroenstevens.graduation_android.authentication;

import android.accounts.Account;

public class AccountHelper {

//  Account type id
    public static final String ACCOUNT_TYPE = "com.example.jeroenstevens.graduation_android";

//  Account name
    public static final String ACCOUNT_NAME = "Scavenger";

//  Auth token types
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access";

    public static final String AUTHTOKEN_TYPE_STANDARD_ACCESS = "Standard access";
    public static final String AUTHTOKEN_TYPE_STANDARD_ACCESS_LABEL = "Standard access";

    private static Account mCurrentAccount;
    private static String mCurrentAuthtoken;

    public static void setCurrentAccount(Account account) {
        mCurrentAccount = account;
    }

    public static Account getCurrentAccount() {
        return mCurrentAccount;
    }

    public static void setCurrentAuthtoken(String authtoken) {
        mCurrentAuthtoken = authtoken;
    }

    public static String getCurrentAuthtoken() {
        return mCurrentAuthtoken;
    }

}
