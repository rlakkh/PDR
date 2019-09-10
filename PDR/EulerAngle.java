package com.example.rlakkh.pdr;
import java.util.Vector;

/**
 * Created by RLAKKH on 2018-03-29.
 */

public class EulerAngle {
    float prevX, prevY, prevZ;
    float[] tmplpf = new float[3];
    float[][] tmpaccangle = new float[2][1];

    float[][] H = {{1, 0.0f, 0.0f}, {0.0f, 1, 0.0f}};//=new float[2][3];
    float[][] Q = {{0.0001f, 0.0f, 0.0f}, {0.0f, 0.0001f, 0.0f}, {0.0f, 0.0f, 0.1f}};//=new float[3][3];
    float[][] R = {{10.0f, 0.0f}, {0.0f, 10.0f}};//=new float[2][2];
    float[][] x = {{0.0f}, {0.0f}, {0.0f}};//=new float[3][1];
    float[][] P = {{5.0f, 0.0f, 0.0f}, {0.0f, 5.0f, 0.0f}, {0.0f, 0.0f, 5.0f}};//=new float[3][3];

    Mat matrix = new Mat();

    EulerAngle() {

    }

    public float[] LPF(float x, float y, float z) {
        float alpha = 0.5f;

        tmplpf[0] = alpha * prevX + (1 - alpha) * x;
        tmplpf[1] = alpha * prevY + (1 - alpha) * y;
        tmplpf[2] = alpha * prevZ + (1 - alpha) * z;

        prevX = tmplpf[0];
        prevY = tmplpf[1];
        prevZ = tmplpf[2];
        return tmplpf;
    }

    public float[][] EulerAccel(float[] mAcc) {
        float g = 9.8066f;
        float[] tmp = {0.0f,0.0f};
        float[][] tmpaccangle = {{0.0f},{0.0f}};

        if(Math.abs(mAcc[0] / g)<1.0f){
            tmpaccangle[1][0] = (float) -Math.asin(mAcc[0] / g);
        }
        else if(mAcc[0] / g>0){
            tmpaccangle[1][0] = (float)Math.PI/2.0f;
        }
        else{
            tmpaccangle[1][0] = (float)Math.PI/2.0f;
        }

        if(Math.abs(mAcc[1]/(g*Math.cos(tmpaccangle[1][0])))<1.0f){
            tmpaccangle[0][0] = (float) Math.asin(mAcc[1]/(g*Math.cos(tmpaccangle[1][0])));
        }
        else if(mAcc[1]/(g*Math.cos(tmpaccangle[1][0]))>0){
            tmpaccangle[0][0] = (float)Math.PI/2.0f;
        }
        else{
            tmpaccangle[0][0] = -(float)Math.PI/2.0f;
        }


        return tmpaccangle;
    }


    public float[] EulerEKF(float[][] z, float[] rates, float dt) {
        float[] tmp = new float[3];
        float[][] A = Ajacob(x, rates, dt);

        float[][] xp = fx(x, rates, dt);//추정값을 예측
        float[][] Pp = matrix.Plus(matrix.Multim(matrix.Multim(A, P), matrix.Transpose(A)), Q); //오차 공분산 예측

        float[][] K = matrix.Multim(matrix.Multim(Pp, matrix.Transpose(H)), matrix.Inv(matrix.Plus(matrix.Multim(matrix.Multim(H, Pp), matrix.Transpose(H)), R)));//칼만 이득 계산
        x = matrix.Plus(xp, matrix.Multim(K, (matrix.Minus(z, matrix.Multim(H, xp)))));//추정값을 계산
        P = matrix.Minus(Pp, matrix.Multim(matrix.Multim(K, H), Pp));//오차 공분산 계산

        float phi = x[0][0];
        float theta = x[1][0];
        float psi = x[2][0];
        if (psi < 0.0f) {
            psi += 2.0f * 3.14159265f;
        }
        if (psi >= 2.0f * 3.14159265f) {
            psi -= 2.0f * 3.14159265f;
        }
        tmp[0] = (float) Math.toDegrees(phi);
        tmp[1] = (float) Math.toDegrees(theta);
        tmp[2] = (float) Math.toDegrees(psi);

        return tmp;
    }

    public float[][] fx(float[][] xhat, float[] rates, float dt) {
        float phi = xhat[0][0];
        float theta = xhat[1][0];

        if (Math.abs(theta) > 89.5f) {
            theta = 89.5f;
        }
        if (Math.abs(phi) > 89.5f) {
            phi = 89.5f;
        }

        float p = rates[0];
        float q = rates[1];
        float r = rates[2];
        float[][] xdot = new float[3][1];

        xdot[0][0] = (float) (p + q * Math.sin(phi) * Math.tan(theta) + r * Math.cos(phi) * Math.tan(theta));
        xdot[1][0] = (float) (q * Math.cos(phi) - r * Math.sin(phi));
        xdot[2][0] = (float) (q * Math.sin(phi) / Math.cos(theta) + r * Math.cos(phi) / Math.cos(theta));

        return matrix.Plus(xhat, matrix.Multik(xdot, dt));
    }

    public float[][] Ajacob(float[][] xhat, float[] rates, float dt) {
        float[][] A = new float[3][3];
        //
        float phi = xhat[0][0];
        float theta = xhat[1][0];

        if (Math.abs(theta) > 89.5f) {
            theta = 89.5f;
        }
        if (Math.abs(phi) > 89.5f) {
            phi = 89.5f;
        }

        float p = rates[0];
        float q = rates[1];
        float r = rates[2];

        A[0][0] = (float) (q * Math.cos(phi) * Math.tan(theta) - r * Math.sin(phi) * Math.tan(theta));
        A[0][1] = (float) (q * Math.sin(phi) / Math.cos(theta) / Math.cos(theta) + r * Math.cos(phi) / Math.cos(theta) / Math.cos(theta));
        A[0][2] = 0.0f;

        A[1][0] = (float) (-q * Math.sin(phi) - r * Math.cos(phi));
        A[1][1] = 0.0f;
        A[1][2] = 0.0f;

        A[2][0] = (float) (q * Math.cos(phi) / Math.cos(theta) - r * Math.sin(phi) / Math.cos(theta));
        A[2][1] = (float) (q * Math.sin(phi) / Math.cos(theta) * Math.tan(theta) + r * Math.cos(phi) / Math.cos(theta) * Math.tan(theta));
        A[2][2] = 0.0f;

        float[][] tmp = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    tmp[i][j] = 1.0f;
                } else {
                    tmp[i][j] = 0.0f;
                }
            }
        }
        A = matrix.Plus(tmp, matrix.Multik(A, dt));
        return A;
    }
}