package com.example.rupali.sos;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmergencyListAdapter extends BaseAdapter
{
    Context context;

    List<Emergency> emergency_list;

    public EmergencyListAdapter(List<Emergency> listValue, Context context)
    {
        this.context = context;
        this.emergency_list = listValue;
    }

    @Override
    public int getCount()
    {
        return this.emergency_list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.emergency_list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem viewItem = null;
        if(convertView == null)
        {
            viewItem = new ViewItem();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_list_layout, null);

            viewItem.EmerImageView = (ImageView)convertView.findViewById(R.id.ImageView);
            viewItem.EmerNameTextView = convertView.findViewById(R.id.TextView);
            viewItem.EmerDistanceView = convertView.findViewById(R.id.DistanceTextView);
            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }

        viewItem.EmerNameTextView.setText(emergency_list.get(position).Emergency_Name);

        viewItem.EmerImageView.setImageResource(emergency_list.get(position).Emergency_Image);

        viewItem.EmerDistanceView.setText(String.valueOf((int)Math.round(emergency_list.get(position).Emergency_Distance))+" KM");

        return convertView;
    }
}

class ViewItem
{
    TextView EmerNameTextView;
    ImageView EmerImageView;
    TextView EmerDistanceView;
}

