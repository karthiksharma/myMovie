package com.karthik.mymovie.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private static boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Credits: Karthik", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        if(findViewById(R.id.movie_detail_container) == null){
//            Log.v("MAFF", "TWO PANE = false");
            mTwoPane = false;
        }
        else {
//            Log.v("MAFF", "TWO PANE = true");
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
//                Log.v("MAFF", "Fragment added");
            }/*
            else{
//                Log.v("MAFF", "In else block");
            }*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean isTwoPane() {
        return mTwoPane;
    }

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }

    @Override
    public void onItemSelected(MovieTile movieItem) {
        if(mTwoPane){
            Bundle arg = new Bundle();
            arg.putString("Selected Movie", movieItem.getMovieId());

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arg);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, movieItem.getMovieId());
            startActivity(intent);
        }
    }
}
