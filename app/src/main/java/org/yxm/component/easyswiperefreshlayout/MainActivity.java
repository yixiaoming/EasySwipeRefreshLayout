package org.yxm.component.easyswiperefreshlayout;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  private static final String[] PREFIXES = new String[]{"a", "b", "c", "d"};

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    List<String> datas = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      datas.add("item:" + i);
    }

//    final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
//    ListView listView = findViewById(R.id.listview);
//    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itemview, datas);
//    listView.setAdapter(adapter);
//    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//      @Override
//      public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            adapter.insert(PREFIXES[new Random().nextInt(PREFIXES.length)], 0);
//            adapter.notifyDataSetChanged();
//            refreshLayout.setRefreshing(false);
//          }
//        }, 3 * 1000);
//      }
//    });
//    listView.setOnItemClickListener(new OnItemClickListener() {
//      @Override
//      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(MainActivity.this, "click:" + position, Toast.LENGTH_SHORT).show();
//      }
//    });

//    final EasyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
//    ListView listView = findViewById(R.id.listview);
//    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itemview, datas);
//    listView.setAdapter(adapter);
//    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//      @Override
//      public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            adapter.insert(PREFIXES[new Random().nextInt(PREFIXES.length)], 0);
//            adapter.notifyDataSetChanged();
//            refreshLayout.stopRefresing();
//          }
//        }, 3 * 1000);
//      }
//    });
//    listView.setOnItemClickListener(new OnItemClickListener() {
//      @Override
//      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(MainActivity.this, "click:" + position, Toast.LENGTH_SHORT).show();
//      }
//    });

    final EasyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
    final MyRecyclerAdapter adapter = new MyRecyclerAdapter(datas);
    MyRecyclerView recyclerView = findViewById(R.id.recyclerview);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    refreshLayout.setOnRefreshListener(new EasyRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            adapter.insert(PREFIXES[new Random().nextInt(PREFIXES.length)], 0);
            adapter.notifyDataSetChanged();
            refreshLayout.stopRefresing();
          }
        }, 5 * 1000);
      }
    });

//    final EasyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
//    final WebView webView = findViewById(R.id.webview);
//    webView.loadUrl("https://www.baidu.com/");
//    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//      @Override
//      public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            webView.reload();
//            refreshLayout.stopRefresing();
//          }
//        }, 3 * 1000);
//      }
//    });
  }
}
