package com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpelexo.alyfas5anyclient.Adapters.MenuMVAdapter;
import com.simpelexo.alyfas5anyclient.EventBus.MenuItemBack;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.Utiles.HelperMethod;
import com.simpelexo.alyfas5anyclient.Utiles.SpacesItemDecoration;
import com.simpelexo.alyfas5anyclient.ViewModel.MenuMVViewModel;
import com.simpelexo.alyfas5anyclient.ui.Fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MenuMVFragment extends BaseFragment {
    private Unbinder unbinder;

    private MenuMVViewModel menuMVViewModel;


    @BindView(R.id.recycler_menu_fragment)
    RecyclerView recycle_menu_fragment;

    LayoutAnimationController layoutAnimationController;
    MenuMVAdapter adapter;

    public MenuMVFragment() {
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
        menuMVViewModel = ViewModelProviders.of(this).get(MenuMVViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_mv, container, false);
         setUpActivity();
        unbinder = ButterKnife.bind(this, view);

        initView();
        menuMVViewModel.getMessageError().observe(this,s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            HelperMethod.dismissProgressDialog();
        });

        menuMVViewModel.getCategoryListMutable().observe(this,categories -> {
            
            HelperMethod.dismissProgressDialog();
            adapter = new MenuMVAdapter(getContext(),categories);
            recycle_menu_fragment.setAdapter(adapter);
            recycle_menu_fragment.setLayoutAnimation(layoutAnimationController);
        });
        return view;
    }

    private void initView() {
        HelperMethod.showProgressDialog(getActivity(),"Loading",false);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
//         recycle_menu_fragment.setLayoutManager(layoutManager);
//       recycle_menu_fragment.addItemDecoration(new SpacesItemDecoration(8));
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null) {
                    switch (adapter.getItemViewType(position))
                    {
                        case Common
                                .DEFAULT_COLUMN_COUNT:return 1;
                        case Common.FULL_WIDTH_COLUMN:return 2;
                        default:return -1;
                    }
                }

                return -1;
            }
        });
        recycle_menu_fragment.setLayoutManager(layoutManager);
        recycle_menu_fragment.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onBack(){

        super.onBack();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
        unbinder.unbind();
    }
}
