package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-05-01.
 */
public class AvgFilter {
    float k = 0;
    float[][] accavg = {{0.0f,0.0f,0.0f},{0.0f,0.0f,0.0f}};
    float[][] accvar = {{0.0f,0.0f,0.0f},{0.0f,0.0f,0.0f}};
    float[][] avgvec = {{0.0f,0.0f,0.0f},{0.0f,0.0f,0.0f}};
    float[][] varvec = {{0.0f,0.0f,0.0f},{0.0f,0.0f,0.0f}};
    float[] gyro = {0.0f,0.0f,0.0f};
    float[] accel = {0.0f,0.0f,0.0f};

    AvgFilter(){}

    public void setValue(float[] value,float[] acc){
        for(int i=0;i<3;i++) {
            accel[i] = acc[i];
            gyro[i] = value[i];
        }
        k++;

        for(int i=0;i<3;i++) {
            accavg[0][i] = 1 / k * accel[i] + (k - 1) / k * accavg[1][i];
            accvar[0][i] = 1 / k * (float) Math.pow((accel[i] - avgvec[0][i]), 2) + (k - 1) / k * varvec[0][i];
            avgvec[0][i] = 1 / k * gyro[i] + (k - 1) / k * avgvec[1][i];
            varvec[0][i] = 1 / k * (float) Math.pow((gyro[i] - avgvec[0][i]), 2) + (k - 1) / k * varvec[0][i];
        }
        for(int i=0;i<3;i++){
            accavg[1][i]=accavg[0][i];
            accvar[1][i]=accvar[0][i];
            avgvec[1][i] = avgvec[0][i];
            varvec[1][i] = varvec[0][i];
        }
    }

    public float[] getAvg(){
        float[] tmp = {0.0f,0.0f,0.0f};
        for(int i=0;i<3;i++){
            tmp[i] = avgvec[0][i];
        }
        return tmp;
    }

    public float[] getVar(){
        float[] tmp = {0.0f,0.0f,0.0f};

        for(int i=0;i<3;i++){
            tmp[i] = varvec[0][i];
        }
        return tmp;
    }

    public float[] getAccAvg() {
        float[] tmp = {0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            tmp[i] = accavg[0][i];
        }
        return tmp;
    }

    public float[] getAccVar(){
        float[] tmp = {0.0f,0.0f,0.0f};

        for(int i=0;i<3;i++){
            tmp[i] = accvar[0][i];
        }
        return tmp;
    }
}