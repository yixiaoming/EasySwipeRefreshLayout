package org.yxm.component.refreshlayout;

import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnRefreshListener;

public interface IStyleStrategy {

  void onLayout();

  void onStopNestedScroll();

  void onNestedPreScroll(int dy);

  void onNestedScroll(int dy);

  void smoothScrollToHeader();

  void smoothScrollToReset();

  void computeScrollState(boolean isReleased);

  void setOnRefreshListener(OnRefreshListener listener);
}
