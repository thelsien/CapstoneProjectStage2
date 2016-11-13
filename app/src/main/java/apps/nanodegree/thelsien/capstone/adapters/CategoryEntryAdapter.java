package apps.nanodegree.thelsien.capstone.adapters;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import apps.nanodegree.thelsien.capstone.R;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;

/**
 * Created by frodo on 2016. 11. 09..
 */

public class CategoryEntryAdapter extends RecyclerView.Adapter<CategoryEntryAdapter.SpendingsViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnEntryClickedListener mListener;
    private String mCurrencyString;

    public CategoryEntryAdapter(Context context, Cursor cursor, OnEntryClickedListener listener) {
        super();

        this.mContext = context;
        this.mCursor = cursor;
        this.mListener = listener;

        mCurrencyString = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getResources().getString(R.string.prefs_current_currency_key),
                context.getResources().getString(R.string.default_currency)
        );
    }

    @Override
    public SpendingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.category_entry_list_row, parent, false);
        return new SpendingsViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(SpendingsViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mCurrencyView.setText(mCurrencyString);
        holder.mValueView.setText(NumberFormat.getInstance().format(mCursor.getFloat(mCursor.getColumnIndex(SpendingsTable.FIELD_VALUE))));
        holder.mNoteView.setText(mCursor.getString(mCursor.getColumnIndex(SpendingsTable.FIELD_NOTE)));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(mCursor.getColumnIndex(SpendingsTable.FIELD_DATE)) * 1000);
        String dateText = DateFormat.getDateInstance().format(cal.getTime());

        holder.mDateView.setText(dateText);
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

    public class SpendingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mValueView;
        public TextView mNoteView;
        public TextView mDateView;
        public TextView mCurrencyView;

        public SpendingsViewHolder(View itemView) {
            super(itemView);

            mValueView = (TextView) itemView.findViewById(R.id.tv_value);
            mNoteView = (TextView) itemView.findViewById(R.id.tv_note);
            mDateView = (TextView) itemView.findViewById(R.id.tv_date);
            mCurrencyView = (TextView) itemView.findViewById(R.id.tv_currency);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            mListener.onEntryClicked(mCursor.getInt(mCursor.getColumnIndex(SpendingsTable.FIELD_ID)));
        }
    }

    public interface OnEntryClickedListener {
        void onEntryClicked(int entryId);
    }
}
