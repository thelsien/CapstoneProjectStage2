package apps.nanodegree.thelsien.capstone.adapters;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;

import apps.nanodegree.thelsien.capstone.R;
import apps.nanodegree.thelsien.capstone.Utility;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

/**
 * Created by frodo on 2016. 11. 07..
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesAdapterViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnCategoryClickListener mListener;
    private boolean mIsMainFragmentAdapter;

    public CategoriesAdapter(Context context, Cursor cursor, OnCategoryClickListener listener, boolean isMainFragmentAdapter) {
        mContext = context;
        mCursor = cursor;
        mListener = listener;
        mIsMainFragmentAdapter = isMainFragmentAdapter;
    }

    @Override
    public CategoriesAdapter.CategoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.main_categories_grid_list_item,
                parent,
                false
        );

        return new CategoriesAdapterViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CategoriesAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mIconView.setImageResource(mCursor.getInt(mCursor.getColumnIndex(MainCategoriesTable.FIELD_ICON_RES)));
        holder.mNameView.setText(mCursor.getString(mCursor.getColumnIndex(MainCategoriesTable.FIELD_NAME)));
        if (mIsMainFragmentAdapter) {
            float categoryValue = Utility.getCategoryValue(mContext, mCursor.getInt(mCursor.getColumnIndex(MainCategoriesTable.FIELD__ID)));

            holder.mValueContainer.setVisibility(View.VISIBLE);
            holder.mValueView.setText(NumberFormat.getInstance().format(categoryValue));
            holder.mCurrencyView.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getString(R.string.prefs_current_currency_key), mContext.getString(R.string.default_currency)));
        } else {
            holder.mValueContainer.setVisibility(View.GONE);
        }
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

    public class CategoriesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mIconView;
        public TextView mNameView;
        public TextView mValueView;
        public LinearLayout mValueContainer;
        public TextView mCurrencyView;

        public CategoriesAdapterViewHolder(View itemView) {
            super(itemView);

            mValueContainer = (LinearLayout) itemView.findViewById(R.id.container_values_sum);
            mIconView = (ImageView) itemView.findViewById(R.id.iv_icon);
            mNameView = (TextView) itemView.findViewById(R.id.tv_name);
            mValueView = (TextView) itemView.findViewById(R.id.tv_value);
            mCurrencyView = (TextView) itemView.findViewById(R.id.tv_currency);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mListener.onCategoryClicked(mCursor.getInt(mCursor.getColumnIndex(MainCategoriesTable.FIELD__ID)));
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClicked(int categoryId);
    }
}
