package xyz.mzc6838.qrscanner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public Context mContext;
    public List<ImgInfo> ImgInfoList;

    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    public ImageAdapter(Context context, List<ImgInfo> _ImgInfoList){
        ImgInfoList = _ImgInfoList;
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(View view){
            super(view);

            imageView = view.findViewById(R.id.image1);

        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImgInfo info = ImgInfoList.get(position);

        //Log.d("img info", "???");

        Glide.with(mContext).load(info.getImage_url()).into(holder.imageView);


        if(onItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onItemLongClickListener != null && onItemLongClickListener.onItemLongClick(holder.itemView, position);
                }
            });
        }

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return ImgInfoList.size();
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
