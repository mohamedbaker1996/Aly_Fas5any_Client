package com.simpelexo.alyfas5anyclient.EventBus;

import com.simpelexo.alyfas5anyclient.Model.BestDeals;

public class BestDealItemClick {
    private BestDeals bestDealsModel;

    public BestDealItemClick(BestDeals bestDealsModel) {
        this.bestDealsModel = bestDealsModel;
    }

    public BestDeals getBestDealsModel() {
        return bestDealsModel;
    }

    public void setBestDealsModel(BestDeals bestDealsModel) {
        this.bestDealsModel = bestDealsModel;
    }
}
