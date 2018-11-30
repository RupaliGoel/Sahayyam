package com.example.rupali.sos;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

public class EmergencyListAdapter extends BaseAdapter
{
    Context context;
    ViewItem viewItem = null;
    ImageLoader imageLoader = ImageLoader.getInstance();


    List<Emergency> emergency_list;

    public EmergencyListAdapter(List<Emergency> listValue, Context context)
    {
        this.context = context;
        this.emergency_list = listValue;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context.getApplicationContext()));
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

        if(convertView == null)
        {
            viewItem = new ViewItem();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_list_layout, null);

            viewItem.EmerImageView = convertView.findViewById(R.id.ImageView);
            viewItem.EmerNameTextView = convertView.findViewById(R.id.TextView);
            viewItem.EmerDistanceView = convertView.findViewById(R.id.DistanceTextView);
            viewItem.EmerDescTextView = convertView.findViewById(R.id.TextView2);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }

        viewItem.EmerNameTextView.setText(emergency_list.get(position).Emergency_Id+". "+emergency_list.get(position).Emergency_Name);

        String image_url = emergency_list.get(position).Emergency_Image;
        System.out.println(image_url);

        if (image_url.isEmpty()) {
            viewItem.EmerImageView.setImageResource(R.drawable.whiteimageview);
        } else{
            Picasso.with(parent.getContext())
                    .load(image_url).resize(150,150)
                    .noFade().into(viewItem.EmerImageView);
        }

        viewItem.EmerDistanceView.setText(String.valueOf((int)Math.round(emergency_list.get(position).Emergency_Distance))+" KM");
        viewItem.EmerDescTextView.setText(emergency_list.get(position).Emergency_Address);
        return convertView;
    }

    void setImage(Bitmap bmp){
        viewItem.EmerImageView.setImageBitmap(bmp);
    }
}

class ViewItem
{
    TextView EmerNameTextView;
    ImageView EmerImageView;
    TextView EmerDistanceView;
    TextView EmerDescTextView;
}

