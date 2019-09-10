package com.example.rlakkh.pdr;

import android.app.Application;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RLAKKH on 2018-04-06.
 */

public class ApplicationClass extends Application{
    private ArrayList list;
    private String address;
    private String name;

    public void setList(ArrayList list){
        this.list=list;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setName(String name){this.name = name;}

    public ArrayList getList(){
        return this.list;
    }

    public String getAddress(){
        return this.address;
    }

    public String getName(){return this.name;}
}
