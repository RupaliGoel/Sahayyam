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

        /*imageLoader.loadImage(emergency_list.get(position).Emergency_Image, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap
                System.out.print("LOADED IMAGE : "+loadedImage);


            }
        });*/

        /*ImageSize targetSize = new ImageSize(100, 100); // result Bitmap will be fit to this size
        imageLoader.loadImage(emergency_list.get(position).Emergency_Name, targetSize, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap
                System.out.println("\nBITMAP : "+loadedImage);
                setImage(loadedImage);
            }
        });*/

        if(!emergency_list.get(position).Emergency_Image.equals(""))
            imageLoader.displayImage(emergency_list.get(position).Emergency_Image, viewItem.EmerImageView);

//        imageLoader.destroy();

        //viewItem.EmerImageView.setImageResource(emergency_list.get(position).Emergency_Image);

        viewItem.EmerDistanceView.setText(String.valueOf((int)Math.round(emergency_list.get(position).Emergency_Distance))+" KM");

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
}

