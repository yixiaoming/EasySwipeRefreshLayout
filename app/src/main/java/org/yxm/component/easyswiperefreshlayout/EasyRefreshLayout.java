package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
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
  private NestedScrollingParentHelper mNestedScrollingParentHelper;
  private NestedScrollingChildHelper mNestedScrollingChildHelper;
  private int mTouchSlop;
  private Scroller mScroller;
  private OnScrollStateChangeListener mProcessListener;
  private OnRefreshListener mOnRefreshListener;
  private int mState = RESET;
  private boolean mIsStartNestedScroll = false;

  private int mLastTouchY = 0;
  private int mLastTouchX = 0;
  private int[] mConsumed = new int[2];
  private int[] mParentOffsetInWindow = new int[2];

  /** 刷新状态process获取接口，可做动画 */
  public interface OnScrollStateChangeListener {

    void onScrollStateChange(int state);

    void onScrollProcess(int headerHeight, int scrollY);
  }

  /** 开始刷新接口 */
  public interface OnRefreshListener {

    void onRefresh();
  }

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
    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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

  /**
   * 判断targetview是否可以上滑动
   */
  private boolean canChildScrollUp() {
    return mTargetView.canScrollVertically(1);
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

  private void computeScrollState(boolean isRelease) {
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
      mProcessListener.onScrollProcess(mHeaderView.getHeight(), Math.abs(getScrollY()));
      mProcessListener.onScrollStateChange(mState);
    }
  }

  //<editor-fold desc="View自身touch event事件传递">
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    int dy = y - mLastTouchY;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Log.w(TAG, "dispatchTouchEvent: down");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.w(TAG, "dispatchTouchEvent: move:" + dy);
        break;
      case MotionEvent.ACTION_UP:
        Log.w(TAG, "dispatchTouchEvent: up");
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    int x = (int) ev.getX();
    int y = (int) ev.getY();
    int dx = x - mLastTouchX;
    int dy = y - mLastTouchY;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Log.w(TAG, "onInterceptTouchEvent: down");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.w(TAG, "onInterceptTouchEvent: move:" + dy);
        break;
      case MotionEvent.ACTION_UP:
        Log.w(TAG, "onInterceptTouchEvent: up");
        break;
    }
    mLastTouchX = x;
    mLastTouchY = y;
    return super.onInterceptTouchEvent(ev);
  }
  //</editor-fold>

  //<editor-fold desc="NestedScrollingParent相关">

  @Override
  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    Log.d(TAG, "onStartNestedScroll: " + nestedScrollAxes);
    boolean accept = isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    return accept;
  }

  @Override
  public void onNestedScrollAccepted(View child, View target, int axes) {
    Log.d(TAG, "onNestedScrollAccepted: " + axes);
    mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
  }

  @Override
  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    Log.d(TAG, "onNestedPreScroll: " + dy + "," + getScrollY());
    // 当前正在向上滑动，并且header还在展现，优先隐藏header
    int selfConsumeDy = 0;
    if (dy > 0 && getScrollY() < 0) {
      selfConsumeDy = dy + getScrollY() < 0 ? dy : -getScrollY();
      scrollBy(0, selfConsumeDy);
      computeScrollState(false);
    }
    dispatchNestedPreScroll(dx, dy - selfConsumeDy, mConsumed, null);
    Log.d(TAG, "onNestedPreScroll: " + dy + "," + mConsumed[1]);

    consumed[0] = mConsumed[0];
    consumed[1] = mConsumed[1] + selfConsumeDy;

    // 向下滑动，并且targetview无法再向下滑动
//    if (dy < 0 && !canChildScrollDown()) {
//      if (!mScroller.isFinished()) {
//        mScroller.abortAnimation();
//      }
//      mIsStartNestedScroll = true;
//      scrollBy(0, dy);
//      if (mState != REFRESHING) {
//        computeScrollState(false);
//      }
//    }
//    // 向上滑动，但是此时header还未隐藏，先滑动header消耗dy
//    else if (dy > 0 && getScrollY() < 0) {
//      int realComsmedY = 0;
//      if (dy + getScrollY() <= 0) {
//        scrollBy(0, dy);
//        realComsmedY = dy;
//      } else {
//        // 解决快速方向滑动导致整个layout都向上滑动问题
//        scrollBy(0, -getScrollY());
//        realComsmedY = -getScrollY();
//      }
//      if (mState != REFRESHING) {
//        computeScrollState(false);
//      } else {
//        // 向上滑动，如果正在refreshing的话，隐藏headerview
//        mIsStartNestedScroll = true;
//      }
//      consumed[1] = realComsmedY;
//      dispatchNestedPreScroll(dx, dy, consumed, null);
//    }
  }

  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed) {
    // 下拉时，先问parent需要消耗的touch事件
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
        mParentOffsetInWindow);
    Log.d(TAG,
        "onNestedScroll: " + dyConsumed + "," + dyUnconsumed + "," + mParentOffsetInWindow[1]);
    int dy = dyUnconsumed + mParentOffsetInWindow[1];
    if (dy < 0 && !canChildScrollDown()) {
      scrollBy(0, dy);
      computeScrollState(false);
    }
  }

  @Override
  public void onStopNestedScroll(View child) {
    Log.d(TAG, "onStopNestedScroll: ");
    super.onStopNestedScroll(child);
    if (-getScrollY() >= mHeaderView.getHeight()) {
      smoothScrollToHeader();
      if (mOnRefreshListener != null) {
        mOnRefreshListener.onRefresh();
      }
    } else if (getScrollY() < 0 && -getScrollY() < mHeaderView.getHeight()) {
      smoothScrollToReset();
    }
  }
  //<editor-fold>
}