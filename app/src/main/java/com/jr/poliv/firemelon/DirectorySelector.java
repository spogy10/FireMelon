package com.jr.poliv.firemelon;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jr.poliv.firemelon.Adapters.FileAdapter;
import com.jr.poliv.firemelon.Adapters.FileManagerAdapter;
import com.jr.poliv.firemelon.AsyncTaskLoaders.FileAsyncTaskLoader;
import com.jr.poliv.firemelon.Stack.BackStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class DirectorySelector extends AppCompatActivity implements FileAdapter.CardViewListener, LoaderManager.LoaderCallbacks<ArrayList> {

    FloatingActionButton fab;
    RecyclerView recycler;
    TextView path;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selector_directory);
        permissionCheck();
        if(havePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("Paul", "external storage state " + Environment.getExternalStorageState());


            recycler = (RecyclerView) findViewById(R.id.rvFile);
            path = (TextView) findViewById(R.id.tvPath);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recycler.setLayoutManager(gridLayoutManager);
            adapter = new FileManagerAdapter(this, filesArrayList);
            recycler.setAdapter(adapter);

            backTrace = new BackStack(getIntent().getStringExtra(SettingsActivity.BUTTON_TEXT));
            if(!backTrace.isEmpty())
                directory = backTrace.pop();
            path.setText(directory.getAbsolutePath());


            getLoaderManager().initLoader(0, null, this);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });

        }else{
            Toast.makeText(this,"NO PERMISSION or MEDIA NOT MOUNTED", Toast.LENGTH_LONG).show();
        }

    }

    private void save(){
        Intent intent = new Intent();
        intent.putExtra(SettingsActivity.BUTTON_TEXT, directory.getAbsolutePath());
        setResult(getIntent().getIntExtra(SettingsActivity.RESULT_CODE, 0), intent);
        finish();
    }

    @Override
    public void cardViewListener(int position) {
        Log.d("Paul", "Card Clicked - "+String.valueOf(position));
        File tempFile = filesArrayList.get(position);
        if(tempFile.listFiles() != null){
            backTrace.push(directory);
            directory = tempFile;
            path.setText(directory.getAbsolutePath());
            getLoaderManager().restartLoader(0, null, this);
        }else if(tempFile.getAbsolutePath().equals(File.separator+"storage"+File.separator+"emulated")){
            backTrace.push(directory);
            directory = new File(File.separator+"storage"+File.separator+"emulated"+File.separator+"0");
            path.setText(directory.getAbsolutePath());
            getLoaderManager().restartLoader(0, null, this);
        }else{
            Toast.makeText(this,"CANNOT GO TO SELECTED DIRECTORY", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && !backTrace.empty()) {
            directory = backTrace.pop();
            path.setText(directory.getAbsolutePath());
            getLoaderManager().restartLoader(0, null, this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void permissionCheck(){
        havePermission = VoiceNoteFragment.verifyStoragePermissions(this);
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
        Loader loader = new FileAsyncTaskLoader(this, directory.listFiles(), FileAsyncTaskLoader.DIRECTORY_ONLY_MODE);
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
