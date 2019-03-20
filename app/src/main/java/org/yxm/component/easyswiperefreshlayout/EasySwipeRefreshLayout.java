package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.Recycler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

public class EasySwipeRefreshLayout extends FrameLayout {

  public static final String TAG = "EasySwipeRefreshLayout";

  /** 滑动阻尼参数 */
  private static final int SCROLL_RATE = 2;

  /** 滑动状态 初始状态 */
  public static final int STATE_RESET = 0;
  /** 滑动状态 正在下拉 */
  public static final int STATE_PULLING = 1;
  /** 滑动状态 松开可以更新 */
  public static final int STATE_RELEASE_TO_REFRESH = 2;
  /** 滑动状态 正在更新 */
  public static final int STATE_REFRESHING = 3;

  /** header */
  protected View mHeaderView;
  /** scroll状态回调接口 */
  protected ScrollStateLitener mScrollStateListener;
  /** 刷新状态回调 */
  private OnRefreshListener mRefreshListener;
  /** header height */
  private int mHeaderHeight;
  /** 内容view，一般为listview，recyclerview，scrollview等 */
  private View mTargetView;
  /** 记录上次的y值，用于计算滑动距离 */
  private int mLastY;
  /** scroller用来做回弹动画 */
  private Scroller mScroller;
  /** 当前滑动的状态 */
  private int mState;
  /** 是否已经开始拖拽 */
  private boolean mIsBeginDrag = false;


  /**
   * 获取滑动状态接口，在滑动过程中可以持续拿到变化 可以在这里接口中做头部的动画
   */
  public interface ScrollStateLitener {

    /** header height 和 滑动距离的对比 */
    void scrollDuration(int headerHeight, int scrollY);

    /** 获取当前的状态 */
    void state(int state);

  }

  /**
   * 通知开始刷新回调
   */
  public interface OnRefreshListener {

    void onRefresing();
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
    mScroller = new Scroller(getContext());
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mTargetView = getChildAt(0);
    buildHeaderView();
  }

