package org.yxm.component.easyswiperefreshlayout;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.yxm.component.easyswiperefreshlayout.EasySwipeRefreshLayout.OnRefreshListener;

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
    ListView listView = findViewById(R.id.listview);
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(MainActivity.this, "position:"+position, Toast.LENGTH_SHORT).show();
      }
    });
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itemview, datas);
    listView.setAdapter(adapter);

    final MyRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresing() {
        adapter.insert(PREFIXES[new Random().nextInt(PREFIXES.length)], 0);
        adapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            refreshLayout.stopRefresing();
          }
        }, 1 * 1000);
      }
    });
  }
}
