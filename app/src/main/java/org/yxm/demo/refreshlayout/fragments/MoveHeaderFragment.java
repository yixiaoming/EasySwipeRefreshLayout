package org.yxm.demo.refreshlayout.fragments;

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
import java.util.ArrayList;
import org.yxm.demo.refreshlayout.R;
import org.yxm.demo.refreshlayout.adapters.CustomRecyclerAdapter;
import org.yxm.demo.refreshlayout.layout.MoveHeaderRefreshLayout;
import org.yxm.demo.refreshlayout.pojo.Article;
import org.yxm.demo.refreshlayout.repo.ArticleRepo;
import org.yxm.component.refreshlayout.EasySwipeRefreshLayout.OnRefreshListener;

public class MoveHeaderFragment extends Fragment {

  private MoveHeaderRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private CustomRecyclerAdapter mAdapter;

  private ArticleRepo mArticleRepo;

  public static MoveHeaderFragment newInstance() {
    MoveHeaderFragment fragment = new MoveHeaderFragment();
    return fragment;
  }

  public MoveHeaderFragment() {
    mArticleRepo = new ArticleRepo();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = LayoutInflater.from(getContext()).inflate(R.layout.moveheader_fragment,
        container, false);
    initViews(root);
    return root;
  }

  private void initViews(View root) {
    mRefreshLayout = root.findViewById(R.id.refresh_layout);
    mRecyclerView = root.findViewById(R.id.recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new CustomRecyclerAdapter(new ArrayList<Article>());
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.insert(mArticleRepo.getArticles(), 0);
    mAdapter.notifyDataSetChanged();
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            mAdapter.insert(mArticleRepo.getRadomArticle(), 0);
            mRefreshLayout.stopRefreshing();
          }
        }, 3 * 1000);
      }
    });
  }
}
