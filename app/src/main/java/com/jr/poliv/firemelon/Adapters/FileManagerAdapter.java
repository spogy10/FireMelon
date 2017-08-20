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
import com.jr.poliv.firemelon.VoiceNoteFragment;
import com.jr.poliv.firemelon.R;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;


/**
 * Created by poliv on 8/6/2017.
 */

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ViewHolder> {

    private FileAdapter.CardViewListener cvListener;
    private ArrayList<File> filesArrayList;


    public FileManagerAdapter(Context context, ArrayList<File> filesArrayList) {
        this.filesArrayList = filesArrayList;
        VoiceNoteFragment.verifyStoragePermissions((Activity) context);
        try{
            cvListener = (FileAdapter.CardViewListener) context;
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
            if(file.isFile())
                Picasso.with(itemView.getContext()).load(file).placeholder(R.mipmap.dragon_fruit).error(R.mipmap.dragon_fruit).into(iv);
            else {
                Picasso.with(itemView.getContext()).load(R.mipmap.fire_melon).into(iv);
                final int finalPosition = position;
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvListener.cardViewListener(finalPosition);
                    }
                });
            }
            tv.setText(file.getName());
        }
    }




}
