package com.thetrackr.wearablesdemo.bleandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    private Button scanButton;
    private Button connectButton;
    private Button ringButton;
    private Button silenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = (Button)findViewById(R.id.button);
        connectButton = (Button)findViewById(R.id.button2);
        ringButton = (Button)findViewById(R.id.button3);
        silenceButton = (Button)findViewById(R.id.button4);

        Intent startBleServiceIntent = new Intent(getApplicationContext(), BLEService.class);
        startService(startBleServiceIntent);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_DISCOVER_DEVICES);
                sendBroadcast(i);

            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_CONNECT);
                sendBroadcast(i);
            }
        });

        ringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_RING_DEVICE);
                sendBroadcast(i);
            }
        });

        silenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_SILENCE_DEVICE);
                sendBroadcast(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
