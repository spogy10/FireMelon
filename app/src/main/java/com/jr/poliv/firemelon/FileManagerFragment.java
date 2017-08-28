package com.jr.poliv.firemelon;

import android.Manifest;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jr.poliv.firemelon.Adapters.FileAdapter;
import com.jr.poliv.firemelon.Adapters.FileManagerAdapter;
import com.jr.poliv.firemelon.AsyncTaskLoaders.FileAsyncTaskLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import static com.jr.poliv.firemelon.VoiceNoteFragment.getMimeType;


public class FileManagerFragment extends Fragment implements FileAdapter.CardViewListener, LoaderManager.LoaderCallbacks<ArrayList> {

    public FileManagerFragment(){
    }

    RecyclerView recycler;
    FileManagerAdapter adapter;
    Stack<File> backTrace = new Stack<>();
    ArrayList<File> filesArrayList = new ArrayList<>();
    String file_path = File.separator+"storage"+File.separator;
    private File directory = new File(file_path);
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean havePermission = false;

    public static FileManagerFragment newInstance() {
        return new FileManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voicenote, container, false);
        permissionCheck();
        if(havePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("Paul", "external storage state " + Environment.getExternalStorageState());


            recycler = (RecyclerView) rootView.findViewById(R.id.rvFile);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            recycler.setLayoutManager(gridLayoutManager);
            adapter = new FileManagerAdapter(getActivity(), filesArrayList);
            recycler.setAdapter(adapter);

            getLoaderManager().initLoader(0, null, this);

        }else{
            Toast.makeText(getActivity(),"NO PERMISSION or MEDIA NOT MOUNTED", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_voicenote);
        permissionCheck();
        if(havePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("Paul", "external storage state " + Environment.getExternalStorageState());


            recycler = (RecyclerView) findViewById(R.id.rvFile);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recycler.setLayoutManager(gridLayoutManager);
            adapter = new FileManagerAdapter(this, filesArrayList);
            recycler.setAdapter(adapter);

            getLoaderManager().initLoader(0, null, this);

        }else{
            Toast.makeText(this,"NO PERMISSION or MEDIA NOT MOUNTED", Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void cardViewListener(int position) {
        Log.d("Paul", "Card Clicked - "+String.valueOf(position));
        File file = filesArrayList.get(position);
        if(file.isFile())
            clickFile(file);
        else
            clickDirectory(file);
    }

    private void clickDirectory(File tempFile){

        if(tempFile.listFiles() != null){
            backTrace.push(directory);
            directory = tempFile;
            getLoaderManager().restartLoader(0, null, this);
        }else if(tempFile.getAbsolutePath().equals(File.separator+"storage"+File.separator+"emulated")){
            backTrace.push(directory);
            directory = new File(File.separator+"storage"+File.separator+"emulated"+File.separator+"0");
            getLoaderManager().restartLoader(0, null, this);
        }else{
            Toast.makeText(getActivity(),"CANNOT GO TO SELECTED DIRECTORY", Toast.LENGTH_SHORT).show();
        }
    }

    private void clickFile(File file){
        String type = getMimeType(Uri.fromFile(file));
        type = (type != null && !type.equals("")) ? getGeneralType(type) : "";
        Log.d("Paul", type);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.fromFile(file));
        sendIntent.setType(type);
        try{
            startActivity(sendIntent);
        }catch (RuntimeException e){
            Log.d("Paul", "Error trying to send file "+e.toString());
            e.printStackTrace();
        }

    }

    private static String getGeneralType(String s){
        try{

            return s.substring(0, s.indexOf("/")+1) + "*";
        }catch(Exception e){
            Log.d("Paul", "Error trying to get general type "+e.toString());
            e.printStackTrace();
        }
        return "";
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && !backTrace.empty()) {
            directory = backTrace.pop();
            getLoaderManager().restartLoader(0, null, this);
            return true;
        }

        return false;
    }

    private void permissionCheck(){
        havePermission = VoiceNoteFragment.verifyStoragePermissions(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_EXTERNAL_STORAGE:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    havePermission = true;
                    getActivity().recreate();
                }else{
                    Log.d("Paul", "Permission denied");
                }
            }
        }
    }



    @Override
    public Loader<ArrayList> onCreateLoader(int id, Bundle args) {
        Loader loader = new FileAsyncTaskLoader(getActivity(), directory.listFiles(), FileAsyncTaskLoader.EVERYTHING_MODE);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
        filesArrayList.clear();
        filesArrayList.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {
        filesArrayList.clear();
    }
}
