package itis.practice.imagestask;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rvImages)
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        int columnsCount = getResources().getInteger(R.integer.columnsCount);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = point.x;

        mGridLayoutManager = new GridLayoutManager(this, columnsCount);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(new GridAdapter(this, screenWidth/columnsCount));

    }

}
