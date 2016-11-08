package apps.nanodegree.thelsien.capstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class CategoryDetailsFragment extends Fragment {

    public static final String TAG = CategoryDetailsFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_details, container, false);

        Bundle arguments = getArguments();
        Log.d(TAG, String.valueOf(arguments.getInt(CategoryDetailsActivity.INTENT_EXTRA_CATEGORY_ID)));

        return rootView;
    }
}
