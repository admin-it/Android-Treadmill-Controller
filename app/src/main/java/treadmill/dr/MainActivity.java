package treadmill.dr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

//Created by Enyil Valle
public class MainActivity extends AppCompatActivity {

    int MAX_SPEED = 14;
    int MIN_SPEED = 0;
    int MAX_INCLINE = 15;
    int MIN_INCLINE = 0;
    int CHANGE_DELAY = 200;
    SharedPreferences sharedpreferences;
    public static final String SaveData = "TRSMCT" ;
    public static final String WData = "WGHT" ;
    public static final String MSP = "SPRSPD" ;
    public static final String MINC = "MTEV" ;
    public static final String KPHMPH = "EORA" ;
    EditText Speed;
    EditText Incline;
    BluetoothAdapter adapter;
    FloatingActionButton fab;
    Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        String weight = null, incline = null, speed = null, type = null;
        sharedpreferences = getSharedPreferences(SaveData, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        weight = sharedpreferences.getString(WData, null);
        speed = sharedpreferences.getString(MSP, null);
        incline = sharedpreferences.getString(MINC, null);
        type = sharedpreferences.getString(KPHMPH, null);
        if(weight == null | incline == null | speed == null | type == null)
        {
            editor.putString(WData, "200");
            editor.putString(MSP, "15.0");
            editor.putString(MINC, "1.0");
            editor.putString(KPHMPH, "Mile");
            editor.commit();
        }



        fab = (FloatingActionButton) findViewById(R.id.fab);
        act = this;
        adapter = BluetoothAdapter.getDefaultAdapter();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                //code for bluetooth connection goes here
//                if (!adapter.isEnabled()) {
//                    Snackbar.make(view, "Please enable bluetooth", Snackbar.LENGTH_LONG)
//                            .setAction("Turn on", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBluetooth, 1);
//                                }
//                            }).show();
//                } else {
//                    Snackbar.make(view, "Starting bluetooth communication", Snackbar.LENGTH_LONG)
//                            .setAction("Scan", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    IntentFilter filter = new IntentFilter();
//                                    filter.addAction(BluetoothDevice.ACTION_FOUND);
//                                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//                                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//                                    registerReceiver(mReceiver, filter);
//                                    adapter.startDiscovery();
//                                }
//                            }).show();
//                }
            }
        });
        ImageButton speedUp = (ImageButton) findViewById(R.id.btnSpeedUp);
        ImageButton speedDown = (ImageButton) findViewById(R.id.btnSpeedDown);
        ImageButton inclineUp = (ImageButton) findViewById(R.id.btnInclineUp);
        ImageButton inclineDown = (ImageButton) findViewById(R.id.btnInclineDown);
        Speed = (EditText) findViewById(R.id.etSpeed);
        Incline = (EditText) findViewById(R.id.etIncline);
        speedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedUp();
            }
        });
        speedUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                speedIncrement = true;
                speedChange.post(new SpeedHold());
                return false;
            }
        });
        speedUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && speedIncrement) {
                    speedIncrement = false;
                }
                return false;
            }
        });
        speedDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedDown();
            }
        });
        speedDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                speedDecrement = true;
                speedChange.post(new SpeedHold());
                return false;
            }
        });
        speedDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && speedDecrement) {
                    speedDecrement = false;
                }
                return false;
            }
        });
        inclineUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inclineUp();
                try {
                    socket.getOutputStream().write("1".toString().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        inclineUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inclineIncrement = true;
                inclineChange.post(new InclineHold());
                return false;
            }
        });
        inclineUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && inclineIncrement) {
                    inclineIncrement = false;
                }
                return false;
            }
        });
        inclineDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inclineDown();
                try {
                    socket.getOutputStream().write("0".toString().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        inclineDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inclineDecrement = true;
                inclineChange.post(new InclineHold());
                return false;
            }
        });
        inclineDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && inclineDecrement) {
                    inclineDecrement = false;
                }
                return false;
            }
        });
    }
    //Speed up and down buttons
    private Handler speedChange = new Handler();
    private boolean speedIncrement = false;
    private boolean speedDecrement = false;
    class SpeedHold implements Runnable {
        public void run() {
            if( speedIncrement ){
                speedUp();
                speedChange.postDelayed( new SpeedHold(), CHANGE_DELAY );
            } else if( speedDecrement ){
                speedDown();
                speedChange.postDelayed( new SpeedHold(), CHANGE_DELAY );
            }
        }
    }

    public void speedUp() {
        String[] numbers = Speed.getText().toString().split("\\.");
        int currentSpeed;
        int decimal;
        try {
            currentSpeed = Integer.valueOf(numbers[0]);
            decimal = Integer.valueOf(numbers[1]);
        } catch (Exception e) {
            currentSpeed = Integer.valueOf(Speed.getText().toString());
            decimal = 0;
            Log.i("SPEED_UP","Whole Numer");
            e.printStackTrace();
        }
        decimal++;
        if(decimal > 9) {
            currentSpeed += 1;
            decimal = 0;
        }
        if(currentSpeed > MAX_SPEED) {
            currentSpeed = MAX_SPEED;
        }
        Speed.setText(Integer.toString(currentSpeed) + "." + Integer.toString(decimal));
    }

    public void speedDown() {
        String[] numbers = Speed.getText().toString().split("\\.");
        int currentSpeed;
        int decimal;
        try {
            currentSpeed = Integer.valueOf(numbers[0]);
            decimal = Integer.valueOf(numbers[1]);
        } catch (Exception e) {
            currentSpeed = Integer.valueOf(Speed.getText().toString());
            decimal = 0;
            Log.i("SPEED_DOWN","Whole Numer");
        }
        decimal--;
        if(decimal < 0) {
            currentSpeed -= 1;
            decimal = 9;
        }
        if(currentSpeed < MIN_SPEED) {
            currentSpeed = MIN_SPEED;
            decimal = 0;
        }
        Speed.setText(Integer.toString(currentSpeed) + "." + Integer.toString(decimal));
    }

    //Incline buttons
    private Handler inclineChange = new Handler();
    private boolean inclineIncrement = false;
    private boolean inclineDecrement = false;
    class InclineHold implements Runnable {
        public void run() {
            if( inclineIncrement ){
                inclineUp();
                inclineChange.postDelayed( new InclineHold(), CHANGE_DELAY );
            } else if( inclineDecrement ){
                inclineDown();
                inclineChange.postDelayed( new InclineHold(), CHANGE_DELAY );
            }
        }
    }

    public void inclineUp() {
        int currentIncline = Integer.valueOf(Incline.getText().toString());
        currentIncline++;
        if(currentIncline > MAX_INCLINE) {
            currentIncline = MAX_INCLINE;
        }
        Incline.setText(Integer.toString(currentIncline));
    }

    public void inclineDown() {
        int currentIncline = Integer.valueOf(Incline.getText().toString());
        currentIncline--;
        if(currentIncline < MIN_INCLINE) {
            currentIncline = MIN_INCLINE;
        }
        Incline.setText(Integer.toString(currentIncline));
    }

    BluetoothSocket socket;
    ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    ArrayAdapter<String> arrayAdapter;
    ProgressDialog dialog;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("BLUETOOTH", "START DISCOVERY");
                //deviceList = new ArrayList<BluetoothDevice>();
                dialog = ProgressDialog.show(act, "",
                        "Scanning for devices...", true);
                deviceList = new ArrayList<BluetoothDevice>();
                arrayAdapter = new ArrayAdapter<String>(
                        act,
                        android.R.layout.select_dialog_singlechoice);;
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("BLUETOOTH", "DONE DISCOVERY");
                dialog.dismiss();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(act);
                //builderSingle.setIcon();
                builderSingle.setTitle("Select a device to connect: ");
                //arrayAdapter = new ArrayAdapter<String>(
                  //      act,
                    //    android.R.layout.select_dialog_singlechoice);
