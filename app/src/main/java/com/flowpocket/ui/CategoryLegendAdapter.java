package com.flowpocket.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import java.util.ArrayList;
import java.util.List;

public class CategoryLegendAdapter extends RecyclerView.Adapter<CategoryLegendAdapter.LegendViewHolder> {

    public static class CategoryItem {
        public String name;
        public int color;
        public String percentage;

        public CategoryItem(String name, int color, String percentage) {
            this.name = name;
            this.color = color;
            this.percentage = percentage;
        }
    }

    private List<CategoryItem> items = new ArrayList<>();

    @NonNull
    @Override
    public LegendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_legend, parent, false);
        return new LegendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LegendViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<CategoryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class LegendViewHolder extends RecyclerView.ViewHolder {
        private View colorIndicator;
        private TextView categoryName;
        private TextView categoryPercentage;

        public LegendViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryPercentage = itemView.findViewById(R.id.category_percentage);
        }

        public void bind(CategoryItem item) {
            colorIndicator.setBackgroundColor(item.color);
            categoryName.setText(item.name);
            categoryPercentage.setText(item.percentage);
        }
    }
}