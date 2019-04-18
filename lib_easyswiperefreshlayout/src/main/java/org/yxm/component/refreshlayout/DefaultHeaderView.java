package org.yxm.component.refreshlayout;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnScrollStateChangeListener;

/**
 * 在EasySwipeRefreshLayout没有设置HeaderView时生效，只有必要的文字状态修改
 */
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
  public void onScrollStateChange(int state, int headerHeight, int scrollY) {
    int progress = scrollY * 100 / headerHeight;
    progress = progress > 100 ? 100 : progress;
    String progressText = progress + "%";
    if (state == EasySwipeRefreshLayout.PULL_TO_REFRESH) {
      mScrollStateText.setText("下拉刷新: " + progressText);
    } else if (state == EasySwipeRefreshLayout.RELEASE_TO_REFRESH) {
      mScrollStateText.setText("松开开始刷新: " + progressText);
    } else if (state == EasySwipeRefreshLayout.REFRESHING) {
      mScrollStateText.setText("正在刷新");
    }
  }
}