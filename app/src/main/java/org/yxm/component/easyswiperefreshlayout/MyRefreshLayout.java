package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

public class MyRefreshLayout extends EasySwipeRefreshLayout {


  public MyRefreshLayout(Context context) {
    super(context);
  }

  public MyRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 如果想自己设置HeaderView，重写这个方法，设置header view和scroll listener
   */
  @Override
  protected void buildHeaderView() {
    mHeaderView = LayoutInflater.from(getContext()).inflate(
        R.layout.header_view, this, false
    );
    addView(mHeaderView);
    final TextView mScrollStateText = mHeaderView.findViewById(R.id.pull_notify);

    mScrollStateListener = new ScrollStateLitener() {
      @Override
      public void scrollDuration(int headerHeight, int scrollY) {
        Log.d(TAG, "scrollDuration: height:" + headerHeight + ", scrollY:" + scrollY);
      }

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
    };
  }
}
