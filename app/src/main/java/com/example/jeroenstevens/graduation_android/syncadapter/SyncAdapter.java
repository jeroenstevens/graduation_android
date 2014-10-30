/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.jeroenstevens.graduation_android.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.example.jeroenstevens.graduation_android.authentication.AccountGeneral;
import com.example.jeroenstevens.graduation_android.db.DbContentProvider;
import com.example.jeroenstevens.graduation_android.db.DbHelper;
import com.example.jeroenstevens.graduation_android.db.DbInterface;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.rest.RestClient;
import com.example.jeroenstevens.graduation_android.rest.requestBody.CollectionPostRequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * SwatchesSyncAdapter implementation for syncing sample SwatchesSyncAdapter contacts to the
 * platform ContactOperations provider.  This sample shows a basic 2-way
 * sync between the client and a sample server.  It also contains an
 * example of how to update the contacts' status messages, which
 * would be useful for a messaging or social networking client.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SwatchesSyncAdapter";

    private final AccountManager mAccountManager;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {

        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.d(TAG, "onPerformSync for account[" + account.name + "]. Extras: "+sb.toString());
        try {
            // Get the auth token for the current account.
            final String authToken = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.AUTHTOKEN_TYPE_STANDARD_ACCESS, true);

            Log.d(TAG, "Get remote collections");
            RestClient.get().getCollections(4, new Callback<List<Collection>>() {
                @Override
                public void success(List<Collection> collections, Response response) {
                    List<Collection> remoteObjects = collections;
                    List<Collection> localObjects = DbInterface.getCollections();

                    // See what Local objects are missing on Remote
                    ArrayList<Collection> toRemote = new ArrayList<Collection>();
                    for (Collection localObject : localObjects) {
                        if (!remoteObjects.contains(localObject))
                            toRemote.add(localObject);
                    }

                    // See what Remote objects are missing on Local
                    ArrayList<Collection> toLocal = new ArrayList<Collection>();
                    for (Collection remoteObject : remoteObjects) {
                        if (!localObjects.contains(remoteObject))
                            toLocal.add(remoteObject);
                    }

                    if (toRemote.size() == 0) {
                        Log.d(TAG, "No local changes to update server");
                    } else {
                        Log.d(TAG, "Updating remote server with local changes");

                        // Updating remote object
                        for (Collection localObject : toRemote) {
                            Log.d(TAG, "Local -> Remote [" + localObject.getName() + "]");
                            CollectionPostRequestBody requestBody = new CollectionPostRequestBody(localObject.getName(), 4);
                            RestClient.get().postCollection(4, requestBody, new Callback<Collection>() {
                                @Override
                                public void success(Collection collection, Response response) {
                                    Log.d(TAG, "success : " + response.toString());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.d(TAG, "failure : " + error.getBody().toString());
                                }
                            });
                        }
                    }

                    if (toLocal.size() == 0) {
                        Log.d(TAG, "No server changes to update local database");
                    } else {
                        Log.d(TAG, "Updating local database with remote changes");

                        // Updating local object
                        int i = 0;
                        ContentValues toLocalValues[] = new ContentValues[toLocal.size()];
                        for (Collection localObject : toLocal) {
                            Log.d(TAG, "Remote -> Local [" + localObject.getName() + "]");

                            ContentValues values = new ContentValues();
                            values.put(DbHelper.COLLECTION_COL_NAME, localObject.getName());

                            toLocalValues[i++] = values;
                        }
                        try {
                            provider.bulkInsert(DbContentProvider.getContentUri(DbHelper.COLLECTION_TABLE_NAME), toLocalValues);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "Finished syncing.");
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });


        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

