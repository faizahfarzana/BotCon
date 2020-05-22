package com.example.trial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class FindPairedDevices extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    ImageButton forward, backward, left, right, spin, circle, horn, light;
    SeekBar speed;

    Button buttonShowPairedDevices, disconnect;
    //ListView showListPairedDevices;
    private Set <BluetoothDevice> pairedDevices;
    private BluetoothAdapter bluetoothAdapter = null;

    BluetoothSocket Socket = null;
    private boolean isBthConnected = false;
    static final UUID deviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_device);

        initializer();
        checkBluetooth();
    }

    public void checkBluetooth() {
        if(bluetoothAdapter == null)
        {
            toast("This device does not have Bluetooth");
            //finish();
        }
        else if(!bluetoothAdapter.isEnabled())
        {
            Intent turnOnBth = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBth,1);
        }
    }

    private void showPairedDevices()
    {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        final ArrayList listPairedDevices = new ArrayList();

        if (pairedDevices.size() > 0)
            for(BluetoothDevice device : pairedDevices)
                listPairedDevices.add(device.getName() + "\n" + device.getAddress());

        else    toast("No Paired Bluetooth Devices Found");

        AlertDialog.Builder popUpList = new AlertDialog.Builder(this);
        popUpList.setIcon(R.drawable.ic_launcher_foreground);
        popUpList.setTitle("Select a device!");

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_main, R.id.customTextView, listPairedDevices);

        popUpList.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deviceInfo = listPairedDevices.get(which).toString();
                address = deviceInfo.substring (deviceInfo.length() - 17);

                new ConnectBth().execute();
            }
        });
        popUpList.show();

//        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_main, R.id.textView, listPairedDevices);
//        showListPairedDevices.setAdapter(adapter);
//        showListPairedDevices.setOnItemClickListener(connectBot);
    }

//    private AdapterView.OnItemClickListener connectBot = new AdapterView.OnItemClickListener()
//    {
//        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
//        {
//            deviceInfo = ((TextView)v.findViewById(R.id.textView)).getText().toString();
//
//            address = deviceInfo.substring (deviceInfo.length() - 17);
//            new ConnectBth().execute();
//        }
//    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.menu_device_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class ConnectBth extends AsyncTask<Void, Void, Void>
    {
        private boolean connected = true;

        @Override
        protected void onPreExecute() { }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (Socket == null || !isBthConnected)
                {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bthDevice = bluetoothAdapter.getRemoteDevice(address);
                    Socket = bthDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Socket.connect();
                }
            }
            catch (IOException e)
            {
                connected = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!connected)
            {
                toast("Connection failed.");
                //finish();
            }
            else
            {
                toast("Connected.");
                isBthConnected = true;
            }
        }
    }

    public void initializer() {
        //showListPairedDevices = findViewById(R.id.listView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        buttonShowPairedDevices = findViewById(R.id.button);
        disconnect = findViewById(R.id.disconnect);

        forward    = findViewById(R.id.imageButton_forward);
        backward   = findViewById(R.id.imageButton_backward);
        left       = findViewById(R.id.imageButton_left);
        right      = findViewById(R.id.imageButton_right);
        spin       = findViewById(R.id.imageButton_spin);
        circle     = findViewById(R.id.imageButton_circle);
        horn       = findViewById(R.id.imageButton_horn);
        light      = findViewById(R.id.imageButton_light);
        speed      = findViewById(R.id.seekbar_speed);

        buttonShowPairedDevices.setOnClickListener (this);
        disconnect.setOnClickListener(this);
        
        forward.setOnTouchListener  (this);
        backward.setOnTouchListener (this);
        left.setOnTouchListener     (this);
        right.setOnTouchListener    (this);
        horn.setOnTouchListener    (this);;
        light.setOnTouchListener    (this);
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.button:
                showPairedDevices();
                break;

            case R.id.disconnect:
                if (Socket != null)
                {
                    try
                    {
                        Socket.close();
                        toast("Disconnected");

                    }
                    catch (IOException e)
                    {
                        toast("Error");
                    }
                }
                break;
        }
    }

    @Override
    public boolean  onTouch (View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.imageButton_forward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("forward");
                    sendData("F");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;

            case R.id.imageButton_backward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("backward");
                    sendData("B");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;

            case R.id.imageButton_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("left");
                    sendData("L");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;

            case R.id.imageButton_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("Right");
                    sendData("R");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;

            case R.id.imageButton_horn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("Horn");
                    sendData("H");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;

            case R.id.imageButton_light:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toast("Light");
                    sendData("L");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toast("stopped");
                    sendData("S");
                }
                break;
        }
        return true;
    }

    private void sendData(String s)
    {
        if (Socket != null)
        {
            try
            {
                Socket.getOutputStream().write(s.toString().getBytes());
                toast("Done");

            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }

    private void toast(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
