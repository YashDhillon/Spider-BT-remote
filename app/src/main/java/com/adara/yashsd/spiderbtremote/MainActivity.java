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
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    String DEVICE_ADDRESS = "";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    Button b1, b2, b3, b4;
    JoystickView joy;
    TextView sonarpitch,sonarangle;

    SeekBar radarx,radary,radarz;

    public static final float MAX = 100f, MIN = 1f;

    public static final int NB_QUALITIES = 36;

    private RadarChart chart;

    final static public String DISTANCE = "DISTANCE";

    ProgressBar distance;

    private IntentFilter mIntentfilter;

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DISTANCE)) {
                distance.setProgress(intent.getIntExtra("DISTANCE",0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sonarangle = (TextView)findViewById(R.id.sonarangle);
        sonarpitch = (TextView)findViewById(R.id.sonarpitch);

        distance = (ProgressBar)findViewById(R.id.distance);

        mIntentfilter = new IntentFilter();
        mIntentfilter.addAction(DISTANCE);
        registerReceiver(mReciever, mIntentfilter);

        if (checkPermission(Manifest.permission.BLUETOOTH) && checkPermission(Manifest.permission.BLUETOOTH_ADMIN)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    007);
        }
    //----------------------------------------------------------------------------------------------
        joy = (JoystickView) findViewById(R.id.joy);
        joy.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (angle >= 45 && angle < 135) {
                    try {
                        outputStream.write(("*0f:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 135 && angle < 225) {
                    try {
                        outputStream.write(("*0l:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 225 && angle < 315) {
                    try {
                        outputStream.write(("*0b:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 315 && angle <= 359) {
                    try {
                        outputStream.write(("*0r:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (angle >= 0 && angle < 45) {
                    try {
                        outputStream.write(("*0r:#").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500);
    //----------------------------------------------------------------------------------------------
        b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if( intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(intent,006);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "this feature is not supported by your device", Toast.LENGTH_SHORT).show();
                }

            }
        });

        b2 = (Button) findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivityForResult(i, 007);
            }
        });

        b3 = (Button) findViewById(R.id.b3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("%0b:#".getBytes());
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
                    outputStream.write("%0c:#".getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
        radarx = (SeekBar)findViewById(R.id.radarx);
        radarx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    outputStream.write(("!"+String.format("%03d", progress)+"#").getBytes());
                    sonarangle.setText(progress+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radary = (SeekBar)findViewById(R.id.radary);
        radary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    outputStream.write(("@"+String.format("%03d", progress)+"#").getBytes());
                    sonarpitch.setText(progress+"");
                  } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radarz = (SeekBar)findViewById(R.id.radarz);
        radarz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    outputStream.write(("^"+String.format("%03d", progress)+"#").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    //----------------------------------------------------------------------------------------------
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
        } else if (requestCode == 006) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String speech = result.get(0);
                if (speech.contains("forward")) {
                    if (speech.contains("one") || speech.contains("1")) {
                        try {
                            outputStream.write(("$1f:#").getBytes());
                            Toast.makeText(this, "$1f:#", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("two") || speech.contains("2")) {
                        try {
                            outputStream.write(("$2f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("three") || speech.contains("3")) {
                        try {
                            outputStream.write(("$3f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("four") || speech.contains("4")) {
                        try {
                            outputStream.write(("$4f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("five") || speech.contains("5")) {
                        try {
                            outputStream.write(("$5f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("six") || speech.contains("6")) {
                        try {
                            outputStream.write(("$6f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("seven") || speech.contains("7")) {
                        try {
                            outputStream.write(("$7f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("eight") || speech.contains("8")) {
                        try {
                            outputStream.write(("$8f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("nine") || speech.contains("9")) {
                        try {
                            outputStream.write(("$9f:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (speech.contains("right")) {
                    if (speech.contains("one") || speech.contains("1")) {
                        try {
                            outputStream.write(("$1r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("two") || speech.contains("2")) {
                        try {
                            outputStream.write(("$2r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("three") || speech.contains("3")) {
                        try {
                            outputStream.write(("$3r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("four") || speech.contains("4")) {
                        try {
                            outputStream.write(("$4r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("five") || speech.contains("5")) {
                        try {
                            outputStream.write(("$5r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("six") || speech.contains("6")) {
                        try {
                            outputStream.write(("$6r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("seven") || speech.contains("7")) {
                        try {
                            outputStream.write(("$7r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("eight") || speech.contains("8")) {
                        try {
                            outputStream.write(("$8r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("nine") || speech.contains("9")) {
                        try {
                            outputStream.write(("$9r:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else if (speech.contains("left")) {
                    if (speech.contains("one") || speech.contains("1")) {
                        try {
                            outputStream.write(("$1l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("two") || speech.contains("2")) {
                        try {
                            outputStream.write(("$2l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("three") || speech.contains("3")) {
                        try {
                            outputStream.write(("$3l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("four") || speech.contains("4")) {
                        try {
                            outputStream.write(("$4l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("five") || speech.contains("5")) {
                        try {
                            outputStream.write(("$5l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("six") || speech.contains("6")) {
                        try {
                            outputStream.write(("$6l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("seven") || speech.contains("7")) {
                        try {
                            outputStream.write(("$7l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("eight") || speech.contains("8")) {
                        try {
                            outputStream.write(("$8l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("nine") || speech.contains("9")) {
                        try {
                            outputStream.write(("$9l:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (speech.contains("back")) {
                    if (speech.contains("one") || speech.contains("1")) {
                        try {
                            outputStream.write(("$1b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("two") || speech.contains("2")) {
                        try {
                            outputStream.write(("$2b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("three") || speech.contains("3")) {
                        try {
                            outputStream.write(("$3b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("four") || speech.contains("4")) {
                        try {
                            outputStream.write(("$4b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("five") || speech.contains("5")) {
                        try {
                            outputStream.write(("$5b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("six") || speech.contains("6")) {
                        try {
                            outputStream.write(("$6b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("seven") || speech.contains("7")) {
                        try {
                            outputStream.write(("$7b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("eight") || speech.contains("8")) {
                        try {
                            outputStream.write(("$8b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (speech.contains("nine") || speech.contains("9")) {
                        try {
                            outputStream.write(("$9b:#").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
            Toast.makeText(this, "System :: Connected to spider", Toast.LENGTH_SHORT).show();
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

            connectedThread mConnectedThread = new connectedThread(socket);
            mConnectedThread.start();
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
                    Toast.makeText(MainActivity.this, writeMessage, Toast.LENGTH_SHORT).show();
                    char reply = writeMessage.charAt(0);
                    if(reply == '@'){
                        int DISTANCE = Integer.parseInt(writeMessage.substring(1));
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(MainActivity.DISTANCE);
                        broadcastIntent.putExtra("DISTANCE",DISTANCE);
                        sendBroadcast(broadcastIntent);
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
