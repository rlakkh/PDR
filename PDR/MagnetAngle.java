package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-05-11.
 */

public class MagnetAngle {
    private float Yaw = 0.0f;

    public void calcMagAngle(float mx,float my,float mz, float[][] euleraccel) {
        float pitchA = -euleraccel[1][0];
        float rollA= -euleraccel[0][0];

        float Mx = (float) (mx*Math.cos(pitchA) - mz*Math.sin(pitchA));
        float My = (float) (mx*Math.sin(rollA)*Math.sin(pitchA) + my*Math.cos(rollA) + mz*Math.sin(rollA)*Math.cos(pitchA));

        Yaw = 90 - (float) (Math.atan2(My,Mx)*180/Math.PI);

        if (Yaw < 0.0f) {
            Yaw += 360.0f;
        }
    }

    public float getYaw(){
        return Yaw;
    }
}