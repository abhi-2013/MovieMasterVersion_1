package com.example.abhishek.moviemaster2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ABHISHEK on 26-03-2016.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private String[] items;
    public  GridViewAdapter(Context context,String[] items){
        super();
        this.context=context;
        this.items=items;
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView img;
        if(convertView==null){
            img=new ImageView(context);
            convertView=img;
            img.setPadding(2,2,2,2);
        }else {
            img=(ImageView) convertView;
        }
        Picasso.with(context)
                .load(items[position])
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.failed)
                .resize(110,160)
                .into(img);
        return  convertView;
    }
}
