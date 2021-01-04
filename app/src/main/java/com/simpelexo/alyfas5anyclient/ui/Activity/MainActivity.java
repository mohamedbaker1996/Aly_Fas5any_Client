package com.simpelexo.alyfas5anyclient.ui.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.simpelexo.alyfas5anyclient.Model.UserModel;
import com.simpelexo.alyfas5anyclient.R;
import com.simpelexo.alyfas5anyclient.Utiles.Common;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static  int APP_REQUEST_CODE =7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DatabaseReference userRef;
  //  private List<AuthUI.IdpConfig> providers;
    private List<AuthUI.IdpConfig> providers;


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);

    }

    @Override
    protected void onStop() {
        if (listener !=null) {
            firebaseAuth.removeAuthStateListener(listener);
            compositeDisposable.clear();
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        init();

    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user!= null) {
                //Already login
                checkUserFromFirebase(user);
                Snackbar snackBar = Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
                                                      R.string.you_already_registerd, Snackbar.LENGTH_LONG);
                                              snackBar.setBackgroundTint(Color.parseColor("#2c3e50"));
                                              snackBar.show();
//                Toast.makeText(MainActivity.this, R.string.you_already_registerd, Toast.LENGTH_SHORT).show();
            }else {
                phoneLogin();
            }

        };

    }
    private void goToHomeActivity(UserModel userModel){
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Common.currentUser = userModel;
                    //Common.currentToken=token;
                    //start Activity
                    startActivity(new Intent(MainActivity.this,HomeCycleActivity.class));
                    finish();
                }).addOnCompleteListener(task -> {
            Common.currentUser = userModel;
          //  Common.currentToken=token;
            Common.updateToken(MainActivity.this,task.getResult().getToken());
            Common.currentToken =task.getResult().getToken();
            //start Activity
            startActivity(new Intent(MainActivity.this,HomeCycleActivity.class));
            finish();
                });


    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else {
                Toast.makeText(this, R.string.fail_sign_in, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

//                            Toast.makeText(MainActivity.this, R.string.login_sucess, Toast.LENGTH_SHORT).show();
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            goToHomeActivity(userModel);
                        }else {
                            ShowRegisterDialog(user);

                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
   private void ShowRegisterDialog(@NonNull FirebaseUser user){
       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
       builder.setTitle(R.string.register);
               builder.setMessage(R.string.fill_all_info);

       View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
       EditText edt_name = (EditText)itemView.findViewById(R.id.edt_name);
       EditText edt_address = (EditText)itemView.findViewById(R.id.edt_address);
       EditText edt_phone = (EditText)itemView.findViewById(R.id.edt_phone);

       //set
       edt_phone.setText(user.getPhoneNumber());
       builder.setView(itemView);
       builder.setNegativeButton(R.string.cancel, (dialog, which) ->
               {
                   dialog.dismiss();
                   Intent mainActivity = new Intent(MainActivity.this, MainActivity.class);
                   mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(mainActivity);
                   finish();
               }
       );

       builder.setPositiveButton(R.string.register, (dialog, which) -> {
           if (TextUtils.isEmpty(edt_name.getText().toString())) {
               Toast.makeText(MainActivity.this, R.string.enter_your_name, Toast.LENGTH_SHORT).show();

               return;
           } else if (TextUtils.isEmpty(edt_phone.getText().toString())) {
               Toast.makeText(MainActivity.this, R.string.enter_your_phone, Toast.LENGTH_SHORT).show();
               return;
           }
           UserModel userModel = new UserModel();
           userModel.setUid(user.getUid());
           userModel.setName(edt_name.getText().toString());
           userModel.setAddress(edt_address.getText().toString());
           userModel.setPhone(edt_phone.getText().toString());
           userRef.child(user.getUid()).setValue(userModel)
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           dialog.dismiss();
                           goToHomeActivity(userModel);
                           Toast.makeText(MainActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                       }
                   });

       });


       builder.setView(itemView);

       androidx.appcompat.app.AlertDialog dialogm = builder.create();
       dialogm.show();
    }

}