package com.karthik.mymovie.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* ViewGroup view = (ViewGroup) findViewById(R.id.fragment);
        getLayoutInflater().inflate(R.layout.fragment_detail, view);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailActivityFragment())
                    .commit();
        }*/
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Good Choice", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        if (savedInstanceState == null) {
            String movieId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
//            Log.v("DA", "movie id is " + movieId);

            Bundle arg = new Bundle();
            arg.putString("Selected Movie", movieId);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arg);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
