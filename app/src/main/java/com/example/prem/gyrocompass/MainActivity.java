package com.example.prem.gyrocompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener,StepListener{
    SensorManager mSensorManager;
    SimpleStepDetector simpleStepDetector;
    int numSteps;
    TextView tx,txt1,st;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx=(TextView)findViewById(R.id.tx1);
        txt1=(TextView)findViewById(R.id.txt1);
        st=(TextView)findViewById(R.id.status);

        numSteps=0;
        simpleStepDetector=new SimpleStepDetector();
        simpleStepDetector.registerListener(this);

        ts = System.currentTimeMillis();
        gy=0.0;
        gy1=0.0;
    }
    void start(View view)
    {
        st.setText("**Recording**");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }
    void stop(View view)
    {
        st.setText("**Paused**");
        mSensorManager.unregisterListener(this);
    }
    void reset(View view)
    {
        deg=0.0;
        gy=0.0;
        numSteps=0;
        tx.setText("Rotation :0 Degrees");
        txt1.setText("Steps0");
    }
    @Override
    protected void onResume() {
        super.onResume();
    // for the system's orientation sensor registered listeners
    //    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
      //          SensorManager.SENSOR_DELAY_GAME);
    }
    void add(View view) throws IOException {
        numSteps++;
        txt1.setText("Steps"+numSteps);
        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/IMU");
        dir.mkdirs();
        File file = new File(dir, "IMUDATA.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(0.69+" " + deg+"\n");
        outputStreamWriter.close();
        fileOutputStream.close();

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    long ts ;
    double time;
    Double deg=0.0,gy,gy1;
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //double vel = (double) (event.values[2]) * 180 / Math.PI;
        double vel = (double) (event.values[1]) * 180 / Math.PI;
        Log.d("Gyro", Float.toString(event.values[0]));
        time = System.currentTimeMillis() - ts;
        ts = System.currentTimeMillis();
        time /= 1000;
        deg += time * (vel + gy) / 2;
        if (deg > 360)
            deg -= 360;
        if (deg < -360)
            deg += 360;
        gy = vel;
        //Toast.makeText(this, "Sensing"+Double.toString(vel), Toast.LENGTH_SHORT).show();
        tx.setText("Rotation: " + Math.round(deg) + " degrees");


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) throws IOException {
        numSteps++;
        txt1.setText("Steps"+numSteps);
        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/IMU");
        dir.mkdirs();
        File file = new File(dir, "IMUDATA.txt");

            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(0.69+" " + deg+"\n");
            outputStreamWriter.close();
            fileOutputStream.close();
    }
}
