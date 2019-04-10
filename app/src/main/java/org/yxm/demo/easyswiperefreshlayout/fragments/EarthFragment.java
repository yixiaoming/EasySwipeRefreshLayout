package org.yxm.demo.easyswiperefreshlayout.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.yxm.demo.easyswiperefreshlayout.MyRecyclerAdapter;
import org.yxm.demo.easyswiperefreshlayout.R;
import org.yxm.demo.widget.EasySwipeRefreshLayout;
import org.yxm.demo.widget.EasySwipeRefreshLayout.OnRefreshListener;

public class EarthFragment extends Fragment {

  private EasySwipeRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private MyRecyclerAdapter mAdapter;

  public static EarthFragment newInstance() {
    EarthFragment fragment = new EarthFragment();
    fragment.setArguments(null);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = LayoutInflater.from(getContext())
        .inflate(R.layout.earth_fragment, container, false);
    initViews(root);
    return root;
  }

  private void initViews(View root) {
    mRefreshLayout = root.findViewById(R.id.refresh_layout);
    mRecyclerView = root.findViewById(R.id.recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    List<String> datas = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      datas.add("item:" + i);
    }
    mAdapter = new MyRecyclerAdapter(datas);
    mRecyclerView.setAdapter(mAdapter);
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
          @Override
          public void run() {
            mAdapter.insert("item:" + new Random(100).nextInt(), 0);
            mRefreshLayout.stopRefreshing();
          }
        }, 3 * 1000);
      }
    });
  }
}
