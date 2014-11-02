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
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.example.jeroenstevens.graduation_android.authentication.AccountHelper;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.object.User;
import com.example.jeroenstevens.graduation_android.rest.RestClient;
import com.example.jeroenstevens.graduation_android.rest.requestBody.CollectionPostRequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    private final AccountManager mAccountManager;
    private final Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "onPerformSync");

        // Get the auth token for the current account.
        final User user = User.getCurrentUser(AccountHelper.getCurrentAuthtoken());

        Log.d(TAG, "Get remote collections");
        RestClient.get().getCollections(user.id, new Callback<List<Collection>>() {

            @Override
            public void success(List<Collection> collections, Response response) {
                String dateAgo = new Date((System.currentTimeMillis() - (1000 * 60 * 10))).toString();

                // Remote
                List<Collection> updatedRemoteObjects = collections;

                // Local
                List<Collection> allLocaObjects = Collection.all();
                List<UUID> allLocaObjectsId = new ArrayList<UUID>();

                // Local -> Remote
                List<Collection> createdLocalObjects = Collection.whereDateAgo(
                        Collection.COL_CREATED_AT, dateAgo);
                List<Collection> updatedLocalObjects = Collection.whereDateAgo(
                        Collection.COL_UPDATED_AT, dateAgo);

                // Remote -> Local
                List<Collection> remoteObjectsToBeCreatedLocally = new ArrayList<Collection>();
                List<Collection> remoteObjectsToBeUpdatedLocally = new ArrayList<Collection>();

                Log.d(TAG, "createdLocalObjects :" + createdLocalObjects);
                Log.d(TAG, "updatedLocalObjects :" + updatedLocalObjects);

                // Get an array of the ids of all local objects
                for (Collection localObject : allLocaObjects) {
                    allLocaObjectsId.add(localObject.id);
                }

                // Determine if remote object needs to be created or updated locally.
                for (Collection remoteObject : updatedRemoteObjects) {
                    // If object with id already exists add to ToBeUpdated
                    // Else add to ToBeCreated
                    if (allLocaObjectsId.contains(remoteObject.id)) {
                        remoteObjectsToBeUpdatedLocally.add(remoteObject);
                    } else {
                        remoteObjectsToBeCreatedLocally.add(remoteObject);
                    }
                }

                Log.d(TAG, "Determine if remote object needs to be created or updated locally");
                Log.d(TAG, "remoteObjectsToBeCreatedLocally :" + remoteObjectsToBeCreatedLocally);
                Log.d(TAG, "remoteObjectsToBeUpdatedLocally :" + remoteObjectsToBeUpdatedLocally);

                // Remove object from updatedLocalObjects if CREATED_AT == UPDATED_AT
                // Otherwise it will do an unnecessary additional request.
                for (Collection localobject : updatedLocalObjects) {
                    if (localobject.updatedAt.equals(localobject.createdAt)) {
                        updatedLocalObjects.remove(localobject);
                    }
                }

                Log.d(TAG, "Double dates should be removed");
                Log.d(TAG, "createdLocalObjects :" + createdLocalObjects);
                Log.d(TAG, "updatedLocalObjects :" + updatedLocalObjects);

                if (remoteObjectsToBeCreatedLocally.size() == 0) {
                    Log.d(TAG, "No local objects to be created");
                } else {
                    Log.d(TAG, "Creating local objects from new remote objects");

                    ActiveAndroid.beginTransaction();
                    try {
                        // Create for every remote object
                        // that doesn't exist locally, a new local object
                        for (Collection remoteObject : remoteObjectsToBeCreatedLocally) {
                            Log.d(TAG, "Remote --create--> Local : " + remoteObject.name);
//                            Collection collection = new Collection();
//                            collection.id = remoteObject.id;
//                            collection.name = remoteObject.name;
//                            collection.userId = remoteObject.userId;
//                            collection.save();
                            remoteObject.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }
                    databaseUpdated();
                }

                if (remoteObjectsToBeUpdatedLocally.size() == 0) {
                    Log.d(TAG, "No local objects to be updated");
                } else {
                    Log.d(TAG, "Updating local objects from remote objects");

                    ActiveAndroid.beginTransaction();
                    try {
                        for (Collection remoteObject : remoteObjectsToBeUpdatedLocally) {
                            Log.d(TAG, "Remote --update--> Local : " + remoteObject.name);

                            Collection localObject = Collection.get(remoteObject.id);
                            localObject.updateFromRemote(remoteObject);
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }
                    databaseUpdated();
                }

                if (createdLocalObjects.size() == 0) {
                    Log.d(TAG, "No new locally created objects");
                } else {
                    Log.d(TAG, "Updating remote objects from local objects");

                    // Create for every local object
                    // that doesn't exist remotely, a new remote object
                    for (Collection localObject : createdLocalObjects) {
                        Log.d(TAG, "Local --create--> Remote [" + localObject.name);

                        CollectionPostRequestBody requestBody = new CollectionPostRequestBody(localObject);
                        RestClient.get().postCollection(requestBody, new Callback<Collection>() {
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

                if (updatedLocalObjects.size() == 0) {
                    Log.d(TAG, "No remote objects to be updated");
                } else {
                    for (Collection localObject : createdLocalObjects) {
                        Log.d(TAG, "Local --update--> Remote [" + localObject.name);

                        CollectionPostRequestBody requestBody = new CollectionPostRequestBody(localObject);
                        RestClient.get().postCollection(requestBody, new Callback<Collection>() {
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

                Log.d(TAG, "Finished syncing.");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void databaseUpdated() {
        getContext().sendBroadcast(new Intent(SyncService.DATABASE_UPDATED));
    }
}

