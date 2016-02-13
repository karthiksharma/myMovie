package com.karthik.mymovie.mymovie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment {


    public MainActivityFragment() {
        // Required empty public constructor
    }

    private MovieTile[] movieList = {
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png"),
            new MovieTile("http://i.imgur.com/DvpvklR.png")
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        MovieTileAdapter movieAdapter = new MovieTileAdapter(getContext(),0, Arrays.asList(movieList));
        GridView gridView = (GridView) rootView;//.findViewById(R.id.discover_movie_grid_view);

        gridView.setAdapter(movieAdapter);

        return gridView;
    }

}
