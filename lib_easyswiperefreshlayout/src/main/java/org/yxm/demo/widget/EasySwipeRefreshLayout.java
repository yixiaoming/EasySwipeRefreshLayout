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

  private IStyleStrategy mStrategy;

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

    mStrategy = new MoveHeaderStrategy(this);
  }

  //<editor-fold desc="提供给StyleStrategy使用的方法">
  public View getTargetView(){
    return mTargetView;
  }

  public View getHeaderView(){
    return mHeaderView;
  }

  public OnScrollStateChangeListener getProcessListener() {
    return mProcessListener;
  }

  public OnRefreshListener getOnRefreshListener() {
    return mOnRefreshListener;
  }

  public void setState(int state){
    mState = state;
  }

  public int getState(){
    return mState;
  }
  //<editor-fold>

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
        mStrategy.onLayout();
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
    mStrategy.setOnRefreshListener(listener);
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

  public void smoothScrollTo(int from, int to) {
    mScroller.startScroll(0, from, 0, to - from, 500);
    invalidate();
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(0, mScroller.getCurrY());
      invalidate();
    }
  }

  public void stopRefreshing() {
    mStrategy.smoothScrollToReset();
    mState = RESET;
  }

  /**
   * 处理滑动拖成中状态的修改
   *
   * @param isRelease touch事件release标志
   */
  private void computeScrollState(boolean isRelease) {
    mStrategy.computeScrollState(isRelease);
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
      mStrategy.onNestedPreScroll(dy);
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
      mStrategy.onNestedScroll(dy);
    }
  }

  @Override
  public void onStopNestedScroll(View child) {
    mNestedScrollingParentHelper.onStopNestedScroll(child);
    if (mTotalUnconsumed > 0) {
      mStrategy.onStopNestedScroll();
      mTotalUnconsumed = 0;
    }
    stopNestedScroll();
  }
  //<editor-fold>
}