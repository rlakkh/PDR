package com.example.rlakkh.pdr;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RLAKKH on 2018-03-21.
 */

public class SendData {
    public String time;
    public String axisx;
    public String axisy;
    public String speech;
    public String pixelX;
    public String pixelY;

    public SendData() {

    }

    public SendData(float posx,float posy,String time,String speech,
                    float pixelX,float pixelY) {
        this.axisx = String.format("%.2f",posx);
        this.axisy = String.format("%.2f",posy);
        this.time = time;
        this.speech = speech;
        this.pixelX = String.format("%f",pixelX);
        this.pixelY = String.format("%f",pixelY);
    }
}