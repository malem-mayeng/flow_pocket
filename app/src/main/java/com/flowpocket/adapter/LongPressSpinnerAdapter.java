package com.flowpocket.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class LongPressSpinnerAdapter extends ArrayAdapter<String> {
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, String item);
    }

    public LongPressSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        android.util.Log.d("SpinnerEdit", "getDropDownView called for position: " + position);

        // Inflate custom layout with edit icon
        if (convertView == null) {
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(getContext());
            convertView = inflater.inflate(com.flowpocket.R.layout.spinner_dropdown_item_with_edit, parent, false);
        }

        android.widget.TextView categoryName = convertView.findViewById(com.flowpocket.R.id.category_name);
        android.widget.ImageView editIcon = convertView.findViewById(com.flowpocket.R.id.edit_icon);

        String item = getItem(position);
        categoryName.setText(item);

        // Handle edit icon click (only for non-Others categories)
        if (longClickListener != null && !"Others".equals(item)) {
            editIcon.setVisibility(android.view.View.VISIBLE);
            editIcon.setOnClickListener(v -> {
                android.util.Log.d("SpinnerEdit", "Edit icon clicked for: " + item);
                longClickListener.onItemLongClick(position, item);
            });
        } else {
            editIcon.setVisibility(android.view.View.GONE);
        }

        return convertView;
    }
}