package com.test.cameron.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.cameron.todolist.adapters.TodosAdapter;
import com.test.cameron.todolist.models.Todo;
import com.test.cameron.todolist.onboarding.LoginActivity;

import java.util.ArrayList;


public class TodoList extends Activity {

    private ListView mListView;
    public static ArrayAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_todo_list_view);

        itemsAdapter = new TodosAdapter(TodosAdapter.itemList, this);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(itemsAdapter);
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), TodoDetailsEdit.class);
                Bundle b = new Bundle();

                b.putString("title", TodosAdapter.itemList.get(position).title);
                b.putString("description", TodosAdapter.itemList.get(position).description);
                b.putString("id", TodosAdapter.itemList.get(position).id);
                b.putInt("position", position);
                intent.putExtras(b);

                startActivity(intent);
                finish();

            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences sharedPref = getSharedPreferences("appPrefs", this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                TodosAdapter.itemList.clear();
                itemsAdapter.notifyDataSetChanged();

                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);

                return true;
            default:
                Intent intent = new Intent(getApplicationContext(), TodoDetails.class);
                startActivity(intent);
                return super.onOptionsItemSelected(item);
        }

    }


}
