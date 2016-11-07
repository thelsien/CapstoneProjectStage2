package apps.nanodegree.thelsien.capstone;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by frodo on 2016. 11. 07..
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesAdapterViewHolder> {

    private Cursor mCursor;

    public CategoriesAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public CategoriesAdapter.CategoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_categories_list_row, parent, false);

        return new CategoriesAdapterViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CategoriesAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mIconView.setImageResource(R.drawable.ic_kitchen_black_48dp);
        holder.mNameView.setText("Food");
        holder.mValueView.setText("90%");
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }

        return 0;
    }

    public class CategoriesAdapterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIconView;
        public TextView mNameView;
        public TextView mValueView;

        public CategoriesAdapterViewHolder(View itemView) {
            super(itemView);

            mIconView = (ImageView) itemView.findViewById(R.id.iv_icon);
            mNameView = (TextView) itemView.findViewById(R.id.tv_name);
            mValueView = (TextView) itemView.findViewById(R.id.tv_value);
        }
    }
}
