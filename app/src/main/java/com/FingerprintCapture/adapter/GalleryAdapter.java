package com.FingerprintCapture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.FingerprintCapture.R;
import com.FingerprintCapture.models.response.UserFingerData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserFingerData> mScannedImages;
    private LayoutInflater mInflter;

    public GalleryAdapter(Context pContext, List<UserFingerData> pScannedImages) {
        this.mContext = pContext;
        this.mScannedImages = pScannedImages;
        mInflter = (LayoutInflater.from(pContext));
    }

    @Override
    public int getCount() {
        return mScannedImages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflter.inflate(R.layout.layout_gallery_images, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
        Glide.with(mContext)
                .load(mScannedImages.get(i).getThumbImage())
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .dontAnimate()
                .into(icon);
        return view;
    }
}
