package org.yxm.component.refreshlayout;

import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.RELEASE_TO_REFRESH;
import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.PULL_TO_REFRESH;
import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.REFRESHING;

import android.util.Log;
import android.view.View;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnRefreshListener;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnScrollStateChangeListener;

public class FixedHeaderStrategy implements IStyleStrategy {

  public static final String TAG = "FixedHeaderStrategy";

  private EasySwipeRefreshLayout mRefreshLayout;
  private View mTargetView;
  private View mHeaderView;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mOnRefreshListener;

  public FixedHeaderStrategy(EasySwipeRefreshLayout view) {
    mRefreshLayout = view;
    mTargetView = mRefreshLayout.getTargetView();
    mHeaderView = mRefreshLayout.getHeaderView();
    mProcessListener = mRefreshLayout.getProcessListener();
    mOnRefreshListener = mRefreshLayout.getOnRefreshListener();
  }

  @Override
  public void onLayout() {
    mHeaderView.layout(0, 0, mHeaderView.getMeasuredWidth(), mHeaderView.getMeasuredHeight());
  }

  @Override
  public void onStopNestedScroll() {
    if (mTargetView.getTop() >= mHeaderView.getHeight()) {
      smoothScrollToHeader();
      computeScrollState(true);
    } else if (mTargetView.getTop() < mHeaderView.getHeight()) {
      smoothScrollToReset();
      computeScrollState(true);
    }
  }

  @Override
  public void onNestedPreScroll(int dy) {
    int mostScrollOffset = -dy + mTargetView.getTop() >= 0 ? -dy : -mTargetView.getTop();
    mTargetView.offsetTopAndBottom(mostScrollOffset);
  }

  @Override
  public void onNestedScroll(int dy) {
    mTargetView.offsetTopAndBottom(-dy);
    computeScrollState(false);
  }

  @Override
  public void computeScrollState(boolean isReleased) {
    if (mRefreshLayout.getState() == REFRESHING) {
      return;
    }
    if (mProcessListener == null) {
      return;
    }
    // 更新refreshlayout state
    if (mTargetView.getTop() >= mHeaderView.getHeight()) {
      mRefreshLayout.setState(RELEASE_TO_REFRESH);
    } else if (mTargetView.getTop() < mHeaderView.getHeight()) {
      mRefreshLayout.setState(PULL_TO_REFRESH);
    }
    // 通知刷新开始
    if (isReleased && mRefreshLayout.getState() == RELEASE_TO_REFRESH) {
      mRefreshLayout.setState(REFRESHING);
      if (mOnRefreshListener != null) {
        mOnRefreshListener.onRefresh();
      }
    }
    // 通知header 滑动状态
    mProcessListener.onScrollStateChange(mRefreshLayout.getState(), mHeaderView.getHeight(),
        mTargetView.getTop());
  }

  @Override
  public void setOnRefreshListener(OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  @Override
  public void smoothScrollToHeader() {
    Log.d(TAG, "toHeader: headerHeight:"+mHeaderView.getHeight()+","
        + "targetHeight:"+mTargetView.getHeight()+",layoutHeight:"+mRefreshLayout.getHeight());
    mTargetView.layout(0, mHeaderView.getHeight(), mTargetView.getWidth(),
        mRefreshLayout.getHeight());
  }

  @Override
  public void smoothScrollToReset() {
    Log.d(TAG, "toReset: headerHeight:"+mHeaderView.getHeight()+","
        + "targetHeight:"+mTargetView.getHeight()+",layoutHeight:"+mRefreshLayout.getHeight());
    mTargetView.layout(0, 0, mTargetView.getWidth(), mTargetView.getHeight());
  }

}
