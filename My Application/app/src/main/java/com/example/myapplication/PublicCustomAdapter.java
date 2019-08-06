package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class PublicCustomAdapter extends BaseAdapter {
    private ArrayList<String> times=new ArrayList<>();
    private ArrayList<String> status=new ArrayList<>();
    LayoutInflater linf;
    public PublicCustomAdapter(Context c, ArrayList<String> times, ArrayList<String> status) {
        this.times=times;
        this.status=status;
        linf=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int position) {
        return times.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v=linf.inflate(R.layout.custom_list,null);
        TextView time=(TextView)v.findViewById(R.id.time);
        Switch switch1=v.findViewById(R.id.switch1);
        time.setText(times.get(position));
        if(status.get(position).equals("true")){
            switch1.setChecked(true);
        }
        else{
            switch1.setChecked(false);
        }
        return v;
    }
}
