package com.simpelexo.alyfas5anyclient.ui.Activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.simpelexo.alyfas5anyclient.Database.CartDataSource;
import com.simpelexo.alyfas5anyclient.Database.CartDatabase;
import com.simpelexo.alyfas5anyclient.Database.LocalCartDataSource;
import com.simpelexo.alyfas5anyclient.EventBus.BestDealItemClick;
import com.simpelexo.alyfas5anyclient.EventBus.CategoryClick;
import com.simpelexo.alyfas5anyclient.EventBus.CounterCartEvent;
import com.simpelexo.alyfas5anyclient.EventBus.FoodItemClick;
import com.simpelexo.alyfas5anyclient.EventBus.HideFABCart;
import com.simpelexo.alyfas5anyclient.EventBus.MenuItemBack;
import com.simpelexo.alyfas5anyclient.EventBus.PopularCategoryClick;
import com.simpelexo.alyfas5anyclient.Model.Category;
import com.simpelexo.alyfas5anyclient.Model.FoodModel;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;
import com.simpelexo.alyfas5anyclient.Utiles.HelperMethod;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.CartFragment;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.FoodDetailFragment;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.FoodListFragment;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.HomeFragment;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.MenuMVFragment;
import com.simpelexo.alyfas5anyclient.ui.Fragment.HomeCycle.ViewOrderFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeCycleActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_home_cycle)
    FrameLayout frameHomeCycle;
    int menuClickId= -1;
    @BindView(R.id.fab)
    CounterFab fab;

    private CartDataSource cartDataSource;

    android.app.AlertDialog dialog;
    private NavController navController;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        counterCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = this.getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language", "ar");
        String languageToLoad = pine;
        Locale locale = new Locale(languageToLoad);//Set Selected Locale
        Locale.setDefault(locale);//set new locale as default
        //  Configuration config = new Configuration();//get Configuration
        Configuration config = new Configuration();//get Configuration
        config.locale = locale;//set config locale as selected locale
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        invalidateOptionsMenu();

        setContentView(R.layout.activity_home_cycle);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new HomeFragment());
        toolbar.setTitle(R.string.menu);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    counterCartItem();
        //    set name for user
        View headerView = navigationView.getHeaderView(0);
      TextView txt_user = headerView.findViewById(R.id.txtFullName);
      Common.setSpanString(getString(R.string.hi_user_name),Common.currentUser.getName(),txt_user);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(startMain);
          //  super.onBackPressed();
            super.onBackPressed();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         if (item.getItemId() == R.id.nav_language_arabic ) {
//            setLocale("ar");
             SharedPreferences ensharedPreferences = getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
             SharedPreferences.Editor eneditor = ensharedPreferences.edit();
             eneditor.putString("language", "ar");
             eneditor.apply();


             Toast toast = Toast.makeText(HomeCycleActivity.this, "تم تغيير اللغه الى العربيه", Toast.LENGTH_LONG);
             View view = toast.getView();

             //To change the Background of Toast
             view.setBackgroundColor(Color.parseColor("#3498db"));
             TextView text = (TextView) view.findViewById(android.R.id.message);

             //Shadow of the Of the Text Color
             text.setShadowLayer(1, 0, 0, Color.TRANSPARENT);
             text.setTextColor(Color.WHITE);
             text.setTextSize(Integer.valueOf(getResources().getString(R.string.text_size)));
             toast.show();
//             Toast.makeText(HomeCycleActivity.this, "Arabic Selected", Toast.LENGTH_SHORT).show();
             Common.currentLanguage ="ar";
             Intent refresh = new Intent(this, HomeCycleActivity.class);
             refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(refresh);
             finish();

             // this.recreate();

         }else if (item.getItemId() == R.id.nav_language_english  ){
          //  setLocale("En");
             SharedPreferences npsharedPrefrences = getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
             SharedPreferences.Editor npeditor = npsharedPrefrences.edit();
             npeditor.putString("language", "EN");
             npeditor.apply();
             Toast toast = Toast.makeText(HomeCycleActivity.this, "English Selected", Toast.LENGTH_LONG);
             View view = toast.getView();

             //To change the Background of Toast
             view.setBackgroundColor(Color.parseColor("#3498db"));
             TextView text = (TextView) view.findViewById(android.R.id.message);

             //Shadow of the Of the Text Color
             text.setShadowLayer(1, 0, 0, Color.TRANSPARENT);
             text.setTextColor(Color.WHITE);
             text.setTextSize(Integer.valueOf(getResources().getString(R.string.text_size)));
             toast.show();
             Common.currentLanguage ="En";
//             Intent intent=getIntent();
//             overridePendingTransition(0, 0);
//             finish();
//             overridePendingTransition(0, 0);
//             startActivity(intent);
             Intent refresh = new Intent(this, HomeCycleActivity.class);

             refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

             startActivity(refresh);
             finish();

         }


        return super.onOptionsItemSelected(item);
    }
