package apps.nanodegree.thelsien.capstone;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

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

        holder.mIconView.setImageResource(mCursor.getInt(mCursor.getColumnIndex(MainCategoriesTable.FIELD_ICON_RES)));
        holder.mNameView.setText(mCursor.getString(mCursor.getColumnIndex(MainCategoriesTable.FIELD_NAME)));
        holder.mValueView.setText("0%");
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }

        return 0;
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
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
