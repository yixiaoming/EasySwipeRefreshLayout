package org.yxm.demo.easyswiperefreshlayout;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.yxm.demo.easyswiperefreshlayout.fragments.RocketFragment;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "EasyRefreshLayout";

  private BottomNavigationView mBottomNavigations;

  private Fragment mEarthFragment;
  private Fragment mRocketFragment;

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mBottomNavigations = findViewById(R.id.bottom_navigations);

//    if (mEarthFragment == null) {
//      mEarthFragment = EarthFragment.newInstance();
//      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//      transaction.add(R.id.content, mEarthFragment, EarthFragment.class.getSimpleName());
//      transaction.commit();
//    }
    if (mRocketFragment == null) {
      mRocketFragment = RocketFragment.newInstance();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.add(R.id.content, mRocketFragment, RocketFragment.class.getSimpleName());
      transaction.commit();
    }
  }
}
