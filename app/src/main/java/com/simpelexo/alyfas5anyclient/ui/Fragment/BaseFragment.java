package com.simpelexo.alyfas5anyclient.ui.Fragment;

import androidx.fragment.app.Fragment;

import com.simpelexo.alyfas5anyclient.ui.Activity.BaseActivity;


public class BaseFragment extends Fragment {
 public BaseActivity baseActivity;
    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();

        assert baseActivity != null;
        baseActivity.baseFragment = this;
    }
 public void onBack(){
     baseActivity.superBackPressed();
    }
}