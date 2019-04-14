package org.yxm.demo.easyswiperefreshlayout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.yxm.demo.easyswiperefreshlayout.R;
import org.yxm.demo.easyswiperefreshlayout.pojo.Article;

public class CustomListAdapter extends BaseAdapter {

  List<Article> mDatas;

  public void insert(List<Article> datas, int position) {
    mDatas.addAll(position, datas);
  }

  public void insert(Article data, int position) {
    mDatas.add(position, data);
  }

  public CustomListAdapter(List<Article> mDatas) {
    this.mDatas = mDatas;
  }

  @Override
  public int getCount() {
    return mDatas.size();
  }

  @Override
  public Object getItem(int position) {
    return mDatas.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    Article article = mDatas.get(position);
    if (convertView != null) {
      viewHolder = (ViewHolder) convertView.getTag();
    } else {
      convertView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.custom_item_view, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.mTitle = convertView.findViewById(R.id.title);
      viewHolder.mContent = convertView.findViewById(R.id.content);
      viewHolder.mTime = convertView.findViewById(R.id.time);
      viewHolder.mLogo = convertView.findViewById(R.id.logo);
      convertView.setTag(viewHolder);
    }
    viewHolder.mTitle.setText(article.title);
    viewHolder.mContent.setText(article.content);
    viewHolder.mTime.setText(article.time);

    return convertView;
  }

  private static class ViewHolder {

    public TextView mTitle;
    public TextView mContent;
    public TextView mTime;
    public ImageView mLogo;
  }
}
