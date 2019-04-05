package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.yxm.component.easyswiperefreshlayout.EasyRefreshLayout.OnScrollStateChangeListener;

public class DefaultHeaderView extends FrameLayout implements
    OnScrollStateChangeListener {

  private TextView mScrollStateText;

  public DefaultHeaderView(Context context) {
    super(context);
    init();
  }

  private void init() {
    MarginLayoutParams params = new MarginLayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT);
    setLayoutParams(params);

    mScrollStateText = new TextView(getContext());
    mScrollStateText.setTextSize(20);
    mScrollStateText.setGravity(Gravity.CENTER);
    mScrollStateText.setPadding(0, 80, 0, 80);
    addView(mScrollStateText);
  }

  @Override
  public void onScrollStateChange(int state) {
    if (state == EasyRefreshLayout.PULL_TO_REFRESH) {
      mScrollStateText.setText("下拉刷新");
    } else if (state == EasyRefreshLayout.RELEASE_TO_REFRESH) {
      mScrollStateText.setText("松开开始刷新");
    } else if (state == EasyRefreshLayout.REFRESHING) {
      mScrollStateText.setText("正在刷新");
    }
  }

  @Override
  public void onScrollProcess(int headerHeight, int scrollY) {
  }
}