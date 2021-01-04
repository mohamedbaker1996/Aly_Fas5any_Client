package com.simpelexo.alyfas5anyclient.Utiles;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.NumberFormat;
import java.util.Locale;

public class HelperMethod {
    private static ProgressDialog checkDialog;
    public static AlertDialog alertDialog;

    public static void replaceFragment(FragmentManager getChildFragmentManager, int id, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }





    public static void showProgressDialog(Activity activity, String title,boolean cancelable) {
        try {

            checkDialog = new ProgressDialog(activity);
            checkDialog.setMessage(title);
            checkDialog.setIndeterminate(false);
            checkDialog.setCancelable(cancelable);

            checkDialog.show();

        } catch (Exception e) {

        }
    }

    public static void dismissProgressDialog() {
        try {

            checkDialog.dismiss();

        } catch (Exception e) {

        }
    }


    public static String getFormatedNumber(String number){
        if(!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        }else{
            return "0";
        }
    }


    public static void disappearKeypad(Activity activity, View v) {
        try {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }
  /*  public static void customToast(Activity activity, String ToastTitle, boolean failed) {

        LayoutInflater inflater = activity.getLayoutInflater();

        int layout_id;

        if (failed) {
            layout_id = R.layout.toast;
        } else {
            layout_id = R.layout.success_toast;
        }

        View layout = inflater.inflate(layout_id,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.text);
        text.setText(ToastTitle);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }*/

    public static void onPermission(Activity activity) {
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE};

        ActivityCompat.requestPermissions(activity,
                perms,
                100);

    }
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

}
