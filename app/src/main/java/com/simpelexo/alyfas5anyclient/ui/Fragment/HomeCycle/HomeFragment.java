package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.simpelexo.alyfas5anyclient.Adapters.BestDealsAdapter;
import com.simpelexo.alyfas5anyclient.Adapters.PopularCategoriesAdapter;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.ViewModel.HomeViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class HomeFragment extends BaseFragment {
    private Unbinder unbinder;
    private HomeViewModel homeViewModel;

    @BindView(R.id.recycler_popular)
    RecyclerView recycler_popular;
    @BindView(R.id.view_pager_home)
    LoopingViewPager view_pager_home;

    LayoutAnimationController layoutAnimationController;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        setUpActivity();
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
          setUpActivity();
        unbinder = ButterKnife.bind(this, view);
        init();
        homeViewModel.getPopularList().observe(this,popularCategories -> {
        //create Adapter
            PopularCategoriesAdapter adapter = new PopularCategoriesAdapter(getContext(),popularCategories);
            recycler_popular.setAdapter(adapter);

        });

        homeViewModel.getBestDealList().observe(this,bestDeals -> {
            BestDealsAdapter adapter = new BestDealsAdapter(getContext(),bestDeals,true);
            view_pager_home.setAdapter(adapter);
            recycler_popular.setLayoutAnimation(layoutAnimationController);
        });

        return view;
    }

    private void init() {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);

        recycler_popular.setHasFixedSize(true);
        recycler_popular.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
    }

    @Override
    public void onBack(){
       // super.onBack();
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        view_pager_home.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        view_pager_home.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
