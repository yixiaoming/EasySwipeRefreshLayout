package org.yxm.component.easyswiperefreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    List<String> datas = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      datas.add("item:" + i);
    }
    ListView listView = findViewById(R.id.listview);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.itemview, datas);
    listView.setAdapter(adapter);
  }
}
