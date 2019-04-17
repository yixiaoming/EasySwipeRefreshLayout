package org.yxm.demo.widget;

import org.yxm.demo.widget.EasySwipeRefreshLayout.OnRefreshListener;

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
