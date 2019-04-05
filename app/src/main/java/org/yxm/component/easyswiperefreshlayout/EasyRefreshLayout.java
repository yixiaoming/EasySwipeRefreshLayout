package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class EasyRefreshLayout extends ViewGroup
    implements NestedScrollingParent, NestedScrollingChild {

  private static final String TAG = "EasyRefreshLayout";

  public static final int RESET = 0;
  public static final int PULL_TO_REFRESH = 1;
  public static final int RELEASE_TO_REFRESH = 2;
  public static final int REFRESHING = 3;

  protected View mHeaderView;
  private View mTargetView;
  private NestedScrollingParentHelper mNestedScrollParentHelper;
  private NestedScrollingChildHelper mNestedScrollChildHelper;
  private int mTouchSlop;
  private Scroller mScroller;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mRefreshListener;
  private int mState = RESET;
  private boolean mIsStartNestedScroll = false;

  public EasyRefreshLayout(Context context) {
    this(context, null);
  }

  public EasyRefreshLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EasyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mNestedScrollParentHelper = new NestedScrollingParentHelper(this);
    mNestedScrollChildHelper = new NestedScrollingChildHelper(this);
    mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    mScroller = new Scroller(getContext());
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

  /**
   * 设置下拉process回调
   */
  public void setProcessListener(OnScrollStateChangeListener listener) {
    mProcessListener = listener;
  }

  /**
   * 设置刷新开始litener
   */
  public void setOnRefreshListener(OnRefreshListener listener) {
    mRefreshListener = listener;
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
   * 判断targetview是否还可以下拉
   */
  private boolean canChildScrollDown() {
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

  public void stopRefresing() {
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

  @Override
  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    Log.d(TAG, "onStartNestedScroll: " + target.getClass()
        .getSimpleName() + "," + nestedScrollAxes);
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedScrollAccepted(View child, View target, int axes) {
    Log.d(TAG, "onNestedScrollAccepted: " + axes);
    mNestedScrollParentHelper.onNestedScrollAccepted(child, target, axes);
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
  }

  @Override
  public void onStopNestedScroll(View child) {
    Log.d(TAG, "onStopNestedScroll: ");
    super.onStopNestedScroll(child);
    if (mIsStartNestedScroll) {
      smoothScrollToReset();
      mIsStartNestedScroll = false;
      if (mState == RELEASE_TO_REFRESH) {
        smoothScrollToHeader();
        computeScrollState(true);
      }
    }
  }

  @Override
  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    Log.d(TAG, "onNestedPreScroll: " + dx + "," + dy + "," + getScrollY());
    // 向下滑动，并且targetview无法再向下滑动
    if (dy < 0 && !canChildScrollDown()) {
      if (!mScroller.isFinished()) {
        mScroller.abortAnimation();
      }
      mIsStartNestedScroll = true;
      scrollBy(0, dy);
      if (mState != REFRESHING) {
        computeScrollState(false);
      }
    }
    // 向上滑动，但是此时header还未隐藏，先滑动header消耗dy
    else if (dy > 0 && getScrollY() < 0) {
      int realComsmedY = 0;
      if (dy + getScrollY() <= 0) {
        scrollBy(0, dy);
        realComsmedY = dy;
      } else {
        // 解决快速方向滑动导致整个layout都向上滑动问题
        scrollBy(0, -getScrollY());
        realComsmedY = -getScrollY();
      }
      if (mState != REFRESHING) {
        computeScrollState(false);
      } else {
        // 向上滑动，如果正在refreshing的话，隐藏headerview
        mIsStartNestedScroll = true;
      }
      consumed[1] = realComsmedY;
      dispatchNestedPreScroll(dx, dy, consumed, null);
    }
  }

  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed) {
    Log.d(TAG, "onNestedScroll: " + dxConsumed + "," + dyConsumed + "," + dxUnconsumed + ","
        + dyUnconsumed);
    super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
  }

  private void computeScrollState(boolean isRelease) {
    if (mProcessListener != null) {
      if (-getScrollY() >= mHeaderView.getHeight()) {
        mState = RELEASE_TO_REFRESH;
      } else if (-getScrollY() > 0 && -getScrollY() < mHeaderView.getHeight()) {
        mState = PULL_TO_REFRESH;
      }
      if (isRelease && mState == RELEASE_TO_REFRESH) {
        mState = REFRESHING;
        mRefreshListener.onRefresh();
      }
      mProcessListener.onScrollProcess(mHeaderView.getHeight(), Math.abs(getScrollY()));
      mProcessListener.onScrollStateChange(mState);
    }
  }

  public interface OnScrollStateChangeListener {

    void onScrollStateChange(int state);

    void onScrollProcess(int headerHeight, int scrollY);
  }

  public interface OnRefreshListener {

    void onRefresh();
  }
}