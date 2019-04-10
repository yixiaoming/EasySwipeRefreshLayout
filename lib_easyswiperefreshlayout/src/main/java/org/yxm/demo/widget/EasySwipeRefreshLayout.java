package org.yxm.demo.widget;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 下拉刷新LayoutView 支持自定义HeaderView，重写buildHeaderView方法 要获取下拉状态做动画，需要设置 OnScrollStateChangeListener
 *
 * @author yixiaoming
 */
public class EasySwipeRefreshLayout extends ViewGroup
    implements NestedScrollingParent, NestedScrollingChild {

  private static final String TAG = "EasyRefreshLayout";

  public static final int RESET = 0;
  public static final int PULL_TO_REFRESH = 1;
  public static final int RELEASE_TO_REFRESH = 2;
  public static final int REFRESHING = 3;

  protected View mHeaderView;
  private View mTargetView;
  private NestedScrollingParentHelper mNestedScrollingParentHelper;
  private Scroller mScroller;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mOnRefreshListener;
  private int mState = RESET;

  private int[] mParentConsumed = new int[2];
  private int[] mParentOffsetInWindow = new int[2];

  private int mTotalUnconsumed = 0;

  /** 刷新状态process获取接口，可做动画 */
  public interface OnScrollStateChangeListener {

    void onScrollStateChange(int state, int headerHeight, int scrollY);
  }

  /** 开始刷新接口 */
  public interface OnRefreshListener {

    void onRefresh();
  }

  public EasySwipeRefreshLayout(Context context) {
    this(context, null);
  }

  public EasySwipeRefreshLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EasySwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    mScroller = new Scroller(getContext());
    setNestedScrollingEnabled(true);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mTargetView = getChildAt(0);
    buildHeaderView();
  }

  /**
   * 自定义HeaderView重写该方法
   */
  protected void buildHeaderView() {
    if (mHeaderView == null) {
      mHeaderView = new DefaultHeaderView(getContext());
      setProcessListener((OnScrollStateChangeListener) mHeaderView);
      addView(mHeaderView, 0);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      measureChild(child, widthMeasureSpec, heightMeasureSpec);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int contentHeight = 0;
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child == mHeaderView) {
        child.layout(0, 0 - mHeaderView.getMeasuredHeight(), mHeaderView.getMeasuredWidth(), 0);
      } else {
        if (child.getVisibility() == View.GONE) {
          continue;
        }
        child.layout(0, contentHeight, child.getMeasuredWidth(),
            contentHeight + child.getMeasuredHeight());
        contentHeight += child.getMeasuredHeight();
      }
    }
  }

  /**
   * 设置下拉process回调
   */
  public void setProcessListener(OnScrollStateChangeListener listener) {
    mProcessListener = listener;
  }

  /**
   * 设置刷新开始listener
   */
  public void setOnRefreshListener(OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  /**
   * 判断targetview是否还可以下拉
   */
  private boolean canChildScrollDown() {
    if (mTargetView instanceof ListView) {
      return ListViewCompat.canScrollList((ListView) mTargetView, -1);
    }
    return mTargetView.canScrollVertically(-1);
  }

  private void smoothScrollTo(int from, int to) {
    mScroller.startScroll(0, from, 0, to - from, 500);
    invalidate();
  }

  private void smoothScrollToReset() {
    smoothScrollTo(getScrollY(), 0);
  }

  private void smoothScrollToHeader() {
    smoothScrollTo(getScrollY(), -mHeaderView.getHeight());
  }

  public void stopRefreshing() {
    smoothScrollToReset();
    mState = RESET;
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(0, mScroller.getCurrY());
      invalidate();
    }
  }

  /**
   * 处理滑动拖成中状态的修改
   *
   * @param isRelease touch事件release标志
   */
  private void computeScrollState(boolean isRelease) {
    if (mState == REFRESHING) {
      return;
    }
    if (mProcessListener != null) {
      if (-getScrollY() >= mHeaderView.getHeight()) {
        mState = RELEASE_TO_REFRESH;
      } else if (-getScrollY() > 0 && -getScrollY() < mHeaderView.getHeight()) {
        mState = PULL_TO_REFRESH;
      }
      if (isRelease && mState == RELEASE_TO_REFRESH) {
        mState = REFRESHING;
        if (mOnRefreshListener != null) {
          mOnRefreshListener.onRefresh();
        }
      }
      mProcessListener.onScrollStateChange(mState, mHeaderView.getHeight(), Math.abs(getScrollY()));
    }
  }

  //<editor-fold desc="NestedScrollingParent相关">
  @Override
  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedScrollAccepted(View child, View target, int axes) {
    mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
    mTotalUnconsumed = 0;
  }

  @Override
  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    if (dy > 0 && mTotalUnconsumed > 0) {
      if (dy > mTotalUnconsumed) {
        consumed[1] = dy - mTotalUnconsumed;
        mTotalUnconsumed = 0;
      } else {
        mTotalUnconsumed -= dy;
        consumed[1] = dy;
      }
      // 解决快速方向滑动导致整个layout都向上滑动问题
      if (dy + getScrollY() <= 0) {
        scrollBy(0, dy);
      } else {
        scrollBy(0, -getScrollY());
      }
      computeScrollState(false);
    }
    final int[] parentConsumed = mParentConsumed;
    if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
      consumed[0] += parentConsumed[0];
      consumed[1] += parentConsumed[1];
    }
  }


  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed) {
    // 下拉时，先问parent需要消耗的touch事件
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
        mParentOffsetInWindow);
    int dy = dyUnconsumed + mParentOffsetInWindow[1];
    if (dy < 0 && !canChildScrollDown()) {
      mTotalUnconsumed += Math.abs(dy);
      scrollBy(0, dy);
      computeScrollState(false);
    }
  }

  @Override
  public void onStopNestedScroll(View child) {
    mNestedScrollingParentHelper.onStopNestedScroll(child);
    if (mTotalUnconsumed > 0) {
      if (-getScrollY() >= mHeaderView.getHeight()) {
        computeScrollState(true);
        smoothScrollToHeader();
      } else if (getScrollY() < 0 && -getScrollY() < mHeaderView.getHeight()) {
        computeScrollState(true);
        smoothScrollToReset();
      }
      mTotalUnconsumed = 0;
    }
    stopNestedScroll();
  }
  //<editor-fold>
}