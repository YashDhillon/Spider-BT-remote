package com.adara.yashsd.spiderbtremote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    String DEVICE_ADDRESS = "";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11;
    JoystickView joy;
    TextView logs;

    //final static public String VOICE = "VOICE";
    //final static public String JOY = "JOY";
    //final static public String BUTT = "BUTT";
    //final static public String LOG = "LOG";


    //private IntentFilter mIntentfilter;

    /*private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(REFRESHRESULT)) {
                String flowRate = intent.getExtras().getString("flowRate");
                String tankStatus = intent.getExtras().getString("tankStatus");
                String timeStatus = intent.getExtras().getString("timeStatus");
                refreshResult(flowRate, tankStatus, timeStatus);
                int flowRateInt = 0;
                flowRateInt = Integer.parseInt(flowRate);
                if (flowRateInt > 0) {
                    tankAnimate(true, tankStatus);
                } else {
                    tankAnimate(false, tankStatus);
                }
            } else if (intent.getAction().equals(NORMALUPDATERESULT)) {
                String flowRate = intent.getExtras().getString("flowRate");
                String tankStatus = intent.getExtras().getString("tankStatus");
                String timeStatus = intent.getExtras().getString("timeStatus");
                tvFlowRate.setText(flowRate + "L/s");
                tvRunTime.setText(timeStatus + "min");
                donutProgressUpdate(tankStatus);
                int flowRateInt = 0;
                flowRateInt = Integer.parseInt(flowRate);
                if (flowRateInt > 0) {
                    tankAnimate(true, tankStatus);
                } else {
                    tankAnimate(false, tankStatus);
                }
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission(Manifest.permission.BLUETOOTH) && checkPermission(Manifest.permission.BLUETOOTH_ADMIN)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    007);
        }

        //------------------------------------------------------------------------------------------
        //mIntentfilter = new IntentFilter();
        //mIntentfilter.addAction(VOICE);
        //mIntentfilter.addAction(JOY);
        //mIntentfilter.addAction(BUTT);
        //mIntentfilter.addAction(LOG);
        //registerReceiver(mReciever, mIntentfilter);
        //------------------------------------------------------------------------------------------

        logs = (TextView)findViewById(R.id.logs);

        joy = (JoystickView) findViewById(R.id.joy);
        joy.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (angle >= 45 && angle < 135) {
                    try {
                        outputStream.write(("*f:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 135 && angle < 225) {
                    try {
                        outputStream.write(("*l:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 225 && angle < 315) {
                    try {
                        outputStream.write(("*b:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 315 && angle <= 359) {
                    try {
                        outputStream.write(("*r:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 0 && angle < 45) {
                    try {
                        outputStream.write(("*r:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500);


        b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivityForResult(i, 007);
            }
        });

        b2 = (Button) findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%a:#".getBytes());
                    displayCommLogs("User :: Sit down");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        b3 = (Button) findViewById(R.id.b3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%b:#".getBytes());
                    displayCommLogs("User :: Stand up");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        b4 = (Button) findViewById(R.id.b4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%c:#".getBytes());
                    displayCommLogs("User :: Wave hand");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        b5 = (Button) findViewById(R.id.b5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%d:#".getBytes());
                    displayCommLogs("User :: Hand shake");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b6 = (Button) findViewById(R.id.b6);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%e:#".getBytes());
                    displayCommLogs("User :: Move right");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b7 = (Button) findViewById(R.id.b7);
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%f:#".getBytes());
                    displayCommLogs("User :: Move left");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b8 = (Button) findViewById(R.id.b8);
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%g:#".getBytes());
                    displayCommLogs("User :: Head up");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b9 = (Button) findViewById(R.id.b9);
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%h:#".getBytes());
                    displayCommLogs("User :: Head down");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b10 = (Button) findViewById(R.id.b10);
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%i:#".getBytes());
                    displayCommLogs("User :: Dance");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        b11 = (Button) findViewById(R.id.b11);
        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%j:#".getBytes());
                    displayCommLogs("User :: Lights");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 007) {

            DEVICE_ADDRESS = data.getExtras().getString("address");

            if (DEVICE_ADDRESS.equals("null")) {
                Toast.makeText(this, "Invalid Address", Toast.LENGTH_SHORT).show();
            } else {
                if (BTinit()) {
                    BTconnect();
                }
            }
        }
    }

    //-----------------------------NEW BLUETOOTH COMM SYSTEM----------------------------------------
    public boolean BTinit() {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            displayCommLogs("System :: Connected to spider");
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return connected;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    //----------------------------------------------------------------------------------------------


    //------------------------NEW BLUETOOTH RECEPTION SYSTEM----------------------------------------

    @SuppressLint("HandlerLeak")
    Handler bthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;
            switch (msg.what) {
                case 1: {
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);

                    char reply = writeMessage.charAt(0);

                    if (reply == '0') {

                    } else if (reply == '1') {

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private class connectedThread extends Thread {
        private final BluetoothSocket mysocket;
        private final InputStream minstr;
        private final OutputStream moutstr;

        public connectedThread(BluetoothSocket mysocket) {
            this.mysocket = mysocket;
            InputStream tempin = null;
            OutputStream tempout = null;
            try {
                tempin = mysocket.getInputStream();
                tempout = mysocket.getOutputStream();
            } catch (IOException e) {
            }
            minstr = tempin;
            moutstr = tempout;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += minstr.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "#".getBytes()[0]) {
                            bthandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }

            }
        }
    }
    //----------------------------------------------------------------------------------------------

    void displayCommLogs(String action){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());

        String finalStamp = "["+format.substring(0,2)+"/"+format.substring(2,4)+"/"+format.substring(4,8)
                +"||"+format.substring(8,10)+":"+format.substring(10,12)+":"+format.substring(12)+"]- ";

        logs.append("\n"+finalStamp+action+";");
    }

    //----------------------------------------------------------------------------------------------

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 007: {
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
