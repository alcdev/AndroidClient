package com.swtracks.timetracks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    TextView domainText, portText;
    Button saveButton;
    private SharedPreferences settings;
    private String domain, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        domainText = (TextView) findViewById(R.id.domain);
        portText = (TextView) findViewById(R.id.port);
        saveButton = (Button) findViewById(R.id.save_button);

        settings = getSharedPreferences("userinfo", MODE_PRIVATE);
        domain = settings.getString("domain", "");
        port = settings.getString("port", "");

        if (!domain.isEmpty()) {
            domainText.setText(domain);
        }

        if (!port.isEmpty()) {
            portText.setText(port);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                domain = domainText.getText().toString();
                port = portText.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("domain", domain);

                if (!port.isEmpty()) {
                    editor.putString("port", port);
                }

                editor.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    
}
