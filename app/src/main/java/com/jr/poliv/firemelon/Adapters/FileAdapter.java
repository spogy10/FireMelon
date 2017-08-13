package com.jr.poliv.firemelon.Adapters;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jr.poliv.firemelon.MainActivity;
import com.jr.poliv.firemelon.R;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;


/**
 * Created by poliv on 8/6/2017.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private CardViewListener cvListener;
    private ArrayList<File> filesArrayList;


    public FileAdapter(Context context, ArrayList<File> filesArrayList) {
        this.filesArrayList = filesArrayList;
        MainActivity.verifyStoragePermissions((Activity) context);
        try{
            cvListener = (CardViewListener) context;
        }catch(ClassCastException e){
            e.printStackTrace();
            Log.d("Paul", context.getClass().getName()+" not an instance of CardViewListener. " + e.toString());
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindView(position);

    }

    @Override
    public int getItemCount() {

        return filesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView iv;
        TextView tv;

        private ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView;
            iv = (ImageView) itemView.findViewById(R.id.imageView);
            tv = (TextView) itemView.findViewById(R.id.textView);
        }

        private void  bindView(int position){
            File file = filesArrayList.get(position);
            Picasso.with(itemView.getContext()).load(R.mipmap.dragon_fruit).into(iv);
            tv.setText(file.getName());
            final int finalPosition = position;
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cvListener.cardViewListener(finalPosition);
                }
            });
        }
    }

    public interface CardViewListener{
        void cardViewListener(int position);
    }


}
