package com.swtracks.timetracks;

import android.content.Context;
import android.content.Intent;
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
    Button cancelButton;
    private SharedPreferences settings;
    private String domain, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        domainText = (TextView) findViewById(R.id.domain);
        portText = (TextView) findViewById(R.id.port);
        saveButton = (Button) findViewById(R.id.save_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);


        settings = getSharedPreferences("userinfo", MODE_PRIVATE);
        domain = settings.getString("domain", "");
        port = settings.getString("port", "");

        if (!domain.isEmpty()) {
            domainText.setText(domain);
        }

        if (!port.isEmpty()) {
            portText.setText(port);
        }

        domainText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            String HTTP = "http://";
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String text = domainText.getText().toString();
                if (hasFocus && text.isEmpty()) {
                    domainText.append(HTTP);
                }

                if (!hasFocus && text.equals(HTTP)) {
                    domainText.setText("");
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                domain = domainText.getText().toString();
                port = portText.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("domain", domain);
                editor.putString("port", port);

                if (port.isEmpty()) {
                    editor.remove("port");
                }

                editor.commit();

                // Always return to the main activity.
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