  /**
   * 如果想自己设置HeaderView，重写这个方法，设置header view和scroll listener
   */
  protected void buildHeaderView() {
    if (mHeaderView == null) {
      mHeaderView = new DefaultHeaderLayout(getContext());
      mScrollStateListener = (ScrollStateLitener) mHeaderView;
      addView(mHeaderView);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    for (int i = 0; i < getChildCount(); i++) {
      View childView = getChildAt(i);
      if (childView == mHeaderView) {
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout(0, -mHeaderHeight, mHeaderView.getMeasuredWidth(), 0);
      }
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int touchY = (int) ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastY = touchY;
        mIsBeginDrag = false;
        break;
      case MotionEvent.ACTION_MOVE:
        int dy = touchY - mLastY;
        mLastY = touchY;
        if (getState() == STATE_REFRESHING) {
          break;
        }
        if (dy > 0) {
          // 向下滑动，判断是否滑到顶部，需要拦截
          if (shouldInterceptScrollEvent()) {
            mIsBeginDrag = true;
            // 如果mScroller弹性回退动画正在执行，直接停止
            if (!mScroller.isFinished()) {
              mScroller.abortAnimation();
            }
            scrollBy(0, -dy / SCROLL_RATE);
            // 滑动距离到达header高度
            if (Math.abs(getScrollY()) >= mHeaderHeight) {
              updateScrollState(STATE_RELEASE_TO_REFRESH);
            } else {
              updateScrollState(STATE_PULLING);
            }
            return true;
          }
        } else {
          // 向上滑动，但是当前出于下拉状态
          if (getState() == STATE_PULLING || getState() == STATE_RELEASE_TO_REFRESH) {
            mIsBeginDrag = true;
            scrollBy(0, -dy);
            // 滑动距离到达header高度
            if (Math.abs(getScrollY()) >= mHeaderHeight) {
              updateScrollState(STATE_RELEASE_TO_REFRESH);
            } else {
              updateScrollState(STATE_PULLING);
            }
            // 方向滑动到顶部，释放这次滑动事件
            if (getScrollY() >= 0) {
              mIsBeginDrag = false;
              updateScrollState(STATE_RESET);
              releaseTouchEvent(ev);
              // 释放motion event，这里有必要做这个动作，因为down事件传递有延迟
              scrollTo(0, 0);
            }
            return true;
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        if (getState() == STATE_RELEASE_TO_REFRESH) {
          updateScrollState(STATE_REFRESHING);
          smoothScrollToHeaderHeight();
          if (mRefreshListener != null) {
            mRefreshListener.onRefresing();
          }
        } else if (getState() == STATE_PULLING) {
          updateScrollState(STATE_RESET);
          smoothScrollToReset();
        }
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    // 这里添加判断是为了防止up事件传递给mTargetView形成点击
    // 不能直接在dispatch中return掉up事件，效果不好
    if (mIsBeginDrag) {
      mIsBeginDrag = false;
      return true;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(0, mScroller.getCurrY());
      invalidate();
    }
  }

  /**
   * 判断是否到达tagetView的顶部，需要拦截事件滑出headerview
   */
  private boolean shouldInterceptScrollEvent() {
    if (mTargetView instanceof ListView) {
      View firstView = ((ListView) mTargetView).getChildAt(0);
      if (firstView != null && firstView.getTop() == 0) {
        return true;
      }
    } else if (mTargetView instanceof RecyclerView) {
      RecyclerView recyclerView = (RecyclerView) mTargetView;
      LayoutManager linearLayoutManager = recyclerView.getLayoutManager();
      View firstView = linearLayoutManager.findViewByPosition(0);
      if (firstView != null && firstView.getTop() == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * 更新scroll状态
   */
  private void updateScrollState(int state) {
    mState = state;
    if (mScrollStateListener != null) {
      mScrollStateListener.state(state);
      mScrollStateListener.scrollDuration(mHeaderHeight, getScrollY());
    }
  }

  /**
   * 释放touchevent，通过发送down事件，将事件流结束
   */
  private void releaseTouchEvent(MotionEvent ev) {
    MotionEvent event = MotionEvent.obtain(ev);
    event.setAction(MotionEvent.ACTION_DOWN);
    this.dispatchTouchEvent(event);
  }

  /**
   * 通过scroller缓慢滑动到某处
   */
  private void smoothScrollTo(int from, int to) {
    mScroller.startScroll(0, from, 0, to - from, 500);
    invalidate();
  }

  /**
   * 滑动到最初原始状态
   */
  private void smoothScrollToReset() {
    smoothScrollTo(getScrollY(), 0);
  }

  /**
   * 滑动到header高度，表示正在刷新
   */
  private void smoothScrollToHeaderHeight() {
    smoothScrollTo(getScrollY(), -mHeaderHeight);
  }

  /**
   * 设置刷新回调
   */
  public void setOnRefreshListener(OnRefreshListener listener) {
    mRefreshListener = listener;
  }

  /**
   * 外部调用，通知刷新结束
   */
  public void stopRefresing() {
    updateScrollState(STATE_RESET);
    smoothScrollToReset();
  }

  /**
   * 获取当前state
   */
  private int getState() {
    return mState;
  }

  private static class DefaultHeaderLayout extends FrameLayout implements ScrollStateLitener {

    private TextView mScrollStateText;

    public DefaultHeaderLayout(Context context) {
      super(context);
      init();
    }

    private void init() {
      MarginLayoutParams params = new MarginLayoutParams(LayoutParams.MATCH_PARENT,
          LayoutParams.WRAP_CONTENT);
      setLayoutParams(params);
      setPadding(0, 10, 0, 10);

      mScrollStateText = new TextView(getContext());
      mScrollStateText.setTextSize(30);
      mScrollStateText.setGravity(Gravity.CENTER);
      this.addView(mScrollStateText);
    }

    /**
     * header height 和 滑动距离的对比
     */
    @Override
    public void scrollDuration(int headerHeight, int scrollY) {

    }

    /**
     * 获取当前的状态
     */
    @Override
    public void state(int state) {
      switch (state) {
        case EasySwipeRefreshLayout.STATE_PULLING:
          mScrollStateText.setText("下拉刷新");
          break;
        case EasySwipeRefreshLayout.STATE_RELEASE_TO_REFRESH:
          mScrollStateText.setText("松开可刷新");
          break;
        case EasySwipeRefreshLayout.STATE_REFRESHING:
          mScrollStateText.setText("正在刷新");
          break;
      }
    }
  }
}
