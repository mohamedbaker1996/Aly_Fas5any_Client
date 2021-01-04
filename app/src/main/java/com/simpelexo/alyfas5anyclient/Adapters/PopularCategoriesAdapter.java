package com.simpelexo.alyfas5anyclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.simpelexo.alyfas5anyclient.Callback.ICategoryCallbackListener;
import com.simpelexo.alyfas5anyclient.Callback.ItemClickListener;
import com.simpelexo.alyfas5anyclient.EventBus.PopularCategoryClick;
import com.simpelexo.alyfas5anyclient.Model.PopularCategory;
import com.simpelexo.alyfas5anyclient.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.PopularViewHolder> {

    Context context;
    List<PopularCategory> popularCategoriesList;

    public PopularCategoriesAdapter(Context context, List<PopularCategory> popularCategoriesList) {
        this.context = context;
        this.popularCategoriesList = popularCategoriesList;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopularViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_populer_categories_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        Glide.with(context).load(popularCategoriesList.get(position).getImage())
                .into(holder.category_image);
        holder.txt_category_name.setText(popularCategoriesList.get(position).getName());
        holder.setListener((view, position1, isLongClick) -> {
            EventBus.getDefault().postSticky(new PopularCategoryClick(popularCategoriesList.get(position1)));
        });
    }

    @Override
    public int getItemCount() {
        return popularCategoriesList.size();
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;


        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;

        ItemClickListener listener;

        public ItemClickListener getListener() {
            return listener;
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this , itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition(),false);
        }
    }
}
