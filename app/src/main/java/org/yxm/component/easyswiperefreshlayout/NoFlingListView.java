package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class NoFlingListView extends ListView {


  public NoFlingListView(Context context) {
    super(context, null);
  }

  public NoFlingListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NoFlingListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
      int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,scrollRangeX, scrollRangeY, maxOverScrollX, 50, isTouchEvent);
  }
}