package com.example.rupali.sos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DirectoryListAdapter extends BaseAdapter
{
    Context context;

    List<User> user_list;

    public DirectoryListAdapter(List<User> listValue, Context context)
    {
        this.context = context;
        this.user_list = listValue;
    }

    @Override
    public int getCount()
    {
        return this.user_list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.user_list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DirectoryViewItem viewItem = null;
        if(convertView == null)
        {
            viewItem = new DirectoryViewItem();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_list_layout, null);

            viewItem.DirectorylImageView = convertView.findViewById(R.id.ImageView);
            viewItem.DirectoryTextView = convertView.findViewById(R.id.TextView);
            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (DirectoryViewItem) convertView.getTag();
        }

        viewItem.DirectoryTextView.setText(user_list.get(position).User_Name);

        viewItem.DirectorylImageView.setImageResource(user_list.get(position).User_Image);

        return convertView;
    }
}

class DirectoryViewItem
{
    TextView DirectoryTextView;
    ImageView DirectorylImageView;
}


