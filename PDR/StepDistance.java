package com.example.rlakkh.pdr;

/**
 * Created by RLAKKH on 2018-04-27.
 */

public class StepDistance {
    private float[] accmax = {1.0f,1.0f}; //최대 가속도 값
    private float accmin = 1.0f; //최소 가속도 값
    private float[] thmax = {1.1f,1.1f}; //최대임계값
    private float thmin = 0.93f; //최소임계값
    private int stepst = 0; //step시작
    private int stepst1 = 0; //step시작
    private int stepst2 = 0; //step시작
    private float steplen = 0.0f; //보폭길이
    private float height = 1.73f;
    private float alpha = 1.73f*0.37f; //보폭추정 상수 alpha*sqrt(sqrt(accmax-accmin))
    private int index = 1;
    //private float dist_prev = 0.0f;
    //MovAvgFilter movAvgFilter=new MovAvgFilter();
    Kalmanfilter kalmanfilter=new Kalmanfilter();

    StepDistance() {

    }
    //가속도의 크기를 계산
    public float AccMagnitude(float x, float y, float z, float dt) {
        float tmp;
        float acc = (float)Math.sqrt(x*x+y*y+z*z);
        //tmp=movAvgFilter.Average(acc/9.8f);
        tmp = 1.0f+2.5f*kalmanfilter.filter((acc/9.8f)-1.0f,dt);
        return tmp;
    }

    public float StepDistance(float[] acc) {
        steplen=0.0f;
        if (acc[0] > thmax[0]) { //step 기준값과 비교
            stepst = 1;
        }

        if (stepst == 1) { //step이 시작함
            if (((acc[1] - acc[2]) * (acc[0] - acc[1]) < 0) & acc[1] >= thmax[0]) {
                //최대 가속도 검출
                if (accmax[0] < acc[1]) {
                    accmax[0] = acc[1];
                }
            }
            if (((acc[1] - acc[2]) * (acc[0] - acc[1]) < 0) & acc[1] <= thmin) {
                //최소 가속도 검출
                if (accmin > acc[1]) {
                    accmin = acc[1];
                }
                stepst2=1;
                if (index<3){
                    index+=1;
                    thmax[1]=thmax[0];
                    thmax[0]=1+0.5f*(accmax[0]-1.0f);
                }
                else{
                    thmax[1]=thmax[0];
                    thmax[0]=1.0f+0.5f*((0.5f*(accmax[0]+accmax[1]))-1.0f);
                }
            }
            if (acc[1]<1.0f&&acc[0]>1.0f&&stepst2==1){
                stepst1=1;
            }
            if (stepst==1&&stepst1==1){
                steplen=alpha*(float)Math.sqrt(Math.sqrt(accmax[0]-accmin));//보폭계산
                accmax[1]=accmax[0];
                accmax[0]=0;
                accmin=1;
                stepst=0;
                stepst1=0;
                stepst2=0;
            }
        }

        if(steplen!=0.0f) {
            return steplen;
        }
        else{
            return 0.0f;
        }
    }
}