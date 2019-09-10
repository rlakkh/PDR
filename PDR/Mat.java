package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-03-29.
 */

public class Mat {
    Mat() {

    }

    public float[][] Plus(float[][] m1, float[][] m2) {
        if ((m1.length == m2.length) & (m1[0].length == m2[0].length)) {

        } else {
            return null;
        }
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                m1[i][j] = m1[i][j] + m2[i][j];
            }
        }
        return m1;
    }

    public float[][] Minus(float[][] m1, float[][] m2) {
        if ((m1.length == m2.length) & (m1[0].length == m2[0].length)) {

        } else {
            return null;
        }
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                m1[i][j] = m1[i][j] - m2[i][j];
            }
        }
        return m1;
    }

    public float[][] Multim(float[][] m1, float[][] m2) {
        int row = 1, col = 1;
        row = m1.length;
        col = m2[0].length;
        float[][] result=new float[row][col];

        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m2[0].length; j++) {
                result[i][j] = 0.0f;
                for (int k = 0; k < m2.length; k++) {
                    result[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return result;
    }

    public float[][] Multik(float[][] m1, float k) {
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                m1[i][j] = m1[i][j] * k;
            }
        }
        return m1;
    }

    public float[][] Inv(float[][] m) {
        float tmp=0.0f;

        float det = 0.0f;
        if (m.length == m[0].length) {
            det = m[0][0] * m[1][1] - m[0][1] * m[1][0];
            tmp=m[0][0];
            m[0][0] = m[1][1];
            m[1][1] = tmp;
            m[0][1] = -m[0][1];
            m[1][0] = -m[1][0];
        }

        if (det != 0.0f) {
            m = Multik(m, 1.0f / det);
            return m;
        } else {
            float[][] e = {{1.0f, 0.0f}, {0.0f, 1.0f}};
            return e;
        }
    }
    public float Inv(float[][] m,int use) {
        if(m[0][0]==0){
            return 1.0f;
        }else{
            return (1.0f / m[0][0]);
        }
    }

    public float[][] Transpose(float[][] m) {
        float[][] result=new float[m[0].length][m.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                result[j][i] = m[i][j];
            }
        }
        return result;
    }
}