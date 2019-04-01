package org.yxm.component.easyswiperefreshlayout;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.yxm.component.easyswiperefreshlayout.EasyRefreshLayout.OnRefreshListener;

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

    final EasyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
    ListView listView = findViewById(R.id.listview);
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itemview, datas);
    listView.setAdapter(adapter);
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            adapter.insert(PREFIXES[new Random().nextInt(PREFIXES.length)], 0);
            adapter.notifyDataSetChanged();
            refreshLayout.stopRefresing();
          }
        }, 3 * 1000);
      }
    });
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(MainActivity.this, "click:" + position, Toast.LENGTH_SHORT).show();
      }
    });


//    final EasyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
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
//        }, 10 * 1000);
//      }
//    });
  }
}