//    public void setLocale(String lang) {
//        Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(this, HomeCycleActivity.class);
////        finish();
//        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(refresh);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home && item.getItemId() != menuClickId) {
//            Intent menuIntent = new Intent(HomeCycleActivity.this, HomeCycleActivity.class);
//            startActivity(menuIntent);
            toolbar.setTitle("menu");
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new HomeFragment());
        } else if (id == R.id.nav_menu && item.getItemId() != menuClickId) {
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new MenuMVFragment());

        }else if (id == R.id.nav_cart && item.getItemId() != menuClickId) {
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new CartFragment());

        } else if (id == R.id.nav_maps && item.getItemId() != menuClickId) {
            Intent orderIntent = new Intent(HomeCycleActivity.this, MapsActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_call_now && item.getItemId() != menuClickId) {
              onCall();

        } else if (id == R.id.nav_orders && item.getItemId() != menuClickId) {
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new ViewOrderFragment());



        } else if (id == R.id.nav_log_out && item.getItemId() != menuClickId) {


            signOut();
            // delete remember user and password
//            Paper.book().destroy();
//
//            Intent mainActivity = new Intent(HomeCycleActivity.this, MainActivity.class);
//            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(mainActivity);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuClickId = item.getItemId();
        return true;
    }

    private void onCall() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(String.valueOf(new StringBuilder("tel: 01126834431 ")
                                )));
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(HomeCycleActivity.this, getString(R.string.you_must_accept)+response.getPermissionName(), Toast.LENGTH_SHORT).show();
                      //  Toast.makeText(this, getString(R.string.you_must_accept), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }


    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sign_out)
                .setMessage(R.string.want_sign_out)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss()).setPositiveButton(getString(R.string.yes), (dialog, which) -> {
               Common.selectedFood = null;
               Common.categorySelected = null;
               Common.currentUser = null;
                    FirebaseAuth.getInstance().signOut();
                Intent mainActivity = new Intent(HomeCycleActivity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainActivity);
                finish();
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new CartFragment());
    }
    //EventBus


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event)
    {
        if (event.isSuccess()) {
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new FoodListFragment());

            // Toast.makeText(this, "click to "+event.getCategoryModel().getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onFoodItemClick(FoodItemClick event)
    {
        if (event.isSuccess()) {
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new FoodDetailFragment());


        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHiddenFAB(HideFABCart event)
    {
        if (event.isHidden()) {

        fab.hide();

        }else {fab.show();}
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event)
    {
        if (event.isSuccess()) {

            counterCartItem();

        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPopularItemClick(PopularCategoryClick event)
    {
        if (event.getPopularCategoryModel() != null) {
            dialog.show();
            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                    .child(event.getPopularCategoryModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Common.categorySelected = snapshot.getValue(Category.class);


                            //Load Food
                                FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                        .child(event.getPopularCategoryModel().getMenu_id())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getPopularCategoryModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot itemSnapShot:snapshot.getChildren())
                                                    {
                                                        Common.selectedFood = itemSnapShot.getValue(FoodModel.class);
                                                    }

                                                    HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new FoodDetailFragment());

                                                }else {

                                                    Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();

                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                             dialog.dismiss();
                                Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                            Toast.makeText(HomeCycleActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick(BestDealItemClick event)
    {
        if (event.getBestDealsModel() != null) {
            dialog.show();
            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                    .child(event.getBestDealsModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Common.categorySelected = snapshot.getValue(Category.class);
                                //Load Food
                                FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                        .child(event.getBestDealsModel().getMenu_id())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getBestDealsModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot itemSnapShot:snapshot.getChildren())
                                                    {
                                                        Common.selectedFood = itemSnapShot.getValue(FoodModel.class);
                                                    }

                                                    HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame_home_cycle, new FoodDetailFragment());

                                                }else {

                                                    Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();

                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                dialog.dismiss();
                                Toast.makeText(HomeCycleActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(HomeCycleActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }
    private void counterCartItem() {
   try {
       cartDataSource.countItemInCart(Common.currentUser.getUid())
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new SingleObserver<Integer>() {
                   @Override
                   public void onSubscribe(Disposable d) {

                   }

                   @Override
                   public void onSuccess(Integer integer) {
                       fab.setCount(integer);
                   }

                   @Override
                   public void onError(Throwable e) {
                       if (!e.getMessage().contains("Query returned empty")) {

                           Toast.makeText(HomeCycleActivity.this, "[COUNT CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }else {fab.setCount(0);}
                   }
               });
   }catch (Exception e){}
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMenuItemBack(MenuItemBack event){
        menuClickId = -1;
         navController.popBackStack(R.id.nav_home,true);
//         if (getSupportFragmentManager().getBackStackEntryCount() >0)
//             getSupportFragmentManager().popBackStack();
    }
}