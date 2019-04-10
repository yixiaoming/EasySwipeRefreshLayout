package org.yxm.demo.easyswiperefreshlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.airbnb.lottie.LottieAnimationView;
import org.yxm.demo.widget.EasySwipeRefreshLayout;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnScrollStateChangeListener;


/**
 * 自定义HeaderView实例
 */
public class RocketRefreshLayout extends EasySwipeRefreshLayout implements
    OnScrollStateChangeListener {

  private LottieAnimationView mPulldownAnim;
  private LottieAnimationView mRefreshAnim;

  public RocketRefreshLayout(Context context) {
    super(context);
  }

  public RocketRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RocketRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 如果想自己设置HeaderView，重写这个方法，设置header view和scroll listener
   */
  @Override
  protected void buildHeaderView() {
    mHeaderView = LayoutInflater.from(getContext()).inflate(
        R.layout.rocket_header_view, this, false
    );
    addView(mHeaderView);
    setProcessListener(this);
    mPulldownAnim = findViewById(R.id.pulldown_anim);
    mPulldownAnim.setAnimation("fly.json");

    mRefreshAnim = findViewById(R.id.refresh_anim);
    mRefreshAnim.setAnimation("circle.json");
    mRefreshAnim.setRepeatCount(ValueAnimator.INFINITE);
  }

  @Override
  public void stopRefreshing() {
    super.stopRefreshing();
    mRefreshAnim.setProgress(0);
    mPulldownAnim.setProgress(0);
  }

  @Override
  public void onScrollStateChange(int state, int headerHeight, int scrollY) {
    float process = (float) (scrollY * 1.0 / headerHeight);
    process = Math.min(0.5f, process);
    mPulldownAnim.setVisibility(VISIBLE);
    mPulldownAnim.setProgress(process);
    mRefreshAnim.setVisibility(GONE);

    if (state == REFRESHING) {
      mPulldownAnim.resumeAnimation();
      mPulldownAnim.addAnimatorListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          mPulldownAnim.pauseAnimation();
          mPulldownAnim.setVisibility(GONE);
          mRefreshAnim.playAnimation();
          mRefreshAnim.setVisibility(VISIBLE);
        }
      });
    }
  }
}
