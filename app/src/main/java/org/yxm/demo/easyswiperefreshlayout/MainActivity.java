package org.yxm.demo.easyswiperefreshlayout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import org.yxm.demo.easyswiperefreshlayout.fragments.DefaultFragment;
import org.yxm.demo.easyswiperefreshlayout.fragments.FixedHeaderFragment;
import org.yxm.demo.easyswiperefreshlayout.fragments.MoveHeaderFragment;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

  public static final String TAG = "EasyRefreshLayout";

  private BottomNavigationView mBottomNavigations;

  private Fragment mCurFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mBottomNavigations = findViewById(R.id.bottom_navigations);
    mBottomNavigations.setOnNavigationItemSelectedListener(this);
    showFragment(DefaultFragment.class.getSimpleName());
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem menuItem) {
    boolean selected = false;
    switch (menuItem.getItemId()) {
      case R.id.action_default:
        showFragment(DefaultFragment.class.getSimpleName());
        selected = true;
        break;
      case R.id.action_rocket_fragment:
        showFragment(MoveHeaderFragment.class.getSimpleName());
        selected = true;
        break;
      case R.id.action_earth_fragment:
        showFragment(FixedHeaderFragment.class.getSimpleName());
        selected = true;
        break;
      case R.id.action_nest_fragment:
        break;
      case R.id.action_style_fixed:
        break;
    }
    return selected;
  }

  private void showFragment(@NonNull String fragmentTag) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    Fragment fragment = fm.findFragmentByTag(fragmentTag);
    if (fragment == null) {
      if (DefaultFragment.class.getSimpleName().equals(fragmentTag)) {
        fragment = DefaultFragment.newInstance();
      } else if (MoveHeaderFragment.class.getSimpleName().equals(fragmentTag)) {
        fragment = MoveHeaderFragment.newInstance();
      } else if (FixedHeaderFragment.class.getSimpleName().equals(fragmentTag)) {
        fragment = FixedHeaderFragment.newInstance();
      }
      ft.add(R.id.content, fragment, fragmentTag);
    }
    if (mCurFragment != null) {
      ft.hide(mCurFragment);
    }
    ft.show(fragment);
    mCurFragment = fragment;
    ft.commit();
  }
}
