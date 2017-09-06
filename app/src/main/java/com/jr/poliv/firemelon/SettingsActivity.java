package com.jr.poliv.firemelon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    boolean changesMade = false;
    Button voiceNote, images, save, cancel;
    SharedPreferences preferences;
    public final static int VOICE_NOTE_BUTTON_RESULT_CODE = 100;
    public final static int IMAGES_BUTTON_RESULT_CODE = 1000;
    public final static String BUTTON_TEXT = "button_text";
    public final static String RESULT_CODE = "result_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        voiceNote = (Button) findViewById(R.id.btVoiceNoteDirectory);
        images = (Button) findViewById(R.id.btImagesDirectory);
        voiceNote.setText(getVoiceNoteFilePathFromSharedPreference());
        images.setText(getImagesFilePathFromSharedPreference());
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        voiceNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DirectorySelector.class);
                intent.putExtra(RESULT_CODE, VOICE_NOTE_BUTTON_RESULT_CODE);
                intent.putExtra(BUTTON_TEXT, ((Button) v).getText().toString());
                startActivityForResult(intent, 0);

            }
        });

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DirectorySelector.class);
                intent.putExtra(RESULT_CODE, IMAGES_BUTTON_RESULT_CODE);
                intent.putExtra(BUTTON_TEXT, ((Button) v).getText().toString());
                startActivityForResult(intent, 0);

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    private String getVoiceNoteFilePathFromSharedPreference(){
        return preferences.getString(getString(R.string.voice_note_file_path), getString(R.string.default_voice_note_directory));
    }

    private String getImagesFilePathFromSharedPreference(){
        return preferences.getString(getString(R.string.images_file_path), getString(R.string.default_images_directory));
    }

    private void save(){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.voice_note_file_path), voiceNote.getText().toString());
        editor.putString(getString(R.string.images_file_path), images.getText().toString());
        if(editor.commit()){
            setResult(RESULT_OK);
            finish();
        }
        else
            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
    }

    private void cancel(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(changesMade)
        {
           showDialogue();
           return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(changesMade)
                    showDialogue();
                else
                    finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case VOICE_NOTE_BUTTON_RESULT_CODE:
                voiceNote.setText(data.getStringExtra(BUTTON_TEXT));
                changesMade = true;
                break;
            case IMAGES_BUTTON_RESULT_CODE:
                images.setText(data.getStringExtra(BUTTON_TEXT));
                changesMade = true;
                break;
            default:
        }
    }

    private void showDialogue(){
        DialogFragment newFragment = new ConfirmationDialog();
        newFragment.show(getFragmentManager(), "Confirmation");
    }

    public  class ConfirmationDialog extends DialogFragment{

        public ConfirmationDialog(){

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("There are unsaved changes. Are you sure you want to exit?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int boss) {
                                    SettingsActivity.this.cancel();
                                }
                            }
                    )
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }
                    )
                    .create();
        }
    }


}
