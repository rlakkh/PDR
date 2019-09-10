package com.example.rlakkh.pdr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import static com.example.rlakkh.pdr.MyView.interset;

public class MainActivity extends AppCompatActivity {
    String mapname;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://projectpdr-617a3.appspot.com/");
    StorageReference imageRef;
    File localFile;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    SpeechRecognizer mRecognizer;
    private final int MY_PERMISSION_RECORD_AUDIO = 1;
    public float[] angle = new float[3];
    float time;
    float[] pixelPosition = {0.0f,0.0f};
    float[] acceldata=new float[3];
    float[] gyrodata=new float[3];
    float[] magnetdata=new float[3];
    float[] avg = {0.0f,0.0f,0.0f};
    float[] var = {0.0f,0.0f,0.0f};
    float[] accavg = {0.0f,0.0f,0.0f};
    float[] accvar = {0.0f,0.0f,0.0f};

    String offsetFileName;
    StringBuffer stringBuffer;
    StringBuffer stringBufferTmp;
    //String str;
    MyView myView;
    //ImageView mapView;
    TextView myLabel;
    TextView dataLabel;

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;

    Thread workerThread;
    Handler mHandler;
    ArrayList list = new ArrayList();

    byte[] readBuffer;
    int readBufferPosition;
    int counter=0;
    volatile boolean stopWorker;
    boolean check=false;
    boolean offsetsave = false;
    boolean loadcheck = false;

    float offsetAX = 0.0f;
    float offsetAY = 0.0f;
    float offsetAZ = 9.8066f;

    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    AvgFilter avgFilter;
    EulerAngle eulerAngle;
    ApplicationClass applicationClass;
    Uri imguri;

    Canvas cvs;
    Paint pnt;
    Bitmap bitmap;

    Bitmap map;
    static float display;
    static boolean isSetPos = false;
    Point size;

    float posx=0;
    float posy=0;
    float[] tmpposx = {0.0f, 0.0f};
    float[] tmpposy = {0.0f, 0.0f};

    String building = "hansung";
    String[] floor = new String[200];
    static String name;

    StepDistance stepDistance;
    float[] acc = {1.0f,1.0f,1.0f};
    float length=0;

    Button btnoffset;
    Button btnSetPos;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.offset:
                if (offsetsave) {
                    makeOffsetFile(offsetFileName+".txt");
                    item.setTitle("옵셋설정");
                    offsetsave = false;
                } else {
                    item.setTitle("옵셋저장");
                    offsetsave = true;
                }
                return true;
            case R.id.init_pos:
                myView.setPosition();
                myView.invalidate();
                pixelPosition = myView.getPixelPosition();
                item.setTitle("위치설정");
                isSetPos = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        floor[0] = "F3";

        Display displ = getWindowManager().getDefaultDisplay();
        size = new Point();
        displ.getSize(size);
        display = (1080.0f) / ((float) size.x);

        mHandler = new Handler();
        eulerAngle = new EulerAngle();
        stepDistance = new StepDistance();
        avgFilter = new AvgFilter();

        applicationClass = (ApplicationClass) getApplication();

        Button pairingButton = (Button) findViewById(R.id.pairing);
        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        Button btnVoice = (Button) findViewById(R.id.btnVoice);
        btnSetPos = (Button) findViewById(R.id.btnSetPos);

        btnSetPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSetPos) {
                    myView.setPosition();
                    myView.invalidate();
                    btnSetPos.setText("위치설정");
                    isSetPos = false;
                } else {
                    btnSetPos.setText("확인");
                    isSetPos = true;
                }
            }
        });

        btnoffset = (Button) findViewById(R.id.btnoffset);
        btnoffset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (offsetsave) {
                    makeOffsetFile(offsetFileName+".txt");
                    btnoffset.setText("옵셋설정");
                    offsetsave = false;
                } else {
                    btnoffset.setText("옵셋저장");
                    offsetsave = true;
                }
            }
        });

        dataLabel = (TextView) findViewById(R.id.datalabel);
        myLabel = (TextView) findViewById(R.id.label);
        myView = (MyView) findViewById(R.id.my_view);

        databaseReference.child("Map").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                building = dataSnapshot.child("building").getValue().toString();
                floor[0] = dataSnapshot.child("floor").getValue().toString();
                mapname = dataSnapshot.child("mapname").getValue().toString();
                interset = Float.parseFloat(dataSnapshot.child("scale").getValue().toString());
                imageRef = storageRef.child(mapname);

                localFile = new File(getFileStorageDir("/Maps"), "map.png");

                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        map = readImageFile("map.png");
                        myView.getBitmap(map);
                        myView.invalidate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"load fail!",Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        final Intent intent_Voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent_Voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent_Voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);

        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 30);
        }

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(intent_Voice);
            }
        });

        pairingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter.isEnabled()) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        int i = 0;
                        for (BluetoothDevice device : pairedDevices) {
                            list.add(i, "Name: " + device.getName() + "\nAddress: " + device.getAddress());
                            i++;
                        }
                        applicationClass.setList(list);
                        myLabel.setText("ok");

                    }
                    check = true;
                    Intent intent = new Intent(MainActivity.this, Sub1Activity.class);
                    startActivity(intent);
                }
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offsetFileName = applicationClass.getName();
                mmDevice = mBluetoothAdapter.getRemoteDevice(applicationClass.getAddress());
                if (check) {
                    try {
                        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                        mmSocket.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                stringBuffer = new StringBuffer();
                stringBufferTmp = new StringBuffer();

                try {
                    mmInputStream = mmSocket.getInputStream();
                    mmOutputStream = mmSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                posx = 0;
                posy = 0;
                recvData();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mmSocket.isConnected()) {
                    workerThread.interrupt();
                    try{
                        mmOutputStream.close();
                        mmInputStream.close();
                        mmSocket.close();
                    }catch (IOException e){e.printStackTrace();}
                }
                saveSensorData();
            }
        });
    }
    float vely = 0.0f;
    float[] tmply = {0.0f,0.0f,0.0f};
    public void recvData() {
        final Handler handler = new Handler();
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            final byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);

                            String tmp = new String(packetBytes, "UTF-8");
                            stringBuffer.append(tmp);

                            handler.post(new Runnable() {
                                public void run() {
                                    while(stringBuffer.length()>=71) {
                                        int type;
                                        String strdata = stringBuffer.substring(0, stringBuffer.indexOf("\n") - 1).toString();
                                        stringBuffer.delete(0, stringBuffer.indexOf("\n") + 1);

                                        if (strdata.length() == 69) {
                                            strdata = strdata.replaceAll("( +)", " ").trim();
                                            String[] data = strdata.split(" ");
                                            type = 0;
                                            for (String datatmp : data) {
                                                if (type == 0) {
                                                    time = Float.parseFloat(datatmp);
                                                } else if (type < 4) {
                                                    acceldata[type - 1] = Float.parseFloat(datatmp);
                                                } else if (type < 7) {
                                                    gyrodata[type - 4] = Float.parseFloat(datatmp);
                                                } else if (type < 10) {
                                                    magnetdata[type - 7] = Float.parseFloat(datatmp);
                                                }
                                                else {
                                                    type = 0;
                                                }
                                                type++;
                                            }


                                            float tmp;
                                            tmp = acceldata[1];
                                            acceldata[1] = acceldata[0];
                                            acceldata[0] = -tmp;
                                            tmp = gyrodata[1];
                                            gyrodata[1] = gyrodata[0];
                                            gyrodata[0] = -tmp;
                                            tmp = magnetdata[1];
                                            magnetdata[1] = magnetdata[0];
                                            magnetdata[0] = -tmp;

                                            if(offsetsave) {
                                                gyrodata[0] = (gyrodata[0] / 32.768f) * (float) Math.PI / 180;
                                                gyrodata[1] = (gyrodata[1] / 32.768f) * (float) Math.PI / 180;
                                                gyrodata[2] = (gyrodata[2] / 32.768f) * (float) Math.PI / 180;
                                                acceldata[0] = 9.8066f * (acceldata[0]) / 16384;
                                                acceldata[1] = 9.8066f * (acceldata[1]) / 16384;
                                                acceldata[2] = 9.8066f * (acceldata[2]) / 16384;

                                                avgFilter.setValue(gyrodata, acceldata);
                                                accavg = avgFilter.getAccAvg();
                                                accvar = avgFilter.getAccVar();
                                                avg = avgFilter.getAvg();
                                                var = avgFilter.getVar();
                                            }else {
                                                if(!loadcheck) {
                                                    loadOffsetFile(offsetFileName+".txt");
                                                    loadcheck =true;
                                                }
                                                acceldata[0] = 9.8066f * (acceldata[0]) / 16384 - offsetAX;
                                                acceldata[1] = 9.8066f * (acceldata[1]) / 16384 - offsetAY;
                                                acceldata[2] = 9.8066f * (acceldata[2]) / 16384 - offsetAZ + 9.8066f;

                                                gyrodata[0] = (gyrodata[0] / 32.768f) * (float) Math.PI / 180 - avg[0];
                                                gyrodata[1] = (gyrodata[1] / 32.768f) * (float) Math.PI / 180 - avg[1];
                                                gyrodata[2] = (gyrodata[2] / 32.768f) * (float) Math.PI / 180 - avg[2];
                                                stringBufferTmp.append(String.format("%f %f %f %f %f %f %f %f %f %f\r\n",
                                                        time, acceldata[0], acceldata[1], acceldata[2],
                                                        gyrodata[0], gyrodata[1], gyrodata[2],
                                                        magnetdata[0], magnetdata[1], magnetdata[2]
                                                ));

                                                angle = eulerAngle.EulerEKF(eulerAngle.EulerAccel(acceldata), gyrodata, 0.008333f);
                                                acc[2] = acc[1];
                                                acc[1] = acc[0];
                                                acc[0] = stepDistance.AccMagnitude(acceldata[0], acceldata[1], acceldata[2], 0.008333f);
                                                length = stepDistance.StepDistance(acc);

                                                posx += length * Math.sin(Math.toRadians(angle[2]));
                                                posy += length * Math.cos(Math.toRadians(angle[2]));
                                                tmpposx[0] = posx;
                                                tmpposy[0] = posy;
                                            }
                                        }
                                    }
                                    if(offsetsave){
                                        myLabel.setText(String.format("%f %f %f\n%f %f %f %f %f %f"
                                                ,accavg[0],accavg[1],accavg[2]
                                                ,avg[0],var[0],avg[1],var[1],avg[2],var[2]));
                                    }else{
                                        myLabel.setText(String.format("방향 : %.2f \n위치 : %.2f %.2f",angle[2],posx,posy));
                                    }

                                    if(tmpposx[0]!=tmpposx[1]||tmpposy[0]!=tmpposy[1]){
                                        tmpposx[1] = posx;
                                        tmpposy[1] = posy;

                                        myView.getPosition(posx,posy);
                                        pixelPosition = myView.getPixelPosition();
                                        myView.invalidate();
                                        SendData sendData = new SendData(posx,posy,getTimeStr("HH시 mm분 ss초"),"",pixelPosition[0],pixelPosition[1]);
                                        databaseReference.child("Maps").child("Location").child(getTimeStr("yyyy-MM-dd")).child(building).child(floor[0]).child(name).push().setValue(sendData);
                                    }
                                }
                            });
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    public float[] gravityCompensate(float[] mAccelData, float pitch, float roll){
        float g = 9.8066f;
        float[] tmp = {0.0f,0.0f,0.0f};

        tmp[0] = (float)(mAccelData[0] - g*Math.sin(pitch));
        tmp[1] = (float)(mAccelData[1] + g*Math.sin(roll)*Math.cos(pitch));
        tmp[2] = (float)(mAccelData[2] - g*Math.cos(roll)*Math.cos(pitch));

        return tmp;
    }

    public void saveSensorData() {
        makeDataFile();
        Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            Toast.makeText(getApplicationContext(),"정확히 말씀해 주세요",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            SendData sendData = new SendData(posx,posy,getTimeStr("HH시 mm분 ss초"),rs[0],pixelPosition[0],pixelPosition[1]);
            databaseReference.child("Maps").child("Location").child(getTimeStr("yyyy-MM-dd")).child(building).child(floor[0]).child(name).push().setValue(sendData);
            Toast.makeText(getApplicationContext(),rs[0],Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    public Bitmap readImageFile(String imagefilename) {
        Bitmap myBitmap;

        File filePath = getFileStorageDir("/Maps");
        File file = new File(filePath, imagefilename);

        if(file.exists()){
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return myBitmap;
        }
        else{
            return null;
        }
    }


    public void makeDataFile(){
        try{
            File filePath=getFileStorageDir("/SensorDataFiles");
            File file = new File(filePath,"SensorData"+getTimeStr("yyyy-MM-dd_HH:mm:ss")+".txt");
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter out=new PrintWriter(fileWriter);
            out.print(stringBufferTmp);
            out.close();
        }catch (Exception e){e.printStackTrace();}
    }

    public void loadOffsetFile(String datafilename){
        try {
            File filePath=getFileStorageDir("/SensorOffsetFiles");
            File file = new File(filePath, datafilename);
            FileReader fr = new FileReader(file) ;
            BufferedReader br = new BufferedReader(fr);
            String readStr = "";
            String str = null;
            while(((str = br.readLine()) != null)){
                readStr += str +"\n";
            }
            br.close();
            String[] datatmp = readStr.split(" ");
            offsetAX = Float.parseFloat(datatmp[0]);
            offsetAY = Float.parseFloat(datatmp[1]);
            offsetAZ = Float.parseFloat(datatmp[2]);
            avg[0] = Float.parseFloat(datatmp[3]);
            var[0] = Float.parseFloat(datatmp[4]);
            avg[1] = Float.parseFloat(datatmp[5]);
            var[1] = Float.parseFloat(datatmp[6]);
            avg[2] = Float.parseFloat(datatmp[7]);
            var[2] = Float.parseFloat(datatmp[8]);
            fr.close() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public void makeOffsetFile(String datafilename){
        float[] avg = avgFilter.getAvg();
        float[] var = avgFilter.getVar();

        try{
            File filePath=getFileStorageDir("/SensorOffsetFiles");
            File file = new File(filePath,datafilename);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter out=new PrintWriter(fileWriter);
            out.print(String.format("%f %f %f %f %f %f %f %f %f",
                    accavg[0], accavg[1], accavg[2],
                    avg[0],var[0],avg[1],var[1],avg[2],var[2]));
            out.close();
        }catch (Exception e){e.printStackTrace();}
    }

    public String getTimeStr(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(str);
        return dateFormat.format(new Date());
    }

    public File getFileStorageDir(String str) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + str);
        if (!file.mkdirs()) {
            Toast.makeText(this, "Directory not created", Toast.LENGTH_SHORT);
        }
        return file;
    }
}