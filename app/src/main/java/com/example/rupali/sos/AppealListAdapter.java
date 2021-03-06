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

import com.squareup.picasso.Picasso;

public class AppealListAdapter extends BaseAdapter
{
    Context context;

    List<Appeal> appeal_list;

    public AppealListAdapter(List<Appeal> listValue, Context context)
    {
        this.context = context;
        this.appeal_list = listValue;
    }

    @Override
    public int getCount()
    {
        return this.appeal_list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.appeal_list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        AppealViewItem viewItem = null;
        if(convertView == null)
        {
            viewItem = new AppealViewItem();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_list_layout, null);

            viewItem.AppealImageView = convertView.findViewById(R.id.ImageView);
            viewItem.AppealTextView = convertView.findViewById(R.id.TextView);
            viewItem.AppealDescView = convertView.findViewById(R.id.TextView2);
            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (AppealViewItem) convertView.getTag();
        }

        viewItem.AppealTextView.setText(appeal_list.get(position).Appeal_Type);

        String image_url = appeal_list.get(position).Appeal_Image;

        if (image_url.isEmpty()) {
            viewItem.AppealImageView.setImageResource(R.drawable.whiteimageview);
        } else{
            Picasso.with(parent.getContext())
                    .load(image_url)
                    .noFade().into(viewItem.AppealImageView);
        }

        viewItem.AppealDescView.setText(appeal_list.get(position).Appeal_Desc);

        return convertView;
    }
}

class AppealViewItem
{
    TextView AppealTextView;
    ImageView AppealImageView;
    TextView AppealDescView;
}


