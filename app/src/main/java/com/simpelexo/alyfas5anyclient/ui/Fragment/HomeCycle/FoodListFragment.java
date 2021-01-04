package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpelexo.alyfas5anyclient.Adapters.FoodListAdapter;
import com.simpelexo.alyfas5anyclient.EventBus.MenuItemBack;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.ViewModel.FoodListViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FoodListFragment extends BaseFragment {

   private FoodListViewModel foodViewModel;

    private Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView recycler_food_list;

    LayoutAnimationController layoutAnimationController;
    FoodListAdapter adapter;
    public FoodListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  setUpActivity();
       foodViewModel =
               ViewModelProviders.of(this).get(FoodListViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);
         setUpActivity();
        unbinder = ButterKnife.bind(this, view);

        initViews();
        foodViewModel.getMutableLiveDataFoodList().observe(this, foodModels -> {
            adapter = new FoodListAdapter(getContext(),foodModels);
            recycler_food_list.setAdapter(adapter);
            recycler_food_list.setLayoutAnimation(layoutAnimationController);
        });

        return view;
    }

    private void initViews() {
        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());
    recycler_food_list.setHasFixedSize(true);
    recycler_food_list.setLayoutManager(new LinearLayoutManager(getContext()));

    layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }



    @Override
    public void onBack() {
        super.onBack();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
        unbinder.unbind();
    }
}
