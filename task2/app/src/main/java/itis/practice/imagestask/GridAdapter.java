package itis.practice.imagestask;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import itis.practice.imagestask.utils.Constants;

/**
 * Created by ilya on 17.09.15.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    List<String> data = Constants.getImagesUrls();
    Activity mActivity;
    int mPictureSize;

    public GridAdapter(Activity activity, int pictureSize) {
        this.mActivity = activity;
        this.mPictureSize = pictureSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.grid_item, parent, false),
                mPictureSize);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mActivity)
                .load(data.get(position))
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivImage)
        ImageView mImageView;

        public ViewHolder(View itemView, int size) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
            params.width = params.height = size;
        }
    }
}
