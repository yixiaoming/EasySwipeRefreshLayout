package org.yxm.demo.easyswiperefreshlayout.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.airbnb.lottie.LottieAnimationView;
import org.yxm.demo.easyswiperefreshlayout.R;
import org.yxm.demo.widget.EasySwipeRefreshLayout;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnScrollStateChangeListener;


/**
 * 自定义HeaderView实例
 */
public class EarthRefreshlayout extends EasySwipeRefreshLayout
    implements OnScrollStateChangeListener {

  private LottieAnimationView mHeaderAnim;

  public EarthRefreshlayout(Context context) {
    super(context);
  }

  public EarthRefreshlayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * 自定义HeaderView重写该方法
   */
  @Override
  protected void buildHeaderView() {
    mHeaderView = LayoutInflater.from(getContext())
        .inflate(R.layout.earth_header_view, this, false);
    addView(mHeaderView);
    setProcessListener(this);
    mHeaderAnim = mHeaderView.findViewById(R.id.refresh_anim);
    mHeaderAnim.setAnimation("earth.json");
    mHeaderAnim.useHardwareAcceleration();
    mHeaderAnim.setRepeatCount(-1);
  }


  @Override
  public void onScrollStateChange(int state, int headerHeight, int scrollY) {
    if (state == REFRESHING) {
      mHeaderAnim.playAnimation();
    }
  }

  @Override
  public void stopRefreshing() {
    super.stopRefreshing();
    mHeaderAnim.cancelAnimation();
  }

}
