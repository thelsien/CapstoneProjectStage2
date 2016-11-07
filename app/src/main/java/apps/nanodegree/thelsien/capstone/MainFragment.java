package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

/**
 * Created by frodo on 2016. 11. 07..
 */

public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setupMainCategories();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lv_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor categoriesCursor = getContext().getContentResolver().query(MainCategoriesTable.CONTENT_URI, null, null, null, null);
        mRecyclerView.setAdapter(new CategoriesAdapter(categoriesCursor));

        return rootView;
    }

    private void setupMainCategories() {
        Cursor c = getContext().getContentResolver().query(MainCategoriesTable.CONTENT_URI, null, null, null, null);

        if (c != null && !c.moveToFirst()) {
            c.close();

            ContentValues values = new ContentValues();
            values.put("name", "Food");
            values.put("icon_res", R.drawable.ic_kitchen_black_48dp);

            getContext().getContentResolver().insert(MainCategoriesTable.CONTENT_URI, values);
        }
    }
}
