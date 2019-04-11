package org.yxm.demo.easyswiperefreshlayout.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.yxm.demo.easyswiperefreshlayout.CustomAdapter;
import org.yxm.demo.easyswiperefreshlayout.R;
import org.yxm.demo.easyswiperefreshlayout.RocketRefreshLayout;
import org.yxm.demo.easyswiperefreshlayout.pojo.Article;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnRefreshListener;

public class RocketFragment extends Fragment {

  private RocketRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private CustomAdapter mAdapter;

  public static RocketFragment newInstance() {
    RocketFragment fragment = new RocketFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = LayoutInflater.from(getContext()).inflate(R.layout.rocket_fragment,
        container, false);
    initViews(root);
    return root;
  }

  private void initViews(View root) {
    mRefreshLayout = root.findViewById(R.id.refresh_layout);
    mRecyclerView = root.findViewById(R.id.recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new CustomAdapter(new ArrayList<Article>());
    mRecyclerView.setAdapter(mAdapter);
    for (int i = 0; i < 20; i++) {
      Article article = new Article();
      article.title = "title:" + i;
      article.content = "content:" + i;
      article.time = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
      mAdapter.insert(article, i);
    }
    mAdapter.notifyDataSetChanged();
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            Random random = new Random(100);
            int index = random.nextInt();
            Article article = new Article();
            article.title = "title:" + index;
            article.content = "content:" + index;
            article.time = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
            mAdapter.insert(article, 0);
            mAdapter.notifyDataSetChanged();
            mRefreshLayout.stopRefreshing();
          }
        }, 5 * 1000);
      }
    });
  }
}
