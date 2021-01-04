package com.simpelexo.alyfas5anyclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.simpelexo.alyfas5anyclient.EventBus.BestDealItemClick;
import com.simpelexo.alyfas5anyclient.Model.BestDeals;
import com.simpelexo.alyfas5anyclient.Model.PopularCategory;
import com.simpelexo.alyfas5anyclient.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BestDealsAdapter extends LoopingPagerAdapter<BestDeals> {
    @BindView(R.id.img_best_deals)
        ImageView img_best_deals;
    @BindView(R.id.txt_best_deals)
            TextView txt_best_deals;


    Unbinder unbinder;

    public BestDealsAdapter(Context context, List<BestDeals> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }


    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.layout_best_deals_item,container,false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        unbinder = ButterKnife.bind(this,convertView);
        Glide.with(convertView).load(itemList.get(listPosition).getImage()).into(img_best_deals);
        txt_best_deals.setText(itemList.get(listPosition).getName());
        convertView.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new BestDealItemClick(itemList.get(listPosition)) );
        });
    }
}
//  unbinder = ButterKnife.bind(this,convertView);
//          Glide.with(convertView).load(itemList.get(listPosition).getImage()).into(img_best_deals);
//          txt_best_deals.setText(itemList.get(listPosition).getName());
