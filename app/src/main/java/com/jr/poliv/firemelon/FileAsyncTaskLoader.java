package com.jr.poliv.firemelon;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by poliv on 8/6/2017.
 */

public class FileAsyncTaskLoader extends AsyncTaskLoader<ArrayList<File>> {
    
    private File[] files;


    public FileAsyncTaskLoader(Context context, File[] files) {
        super(context);
        this.files = files;
    }

    @Override
    public ArrayList<File> loadInBackground() {
        return getFiles();
    }

    private ArrayList<File> getFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for(File i : files){
            if(i.isFile() && !i.getName().equals(".nomedia"))
                fileArrayList.add(i);
        }
        return fileArrayList;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
