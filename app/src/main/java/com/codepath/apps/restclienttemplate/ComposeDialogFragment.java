package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import javax.annotation.Nullable;

import okhttp3.Headers;


public class ComposeDialogFragment extends DialogFragment {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button tweetButton;

    TwitterClient client;

    public ComposeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static ComposeDialogFragment newInstance() {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        return fragment;
    }

    /**
     * Defines the compose listener interface with a method passing back data result.
     */
    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_dialogue, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        tweetButton = view.findViewById(R.id.tweetButton);

        client = TwitterApp.getRestClient(this.getContext());

        // Set click listener on the button
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();

                if (tweetContent.isEmpty()) {
                    Snackbar.make(tweetButton, R.string.compose_empty, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Snackbar.make(tweetButton, R.string.compose_tooLong, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                // Make API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                            if (listener != null) {
                                listener.onFinishComposeDialog(tweet);
                            }
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}