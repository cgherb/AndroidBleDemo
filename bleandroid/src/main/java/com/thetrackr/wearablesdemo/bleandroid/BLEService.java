package com.thetrackr.wearablesdemo.bleandroid;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.UUID;

public class BLEService extends Service
{

    public static final UUID IMMEDIATE_ALERT_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID ALERT_LEVEL_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");


    public static final byte NO_ALERT = 0;
    public static final byte LOW_ALERT = 1;
    public static final byte HIGH_ALERT = 2;

    private final IBinder bleServiceBinder = new bleServiceBinder();

    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter mBtAdapter = null;

    //Hanlder for the scan cancel
    private Handler mHandler = new Handler();

    private BluetoothDevice deviceToConnect;
    private BluetoothGatt connectedGattDevice;

    @SuppressLint("NewApi")
    @Override
    public void onCreate()
    {
        super.onCreate();
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();
        if (mBtAdapter == null)
            {
            //ERROR: User doesn't have bluetooth turned on!  Ask them to turn it on!
            return;
            }
        mBtAdapter.enable();

        bootReceiver();

    }

    private void bootReceiver()
    {
        IntentFilter bleIntentReceiver = new IntentFilter();
        bleIntentReceiver.addAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_DISCOVER_DEVICES);
        bleIntentReceiver.addAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_CONNECT);
        bleIntentReceiver.addAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_RING_DEVICE);
        bleIntentReceiver.addAction(BleDemoConstants.ACTION_BLE_FRAMEWORK_SILENCE_DEVICE);
        getApplicationContext().registerReceiver(bleDemoReceiver, bleIntentReceiver);
    }

    private final BroadcastReceiver bleDemoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(BleDemoConstants.ACTION_BLE_FRAMEWORK_DISCOVER_DEVICES))
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mBtAdapter.stopLeScan(mLeScanCallback);
                    }
                }, 10000);

                if(mBtAdapter.isEnabled())
                {
                    mBtAdapter.startLeScan(mLeScanCallback);
                }
            }

            else if(intent.getAction().equals(BleDemoConstants.ACTION_BLE_FRAMEWORK_CONNECT))
            {
                deviceToConnect.connectGatt(getApplicationContext(), true, mGattCallback);
            }
            else if(intent.getAction().equals(BleDemoConstants.ACTION_BLE_FRAMEWORK_RING_DEVICE))
            {
                BluetoothGattService iaService = connectedGattDevice.getService(IMMEDIATE_ALERT_UUID);
                try
                {
                    BluetoothGattCharacteristic alertLevelChar = iaService.getCharacteristic(ALERT_LEVEL_UUID);
                    alertLevelChar.setValue(HIGH_ALERT,BluetoothGattCharacteristic.FORMAT_UINT8,0);
                    if(alertLevelChar != null)
                    {
                        if(!connectedGattDevice.writeCharacteristic(alertLevelChar))
                        {
                            Log.v("BLEService", "Ring Device Error");
                        }
                    }
                }catch (NullPointerException nullerror)
                {
                    nullerror.printStackTrace();
                }
            }
            else if(intent.getAction().equals(BleDemoConstants.ACTION_BLE_FRAMEWORK_SILENCE_DEVICE))
            {
                BluetoothGattService iaService = connectedGattDevice.getService(IMMEDIATE_ALERT_UUID);
                try
                {
                    BluetoothGattCharacteristic alertLevelChar = iaService.getCharacteristic(ALERT_LEVEL_UUID);
                    alertLevelChar.setValue(NO_ALERT,BluetoothGattCharacteristic.FORMAT_UINT8,0);
                    if(alertLevelChar != null)
                    {
                        if(!connectedGattDevice.writeCharacteristic(alertLevelChar))
                        {
                            Log.v("BLEService", "Ring Device Error");
                        }
                    }
                }catch (NullPointerException nullerror)
                {
                    nullerror.printStackTrace();
                }
            }
            else if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                final BluetoothDevice tempDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("BLEService", "Bond State changed to " + tempDevice.getBondState());
            }
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.v("BLEService", "device is " + device.getAddress());
            if(device.getAddress().equalsIgnoreCase("C7:44:1D:A4:8B:58"))
            {
                deviceToConnect = device;
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.v("BLEService", "connection change to " + newState + " with status " + status);
            if(newState == 2)
            {
                connectedGattDevice = gatt;
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("BLEService", "onServicesDiscovered() " + status);
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return this so the service will stay alive
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bleServiceBinder;
    }

    public class bleServiceBinder extends Binder
    {
        public BLEService getService()
        {
            return BLEService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}