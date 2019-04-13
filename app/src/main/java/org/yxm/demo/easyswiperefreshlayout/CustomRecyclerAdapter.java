package org.yxm.demo.easyswiperefreshlayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.yxm.demo.easyswiperefreshlayout.pojo.Article;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

  private List<Article> mDatas;

  public CustomRecyclerAdapter(List<Article> mDatas) {
    this.mDatas = mDatas;
  }

  public void insert(Article article, int position) {
    mDatas.add(position, article);
    notifyDataSetChanged();
  }

  public void insert(List<Article> articles, int position) {
    mDatas.addAll(position, articles);
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.custom_item_view, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(itemView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    Article article = mDatas.get(i);
    viewHolder.mTitle.setText(article.title);
    viewHolder.mContent.setText(article.content);
    viewHolder.mTime.setText(article.time);
  }

  @Override
  public int getItemCount() {
    return mDatas.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView mLogo;
    public TextView mTitle;
    public TextView mContent;
    public TextView mTime;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mLogo = itemView.findViewById(R.id.logo);
      mTitle = itemView.findViewById(R.id.title);
      mContent = itemView.findViewById(R.id.content);
      mTime = itemView.findViewById(R.id.time);
    }
  }
}
