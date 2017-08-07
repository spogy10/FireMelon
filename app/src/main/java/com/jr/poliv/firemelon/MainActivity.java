package com.jr.poliv.firemelon;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FileAdapter.CardViewListener, LoaderManager.LoaderCallbacks<ArrayList> {

    RecyclerView recycler;
    FileAdapter adapter;
    ArrayList<File> filesArrayList = new ArrayList<>();
    String file_path = File.separator+"storage"+File.separator+"6C58-E107"+File.separator+"vn archive";
    final File vnArchive = new File(file_path);
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean havePermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    }


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

    public String getMimeType(Uri uri) {
        String mimeType;

            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());

        mimeType = (mimeType == null)? "":mimeType;

        return mimeType;
    }

    private void permissionCheck(){
        havePermission = verifyStoragePermissions(this);
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
                }else{
                    Log.d("Paul", "Permission denied");
                }
            }
        }
    }

    @Override
    public Loader<ArrayList> onCreateLoader(int id, Bundle args) {
        Loader loader = new FileAsyncTaskLoader(this, vnArchive.listFiles());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Because app crashes sometimes without the try->catch

        File file = new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.temp_file_name));
        try {
            // if file exists in memory
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Log.d("Paul","Some error happened?");
        }

    }

}
