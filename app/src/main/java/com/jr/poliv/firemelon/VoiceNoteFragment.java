package com.jr.poliv.firemelon;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.jr.poliv.firemelon.adapters.FileAdapter;
import com.jr.poliv.firemelon.asyncTaskLoaders.FileAsyncTaskLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VoiceNoteFragment extends Fragment implements FileAdapter.CardViewListener, LoaderManager.LoaderCallbacks<ArrayList> {

    public VoiceNoteFragment(){
    }

    RecyclerView recycler;
    FileAdapter adapter;
    ArrayList<File> filesArrayList = new ArrayList<>();
    String default_file_path = File.separator+"storage"+File.separator+"6C58-E107"+File.separator+"vn archive";
    File vnArchive;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean havePermission = false;

    public static VoiceNoteFragment newInstance(){
        return new VoiceNoteFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voicenote, container, false);
        vnArchive = getFilePathFromSharedPreference();
        permissionCheck();
        if(havePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("Paul", "external storage state " + Environment.getExternalStorageState());

            recycler = (RecyclerView) rootView.findViewById(R.id.rvFile);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            recycler.setLayoutManager(gridLayoutManager);
            adapter = new FileAdapter(getActivity(), filesArrayList);
            recycler.setAdapter(adapter);

            getLoaderManager().initLoader(0, null, this);

        }else{
            Toast.makeText(getActivity(),"NO PERMISSION or MEDIA NOT MOUNTED", Toast.LENGTH_LONG).show();

        }

        return rootView;
    }

    private File getFilePathFromSharedPreference() {
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);

        String file_path = preferences.getString(getString(R.string.voice_note_file_path), default_file_path);
        return new File(file_path);
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_voicenote);
        //Intent intent = new Intent(this, FileManagerFragment.class); startActivity(intent);
        permissionCheck();
        if(havePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("Paul", "external storage state " + Environment.getExternalStorageState());


            recycler = (RecyclerView) findViewById(R.id.rvFile);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recycler.setLayoutManager(gridLayoutManager);
            adapter = new FileAdapter(this, filesArrayList);
            recycler.setAdapter(adapter);

            getLoaderManager().initLoader(0, null, this);

        }else{
            Toast.makeText(this,"NO PERMISSION or MEDIA NOT MOUNTED", Toast.LENGTH_LONG).show();
        }

    }*/


    void sendToWhatsApp(File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        File newFile = new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.temp_file_name));
        FileOutputStream out = new FileOutputStream(newFile);
        byte[] readData = new byte[1024*500];
        int i = stream.read(readData);
        while( i != -1){
            out.write(readData, 0, i);
            i = stream.read(readData);
        }
        out.flush();
        out.close();
        stream.close();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("audio/mp3");
        intent.putExtra("FilePath", newFile.getAbsolutePath());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(newFile.getAbsolutePath()));
        intent.setPackage(getString(R.string.whatsapp_package_name));
        Log.d("Paul", getMimeType(Uri.fromFile(file)));
        startActivityForResult(intent,1);
        newFile.deleteOnExit();
    }

    @Override
    public void cardViewListener(int position) {
        Log.d("Paul", String.valueOf(position));
        try {
            sendToWhatsApp(filesArrayList.get(position));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getMimeType(Uri uri) {
        String mimeType;

            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());

        mimeType = (mimeType == null)? "":mimeType;

        return mimeType;
    }

    private void permissionCheck(){
        havePermission = verifyStoragePermissions(getActivity());
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;

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
        Loader loader = new FileAsyncTaskLoader(getActivity(), vnArchive.listFiles());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Because app crashes sometimes without the try->catch

        Log.d("Paul", "delete file");

        File file = new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.temp_file_name));
        try {
            // if file exists in memory
            if (file.exists()) {
                file.delete();
                Log.d("Paul", "file deleted");
            }
        } catch (Exception e) {
            Log.d("Paul","Some error happened?");
        }

    }

}
