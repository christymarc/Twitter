package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ComposeActivity extends AppCompatActivity {

    final int MAX_TWEET_LENGTH = 240;

    EditText etCompose;
    Button tweetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        tweetButton = findViewById(R.id.tweetButton);

        // Set click listener on the button
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();

                //TODO: CHRISTY -> look into 'working with edittext' resource

                if (tweetContent.isEmpty()) {

                    //TODO: replace toasts --> put string or android snackbar
                    Toast.makeText(ComposeActivity.this,
                            "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this,
                            "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this,
                        tweetContent, Toast.LENGTH_LONG).show();
                return;
                // Make API call to Twitter to publish the tweet
            }
        });
    }
}