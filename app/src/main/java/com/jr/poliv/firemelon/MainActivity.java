package com.jr.poliv.firemelon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jr.poliv.firemelon.Adapters.FileAdapter;

public class MainActivity extends AppCompatActivity implements FileAdapter.CardViewListener {

    private static final int REQUEST_CODE_FOR_SETTINGS_INTENT = 1000;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private final int VOICE_NOTE_FRAGMENT = 0;
    private final int IMAGES_FRAGMENT = 1;
    private final int FILE_MANAGER_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intent intent = new Intent(this, VoiceNoteFragment.class); startActivity(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if(mViewPager.getCurrentItem() == FILE_MANAGER_FRAGMENT)
            if( ((FileManagerFragment) mSectionsPagerAdapter.fragments[FILE_MANAGER_FRAGMENT]).onKeyDown(keyCode, event) )
                return true;

        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0){
            if (mViewPager.getCurrentItem() == FILE_MANAGER_FRAGMENT)
                ((FileManagerFragment) mSectionsPagerAdapter.fragments[FILE_MANAGER_FRAGMENT]).onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (mViewPager.getCurrentItem() == VOICE_NOTE_FRAGMENT)
                ((VoiceNoteFragment) mSectionsPagerAdapter.fragments[VOICE_NOTE_FRAGMENT]).onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (mViewPager.getCurrentItem() == IMAGES_FRAGMENT)
                ((ImagesFragment) mSectionsPagerAdapter.fragments[IMAGES_FRAGMENT]).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_FOR_SETTINGS_INTENT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == REQUEST_CODE_FOR_SETTINGS_INTENT){
            switch (resultCode){
                case RESULT_OK: finish(); startActivity(getIntent());
                    break;
                default: case RESULT_CANCELED:
            }
        }
    }

    @Override
    public void cardViewListener(int position) {
        if(mViewPager.getCurrentItem() == FILE_MANAGER_FRAGMENT)
            ((FileManagerFragment) mSectionsPagerAdapter.fragments[FILE_MANAGER_FRAGMENT]).cardViewListener(position);

        if(mViewPager.getCurrentItem() == VOICE_NOTE_FRAGMENT)
            ((VoiceNoteFragment) mSectionsPagerAdapter.fragments[VOICE_NOTE_FRAGMENT]).cardViewListener(position);

        if(mViewPager.getCurrentItem() == IMAGES_FRAGMENT)
            ((ImagesFragment) mSectionsPagerAdapter.fragments[IMAGES_FRAGMENT]).cardViewListener(position);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments = new Fragment[getCount()];


        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position){
                case 0: fragments[position] = VoiceNoteFragment.newInstance();
                    return fragments[position];

                case 1: fragments[position] = ImagesFragment.newInstance();
                    return fragments[position];

                case 2: fragments[position] = FileManagerFragment.newInstance();
                    return fragments[position];

                default: return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Voice Notes";
                case 1:
                    return "Images";
                case 2:
                    return "File Manger";
            }
            return null;
        }
    }
}
