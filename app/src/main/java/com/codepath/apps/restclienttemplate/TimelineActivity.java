package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {

    private SwipeRefreshLayout swipeContainer;
    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Code to refresh line
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    // Sends out a network request and appends new data items to your adapter
    private void loadNextDataFromApi(int page) {
        final int CURRENT_POSITION = adapter.getItemCount();
        client.getTweets(getLastTweetID(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Add new items to the adapter
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyItemRangeInserted(CURRENT_POSITION, tweets.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Endless Scrolling is working");
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Endless Scroll error loading");
            }
        });
    }

    public Long getLastTweetID() {
        Tweet last_tweet = tweets.get(tweets.size()-1);
        return last_tweet.id;
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Clear out old items before adding new ones
                adapter.clear();
                // Add new items to the adapter
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    // Now setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure", throwable);
            }
        });
    }

// Commented out because now using floating button & fragment dialogue
// -> this was how you create a menu with a button and have that button start a new activity
// + return the data from the activity to this main activity
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; adds items to actionbar if it's present
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.compose) {
//            // Compose icon selected
//            // Navigate to compose activity
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            // Get data from the intent (tweet)
//            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
//            // Update the RecyclerView with this new tweet
//            // Modify data source of tweets
//            tweets.add(0, tweet);
//            // Update the adapter
//            adapter.notifyItemInserted(0);
//            rvTweets.smoothScrollToPosition(0);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });
    }

    public void onLogoutButton(View view) {
        // Clear login info
        TwitterApp.getRestClient(this).clearAccessToken();

        // Return back to login screen (could also do this with 'finish()')
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Ensures the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
    }

    public void onComposeButton(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance();
        composeDialogFragment.show(fm, "compose_fragment");
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        // Update the RecyclerView with this new tweet
        // Modify data source of tweets
        tweets.add(0, tweet);
        // Update the adapter
        adapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);
    }
}