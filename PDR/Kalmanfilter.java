package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-04-27.
 */

public class Kalmanfilter {
    float[][] H = {{0.0f, 1.0f}};
    float[][] Q = {{0.1f, 0.0f}, {0.0f, 0.1f}};
    float[][] R = {{10.0f}};
    float[][] x = {{1.0f}, {0.0f}};
    float[][] P = {{1.0f, 0.0f}, {0.0f, 1.0f}};
    float[][] A = {{1.0f, 0.0f}, {0.0f, 1.0f}};
    Mat matrix1 = new Mat();

    Kalmanfilter() {

    }

    public float filter(float data, float dt) {
        float[][] tmp = {{data}};
        A[0][1] = dt;
        float[][] xp = matrix1.Multim(A, x);
        float[][] Pp = matrix1.Plus(matrix1.Multim(matrix1.Multim(A, P), matrix1.Transpose(A)), Q);
        float[][] K = matrix1.Multik(matrix1.Multim(Pp, matrix1.Transpose(H)), matrix1.Inv(matrix1.Plus(matrix1.Multim(matrix1.Multim(H, Pp), matrix1.Transpose(H)), R), 1));
        x = matrix1.Plus(xp, matrix1.Multim(K, matrix1.Minus(tmp, matrix1.Multim(H, xp))));
        P = matrix1.Minus(Pp, matrix1.Multim(K, matrix1.Multim(H, Pp)));

        return x[1][0];
    }
}