//               for(String s : deviceList){
//                   arrayAdapter.add(s);
//               }
                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                dialog.dismiss();
                                final int lewitch = which;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //System.out.println("Bluetooth Bond " + deviceList.get(which).createBond());
                                            UUID id = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                                            socket = deviceList.get(lewitch).createRfcommSocketToServiceRecord(id);
                                            //socket.connect();
                                            Class<?> clazz = socket.getRemoteDevice().getClass();
                                            Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                                            Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                                            Object[] params = new Object[] {Integer.valueOf(1)};
                                            socket = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
                                            socket.connect();
                                            //socket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
//                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
//                                        act);
//                                builderInner.setMessage("Establishing Connection");
//                                builderInner.setTitle("Connect to " + strName + "?");
//                                builderInner.setPositiveButton(
//                                        "Yes",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(
//                                                    DialogInterface dialog,
//                                                    int which) {
//
//                                                dialog.dismiss();
//                                            }
//                                        }).setNegativeButton("No", null);
//                                builderInner.show();

                                //connecting code

                            }
                        });
                builderSingle.show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                Log.d("BLUETOOTH", "NEW DEVICE FOUND");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    deviceList.add(device);
                    arrayAdapter.add(device.getName());
                } catch (Exception e){

                }
                //Toast.makeText(act, device.getAddress(),Toast.LENGTH_LONG).show();
                //adapter.listenUsingRfcommWithServiceRecord(adapter.getName(), adapter.MY_UUID);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_bluetooth) {
            //code for bluetooth connection goes here
            if (adapter == null){
                new AlertDialog.Builder(act)
                        .setTitle("Bluetooth not supported")
                        .setMessage("This device does not support bluetooth!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .show();
            } else if (!adapter.isEnabled()) {
                Snackbar.make(fab, "Please enable bluetooth", Snackbar.LENGTH_LONG)
                        .setAction("Turn on", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBluetooth, 1);
                            }
                        }).show();
            } else {
                Snackbar.make(fab, "Starting bluetooth communication", Snackbar.LENGTH_LONG)
                        .setAction("Scan", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IntentFilter filter = new IntentFilter();
                                filter.addAction(BluetoothDevice.ACTION_FOUND);
                                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                                registerReceiver(mReceiver, filter);
                                adapter.startDiscovery();
                            }
                        }).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
