package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyRecyclerView extends RecyclerView {

  private static final String TAG = "EasyRefreshLayout";

  private int mLastTouchY = 0;

  public MyRecyclerView(@NonNull Context context) {
    super(context);
  }

  public MyRecyclerView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    int dy = y - mLastTouchY;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Log.e(TAG, "recyclerview dispatchTouchEvent: down");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.e(TAG, "recyclerview dispatchTouchEvent: move:" + dy);
        break;
      case MotionEvent.ACTION_UP:
        Log.e(TAG, "recyclerview dispatchTouchEvent: up");
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    int dy = y - mLastTouchY;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Log.e(TAG, "recyclerview onInterceptTouchEvent: down");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.e(TAG, "recyclerview onInterceptTouchEvent: move:" + dy);
        break;
      case MotionEvent.ACTION_UP:
        Log.e(TAG, "recyclerview onInterceptTouchEvent: up");
        break;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    int dy = y - mLastTouchY;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        Log.e(TAG, "recyclerview onTouchEvent: down");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.e(TAG, "recyclerview onTouchEvent: move:" + dy);
        break;
      case MotionEvent.ACTION_UP:
        Log.e(TAG, "recyclerview onTouchEvent: up");
        break;
    }
    mLastTouchY = y;
    return super.onTouchEvent(ev);
  }
//
//  @Override
//  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//    Log.e(TAG, "recyclerview dispatchNestedPreScroll: " + dx + "," + dy + "," + consumed[0] + ","
//        + consumed[1]);
//    return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
//  }
//
//  @Override
//  public boolean startNestedScroll(int axes) {
//    Log.e(TAG, "recyclerview startNestedScroll: " + axes);
//    return super.startNestedScroll(axes);
//  }
//
//  public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
//    super(context, attrs, defStyle);
//  }
//
//  @Override
//  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//    Log.e(TAG, "recyclerview onStartNestedScroll: " + nestedScrollAxes);
//    return super.onStartNestedScroll(child, target, nestedScrollAxes);
//  }
//
//  @Override
//  public void onNestedScrollAccepted(View child, View target, int axes) {
//    super.onNestedScrollAccepted(child, target, axes);
//    Log.e(TAG, "recyclerview onNestedScrollAccepted: " + axes);
//  }
//
//  @Override
//  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//    super.onNestedPreScroll(target, dx, dy, consumed);
//    Log.e(TAG,
//        "recyclerview onNestedPreScroll: " + dx + "," + dy + "," + consumed[0] + "," + consumed[1]);
//  }
//
//  @Override
//  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
//      int dyUnconsumed) {
//    super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//    Log.e(TAG,
//        "recyclerview onNestedScroll: " + dxConsumed + "," + dyConsumed + "," + dxUnconsumed + ","
//            + dyUnconsumed);
//  }
}
