package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

public class EasySwipeRefreshLayout extends ViewGroup {

  public static final String TAG = "EasySwipeRefreshLayout";

  private View mHeaderView;
  private TextView mText;
  private int mHeaderHeight;

  private View mTargetView;

  private int mLastY;

  private int mTouchSlop;
  private Scroller mScroller;
  private int mState;
  private ScrollStateLitener mScrollStateListener = new ScrollStateLitener() {
    @Override
    public void scrollDuration(int headerHeight, int scrollY) {

    }

    @Override
    public void state(int state) {
      if (state == STATE_PULLING) {
        mText.setText("下拉刷新");
      } else if (state == STATE_RELEASE_TO_REFRESH) {
        mText.setText("松开开始刷新");
      } else if (state == STATE_REFRESHING) {
        mText.setText("正在刷新");
      }
    }
  };

  private static final int SCROLL_RATE = 2;

  /* 滑动状态常量 */
  private static final int STATE_RESET = 0;
  private static final int STATE_PULLING = 1;
  private static final int STATE_RELEASE_TO_REFRESH = 2;
  private static final int STATE_REFRESHING = 3;

  public interface ScrollStateLitener {

    void scrollDuration(int headerHeight, int scrollY);

    void state(int state);
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
    mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    mScroller = new Scroller(getContext());
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (mHeaderView == null) {
      mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.header_view, this, false);
      mText = mHeaderView.findViewById(R.id.pull_notify);
      addView(mHeaderView);
    }
    mTargetView = getChildAt(0);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Log.d(TAG, "onSizeChanged: ");
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
      View childView = getChildAt(i);
      if (childView == mHeaderView) {
        mHeaderHeight = childView.getMeasuredHeight();
        mHeaderView.layout(0, -mHeaderHeight, childView.getMeasuredWidth(), 0);
        contentHeight += mHeaderHeight;
      } else {
        childView.layout(0, contentHeight, childView.getMeasuredHeight(),
            contentHeight + childView.getMeasuredHeight());
        if (i < getChildCount()) {
          contentHeight += childView.getMeasuredHeight();
        }
      }
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastY = (int) ev.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        int y = (int) ev.getY();
        int dy = y - mLastY;
        mLastY = y;
        if (getState() == STATE_REFRESHING) {
          break;
        }
        if (dy > 0) {
          // 向下滑动，判断是否滑到顶部，需要拦截
          if (shouldInterceptScrollEvent()) {
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
            return false;
          }
        } else {
          // 向上滑动，但是当前出于下拉状态
          if (getState() == STATE_PULLING || getState() == STATE_RELEASE_TO_REFRESH) {
            scrollBy(0, -dy);
            // 滑动距离到达header高度
            if (Math.abs(getScrollY()) >= mHeaderHeight) {
              updateScrollState(STATE_RELEASE_TO_REFRESH);
            } else {
              updateScrollState(STATE_PULLING);
            }
            // 方向滑动到顶部，释放这次滑动事件
            if (getScrollY() >= 0) {
              releaseTouchEvent(ev);
              scrollTo(0, 0); // 释放motion event，这里有必要做这个动作，因为时间传递有延迟
            }
            return false;
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        if (getState() == STATE_RELEASE_TO_REFRESH) {
          smoothScrollTo(getScrollY(), -mHeaderHeight);
          updateScrollState(STATE_REFRESHING);
        } else if (getState() == STATE_PULLING) {
          smoothScrollTo(getScrollY(), 0);
          updateScrollState(STATE_RESET);
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(0, mScroller.getCurrY());
      invalidate();
    }
  }

  private boolean shouldInterceptScrollEvent() {
    if (mTargetView instanceof ListView) {
      View firstVisibleItemView = ((ListView) mTargetView).getChildAt(0);
      if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
        return true;
      }
    }
    return false;
  }

  private void updateScrollState(int state) {
    mState = state;
    if (mScrollStateListener != null) {
      mScrollStateListener.state(state);
      mScrollStateListener.scrollDuration(mHeaderHeight, getScrollY());
    }
  }

  private void releaseTouchEvent(MotionEvent ev) {
    MotionEvent event = MotionEvent.obtain(ev);
    event.setAction(MotionEvent.ACTION_DOWN);
    this.dispatchTouchEvent(event);
  }

  private void smoothScrollTo(int from, int to) {
    mScroller.startScroll(0, from, 0, to - from, 500);
    invalidate();
  }

  private int getState() {
    return mState;
  }
}
