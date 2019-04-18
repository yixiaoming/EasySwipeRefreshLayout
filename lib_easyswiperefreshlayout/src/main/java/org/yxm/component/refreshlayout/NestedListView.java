package org.yxm.component.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 用作ListView的替代，由于ListView不支持NestedScroll
 * 如果下拉刷新适配ListView的话，需要将ListView放到NestedScrollView中下拉刷新可以生效
 * ListView放到ScrollView下由于绘制原因只能显示一个item，所以这里定义此类用作替代方案
 * 注意：该控件并不具有NestedScroll功能，只是为了让ListView在ScrollView下显示完全
 */
public class NestedListView extends ListView {

  public NestedListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedListView(Context context) {
    super(context);
  }

  public NestedListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }
}