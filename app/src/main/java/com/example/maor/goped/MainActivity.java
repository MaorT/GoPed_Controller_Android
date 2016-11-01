package com.example.maor.goped;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Bluetooth BT;
    float currentDegreeTemp = 0,currentDegreeThrottle=0;
    ImageView imgNeedleTemp,imgNeedleThrottle;
    Button sendBtn;
    EditText txtInput;
    TextView BTinput,textViewThrottle,textViewTemp;
    ImageButton led_Off_ImgBtn,led_mode2_ImgBtn;


    private static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        imgNeedleTemp = (ImageView) findViewById(R.id.NeedleTempImgView);
        imgNeedleThrottle = (ImageView) findViewById(R.id.NeedleThrottleImgView);
        sendBtn = (Button)findViewById(R.id.BtnSendCmd);
        txtInput = (EditText)findViewById(R.id.txtCmd);
        BTinput = (TextView)findViewById(R.id.txtBTin);
        textViewThrottle = (TextView)findViewById(R.id.textViewThrottle);
        textViewTemp = (TextView)findViewById(R.id.textViewTemp);
        led_Off_ImgBtn = (ImageButton)findViewById(R.id.imgBtnLedOff);
        led_mode2_ImgBtn = (ImageButton)findViewById(R.id.imgBtnLedMode2);


        BT = Bluetooth.getInstance();

        if (address != null)
        {
            BT.Connect(address);
            if(!BT.isConnected())
            {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }


        Update_Bluetooth_Incomming_Func();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtInput.getText().toString().length()>0)
                {
                    BT.SendBT(txtInput.getText()+"~");
                }

            }
        });

        led_Off_ImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BT.SendBT("slave#led#off");
            }
        });

        led_mode2_ImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BT.SendBT("slave#led#m2");
            }
        });


        BT.SendBT("show~"); // start show gauges
    }


    public void Rotate_Temp_Needle(float degree) {

        // get the angle around the z-axis rotated

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegreeTemp,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // long durationCalcs = 100+(long)Math.abs(currentDegree-degree)*25;
        long durationCalcs = 10; // how long the animation will take place
        ra.setDuration(durationCalcs);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        imgNeedleTemp.startAnimation(ra);
        currentDegreeTemp = degree;

    }

    public void Rotate_Throttle_Needle(float degree) {

        // get the angle around the z-axis rotated

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegreeThrottle,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // long durationCalcs = 100+(long)Math.abs(currentDegree-degree)*25;
        long durationCalcs = 10; // how long the animation will take place
        ra.setDuration(durationCalcs);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        imgNeedleThrottle.startAnimation(ra);
        currentDegreeThrottle = degree;

    }

    public void showDegree(float degree)
    {
        float res = degree*2.25f;
        Rotate_Temp_Needle(res);
    }

    public void showThrottle(int t)
    {
        float res =  t*2.7f;
        Rotate_Throttle_Needle(res);
    }


    private void Update_Bluetooth_Incomming_Func()
    {
        BT.Set_BluetoothHandle_Function(new Runnable() { // set function to handle incoming BT meassage
            @Override
            public void run() {
                String txt = BT.GetLastMessage();
                if(txt.contains("#"))
                {
                    int hashIndex = txt.indexOf("#");
                    String command = txt.substring(0,hashIndex);
                    String value =  txt.substring(hashIndex+1);

                    if(command.compareTo("temp")==0 )
                    {
                        float deg = Float.valueOf(value);
                        textViewTemp.setText(deg + "C°");
                        showDegree(deg);
                    }
                    else if(command.compareTo("T")==0)
                    {
                        int x = Integer.valueOf(value);
                        textViewThrottle.setText(x +" %");
                        showThrottle(x);
                    }
                }
                else
                    BTinput.setText("BT response: "+txt);

               // sensorView0.setText(" Degrees: " + Math.round(Float.valueOf(txt)));	//update the textviews with sensor values
            }
        });

    }











}
