package com.swtracks.timetracks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends Activity {

    TimeTracksAPI api;
    private UserLoginTask mAuthTask = null;
    TextView userText;
    Boolean haveInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        api = new TimeTracksAPI(this);

        if (!haveInfo) {
            haveInfo = true;
            getUserInfo();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void getUserInfo() {
        if (mAuthTask != null) {
            return;
        }

        mAuthTask = new UserLoginTask();
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        @Override
        protected Boolean doInBackground(Void... params) {
            text = api.GetUserInfo();

            // Doesn't matter what we return.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            userText = (TextView) findViewById(R.id.userText);

            if (!text.isEmpty()){
                Log.i("text", text);
                userText.setText(text);
            } else{
                userText.setText("Error retrieving user info.");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
