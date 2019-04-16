package org.yxm.demo.widget;

import static org.yxm.demo.widget.EasySwipeRefreshLayout.PULL_TO_REFRESH;
import static org.yxm.demo.widget.EasySwipeRefreshLayout.REFRESHING;
import static org.yxm.demo.widget.EasySwipeRefreshLayout.RELEASE_TO_REFRESH;

import android.view.View;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnRefreshListener;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnScrollStateChangeListener;

class MoveHeaderStrategy implements IStyleStrategy {

  private EasySwipeRefreshLayout mRefreshLayout;
  private View mTargetView;
  private View mHeaderView;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mOnRefreshListener;

  public MoveHeaderStrategy(EasySwipeRefreshLayout view) {
    mRefreshLayout = view;
    mTargetView = mRefreshLayout.getTargetView();
    mHeaderView = mRefreshLayout.getHeaderView();
    mProcessListener = mRefreshLayout.getProcessListener();
    mOnRefreshListener = mRefreshLayout.getOnRefreshListener();
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
    if (mProcessListener != null) {
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
  }

  @Override
  public void setOnRefreshListener(OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  @Override
  public void smoothScrollToHeader() {
    mRefreshLayout.smoothScrollTo(mRefreshLayout.getScrollY(), -mHeaderView.getHeight());
  }

  @Override
  public void smoothScrollToReset() {
    mRefreshLayout.smoothScrollTo(mRefreshLayout.getScrollY(), 0);
  }

}
