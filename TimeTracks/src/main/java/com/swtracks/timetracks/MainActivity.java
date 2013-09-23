package com.swtracks.timetracks;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    TimeTracksAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // remove this.  hacky hack.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        api = new TimeTracksAPI(this);
        if (!api.Autologin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true; //super.onOptionsItemSelected(item);
        }
    }
    
}
