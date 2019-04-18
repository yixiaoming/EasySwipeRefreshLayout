package org.yxm.component.refreshlayout;

import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.PULL_TO_REFRESH;
import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.REFRESHING;
import static org.yxm.component.refreshlayout.EasySwipeRefreshLayout.RELEASE_TO_REFRESH;

import android.view.View;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnRefreshListener;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnScrollStateChangeListener;

public class MoveHeaderStrategy implements IStyleStrategy {

  private EasySwipeRefreshLayout mRefreshLayout;
  private View mHeaderView;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mOnRefreshListener;

  public MoveHeaderStrategy(EasySwipeRefreshLayout view) {
    mRefreshLayout = view;
    mHeaderView = mRefreshLayout.getHeaderView();
    mProcessListener = mRefreshLayout.getProcessListener();
    mOnRefreshListener = mRefreshLayout.getOnRefreshListener();
  }

  @Override
  public void onLayout() {
    mHeaderView.layout(0, 0 - mHeaderView.getMeasuredHeight(), mHeaderView.getMeasuredWidth(), 0);
  }

  @Override
  public void onStopNestedScroll() {
    if (-mRefreshLayout.getScrollY() >= mHeaderView.getHeight()) {
      computeScrollState(true);
      smoothScrollToHeader();
    } else if (mRefreshLayout.getScrollY() < 0
        && -mRefreshLayout.getScrollY() < mHeaderView.getHeight()) {
      computeScrollState(true);
      smoothScrollToReset();
    }
  }

  @Override
  public void onNestedPreScroll(int dy) {
    int mostScrollOffset = dy + mRefreshLayout.getScrollY() <= 0
        ? dy : -mRefreshLayout.getScrollY();
    mRefreshLayout.scrollBy(0, mostScrollOffset);
  }

  @Override
  public void onNestedScroll(int dy) {
    mRefreshLayout.scrollBy(0, dy);
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
    if (-mRefreshLayout.getScrollY() >= mHeaderView.getHeight()) {
      mRefreshLayout.setState(RELEASE_TO_REFRESH);
    } else if (-mRefreshLayout.getScrollY() > 0
        && -mRefreshLayout.getScrollY() < mHeaderView.getHeight()) {
      mRefreshLayout.setState(PULL_TO_REFRESH);
    }
    if (isReleased && mRefreshLayout.getState() == RELEASE_TO_REFRESH) {
      mRefreshLayout.setState(REFRESHING);
      if (mOnRefreshListener != null) {
        mOnRefreshListener.onRefresh();
      }
    }
    mProcessListener
        .onScrollStateChange(mRefreshLayout.getState(),
            mHeaderView.getHeight(), Math.abs(mRefreshLayout.getScrollY()));
  }

  @Override
  public void setOnRefreshListener(OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  @Override
  public void smoothScrollToHeader() {
    mRefreshLayout.smoothScrollTo(mRefreshLayout.getScrollY(), -mHeaderView.getHeight(), 500);
  }

  @Override
  public void smoothScrollToReset() {
    mRefreshLayout.smoothScrollTo(mRefreshLayout.getScrollY(), 0, 500);
  }

}
