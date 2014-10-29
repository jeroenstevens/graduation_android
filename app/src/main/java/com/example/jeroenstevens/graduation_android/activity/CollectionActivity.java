package com.example.jeroenstevens.graduation_android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.adapter.ItemsAdapter;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.object.Item;
import com.example.jeroenstevens.graduation_android.rest.RestClient;
import com.example.jeroenstevens.graduation_android.rest.requestBody.CollectionPostRequestBody;
import com.example.jeroenstevens.graduation_android.rest.requestBody.ItemPostRequestBody;
import com.example.jeroenstevens.graduation_android.view.SlidingTabLayout;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CollectionActivity extends Activity {
    public static final String TAG = "CollectionActivity";

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private SwipeListView mSwipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);

        refreshCollection();

        ((ImageView) findViewById(R.id.add_collection)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText collectionName = new EditText(CollectionActivity.this);
                new AlertDialog.Builder(CollectionActivity.this)
                        .setTitle("Collection name")
                        .setView(collectionName)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String inputText = collectionName.getText().toString();

                                RestClient.get().postCollection("4", new CollectionPostRequestBody(inputText, 4), new Callback<Collection>() {
                                    @Override
                                    public void success(Collection collection, Response response) {
                                        Log.d(TAG, "postCollection response : " + response.toString());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshCollection();
                                            }
                                        });
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d(TAG, "postCollection error : " + error.toString());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getBaseContext(), "What kind of scavenger are you?!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

    }

    private void refreshCollection() {
        RestClient.get().getCollections("4", new Callback<List<Collection>>() {

            @Override
            public void success(final List<Collection> collections, Response response) {
                Log.d(TAG, "collections : " + collections);

                mViewPager = (ViewPager) findViewById(R.id.view_pager);
                mViewPager.setAdapter(new PagerAdapter() {
                    @Override
                    public int getCount() {
                        return collections.size();
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object o) {
                        return o == view;
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        return collections.get(position).getName();
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, final int position) {
                        View view = getLayoutInflater().inflate(R.layout.pager_item, container, false);
                        container.addView(view);

                        mSwipeListView = (SwipeListView) findViewById(R.id.list_view);
                        mSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

                            @Override
                            public void onDismiss(int[] reverseSortedPositions) {
                                Log.d(TAG, "onDismiss");
                                for (int position : reverseSortedPositions) {
                                    Item item = (Item) mSwipeListView.getItemAtPosition(position);
                                    RestClient.get().deleteItem(item.getCollectionId(), item.getId(), new Callback<Item>() {
                                        @Override
                                        public void success(Item item, Response response) {
                                            Log.d(TAG, "postItem response : " + response.toString());
                                            RestClient.get().getItems(4, new Callback<List<Item>>() {
                                                @Override
                                                public void success(List<Item> items, Response response) {
                                                    mSwipeListView.setAdapter(new ItemsAdapter(CollectionActivity.this, items));
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.d(TAG, "getItems error : " + error.toString());
                                                }
                                            });
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Log.d(TAG, "deleteItem error : " + error.toString());
                                        }
                                    });
                                }
                            }
                        });

                        ((ImageView) findViewById(R.id.add_item)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final EditText itemName = new EditText(CollectionActivity.this);
                                new AlertDialog.Builder(CollectionActivity.this)
                                        .setTitle("Item name")
                                        .setView(itemName)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                String inputText = itemName.getText().toString();

                                                RestClient.get().postItem(collections.get(position).getId(), new ItemPostRequestBody(inputText, 4), new Callback<Item>() {
                                                    @Override
                                                    public void success(Item item, Response response) {
                                                        Log.d(TAG, "postItem response : " + response.toString());
                                                        RestClient.get().getItems(4, new Callback<List<Item>>() {
                                                            @Override
                                                            public void success(List<Item> items, Response response) {
                                                                mSwipeListView.setAdapter(new ItemsAdapter(CollectionActivity.this, items));
                                                            }

                                                            @Override
                                                            public void failure(RetrofitError error) {
                                                                Log.d(TAG, "getItems error : " + error.toString());
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        Log.d(TAG, "postItem error : " + error.toString());
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Toast.makeText(getBaseContext(), "What kind of scavenger are you?!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                            }
                        });

                        return view;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((View) object);
                        Log.i(TAG, "destroyItem() [position: " + position + "]");
                    }
                });

                mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
                mSlidingTabLayout.setViewPager(mViewPager);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "error : " + error);
            }
        });
    }


}

