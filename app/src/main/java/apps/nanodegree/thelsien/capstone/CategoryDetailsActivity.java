package apps.nanodegree.thelsien.capstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class CategoryDetailsActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_CATEGORY_ID = "category_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(INTENT_EXTRA_CATEGORY_ID, getIntent().getIntExtra(INTENT_EXTRA_CATEGORY_ID, -1));

            CategoryDetailsFragment fragment = new CategoryDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.category_detail_container, fragment)
                    .commit();
        }
    }
}
