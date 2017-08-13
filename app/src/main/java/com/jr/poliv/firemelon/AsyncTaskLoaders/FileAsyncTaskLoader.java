package com.jr.poliv.firemelon.AsyncTaskLoaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by poliv on 8/6/2017.
 */

public class FileAsyncTaskLoader extends AsyncTaskLoader<ArrayList<File>> {
    
    private File[] files;
    private int mode;
    public static final int EVERYTHING_MODE = 1;
    public static final int FILES_ONLY_MODE = 0;
    public static final int DIRECTORY_ONLY_MODE = 2;


    public FileAsyncTaskLoader(Context context, File[] files) {
        super(context);
        this.files = files;
        mode = FILES_ONLY_MODE;
    }

    public FileAsyncTaskLoader(Context context, File[] files, int mode) {
        super(context);
        this.files = files;
        this.mode = mode;
    }

    @Override
    public ArrayList<File> loadInBackground() {

        switch(mode){
            case EVERYTHING_MODE: return getEverything();

            case DIRECTORY_ONLY_MODE: return getDirectories();

            case FILES_ONLY_MODE: default: return getFiles();
        }
    }

    private ArrayList<File> getFiles() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for(File i : files){
            if(i.isFile() && !i.getName().equals(".nomedia"))
                fileArrayList.add(i);
        }
        return fileArrayList;
    }

    private ArrayList<File> getEverything(){
        ArrayList<File> fileArrayList = new ArrayList<>(), tempArrayList = new ArrayList<>();

        for(File i : files){
            if(i.isDirectory())
                fileArrayList.add(i);
        }

        Collections.sort(fileArrayList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });



        for(File i : files){
            if(!i.isDirectory())
                tempArrayList.add(i);
        }

        Collections.sort(tempArrayList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        fileArrayList.addAll(tempArrayList);


        return fileArrayList;
    }

    private ArrayList<File> getDirectories() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for(File i : files){
            if(i.isDirectory())
                fileArrayList.add(i);
        }

        Collections.sort(fileArrayList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return fileArrayList;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
