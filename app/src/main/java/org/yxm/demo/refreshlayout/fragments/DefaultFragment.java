package org.yxm.demo.refreshlayout.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.yxm.demo.refreshlayout.R;
import org.yxm.demo.refreshlayout.adapters.CustomRecyclerAdapter;
import org.yxm.demo.refreshlayout.pojo.Article;
import org.yxm.demo.refreshlayout.repo.ArticleRepo;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnRefreshListener;

public class DefaultFragment extends Fragment {

  private EasySwipeRefreshLayout mRefreshlayout;
  private RecyclerView mRecyclerview;
  private CustomRecyclerAdapter mAdapter;
  private ArticleRepo mArticleRepo;

  public DefaultFragment() {
    mArticleRepo = new ArticleRepo();
  }

  public static DefaultFragment newInstance() {
    DefaultFragment fragment = new DefaultFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = LayoutInflater.from(getContext())
        .inflate(R.layout.default_fragment, container, false);
    initViews(root);
    return root;
  }

  private void initViews(View root) {
    mRefreshlayout = root.findViewById(R.id.refresh_layout);
    mRecyclerview = root.findViewById(R.id.recyclerview);
    mRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
    mAdapter = new CustomRecyclerAdapter(new ArrayList<Article>());
    mAdapter.insert(mArticleRepo.getArticles(), 0);
    mRecyclerview.setAdapter(mAdapter);
    mRefreshlayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            mAdapter.insert(mArticleRepo.getRadomArticle(), 0);
            mRefreshlayout.stopRefreshing();
          }
        }, 3 * 1000);
      }
    });
  }
}
