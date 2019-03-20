package org.yxm.component.easyswiperefreshlayout;

import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

  private List<String> mDatas;

  public void insert(String item, int position){
    mDatas.add(position, item);
  }

  public MyRecyclerAdapter(List<String> mDatas) {
    this.mDatas = mDatas;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    TextView tv = (TextView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.itemview, viewGroup, false);
    ViewHolder vh = new ViewHolder(tv);
    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    viewHolder.textView.setText(mDatas.get(i));
  }

  @Override
  public int getItemCount() {
    return mDatas.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView textView;

    public ViewHolder(@NonNull TextView itemView) {
      super(itemView);
      textView = itemView;
    }
  }

}
