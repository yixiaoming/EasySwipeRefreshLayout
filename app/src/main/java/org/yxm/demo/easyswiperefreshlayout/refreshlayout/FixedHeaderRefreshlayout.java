package org.yxm.demo.easyswiperefreshlayout.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.airbnb.lottie.LottieAnimationView;
import org.yxm.demo.easyswiperefreshlayout.R;
import org.yxm.demo.widget.EasySwipeRefreshLayout;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnScrollStateChangeListener;
import org.yxm.demo.widget.FixedHeaderStrategy;

/**
 * 自定义HeaderView实例
 */
public class FixedHeaderRefreshlayout extends EasySwipeRefreshLayout
    implements OnScrollStateChangeListener {

  private LottieAnimationView mHeaderAnim;

  public FixedHeaderRefreshlayout(Context context) {
    this(context, null);
  }

  public FixedHeaderRefreshlayout(Context context, AttributeSet attrs) {
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
    mStrategy = new FixedHeaderStrategy(this);
  }


  @Override
  public void onScrollStateChange(int state, int headerHeight, int scrollY) {
    mHeaderAnim.setVisibility(VISIBLE);
    float process = (float) (scrollY * 1.0 / headerHeight);
    mHeaderAnim.setProgress(process);
    if (state == REFRESHING) {
      mHeaderAnim.playAnimation();
    }
  }

  @Override
  public void stopRefreshing() {
    super.stopRefreshing();
    mHeaderAnim.cancelAnimation();
    mHeaderAnim.setVisibility(GONE);
  }

}
