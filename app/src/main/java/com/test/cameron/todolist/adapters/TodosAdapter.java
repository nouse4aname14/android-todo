package com.test.cameron.todolist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.cameron.todolist.R;
import com.test.cameron.todolist.models.Todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TodosAdapter extends ArrayAdapter<Todo> {
    public static ArrayList<Todo> itemList;
    private Context context;

    public TodosAdapter(ArrayList<Todo> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    static class ViewHolder {
        public TextView text;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.activity_todo_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.todo_list_item_title);

            rowView.setTag(viewHolder);

        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = this.itemList.get(position).title;
        holder.text.setText(s);

        return rowView;

//        Log.d("TITLE", itemList.get(position).title);
//
//        View v = convertView;
//        if (v == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.activity_todo_list_item, parent, false);
//        }
//
//        TextView text = (TextView) v.findViewById(R.id.todo_list_item_title);
//        text.setText(itemList.get(position).title);
//
//        return v;

    }



    public AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String item = ((TextView)view).getText().toString();
            Log.d("TEXT", item);

        }
    };

    public static void prepend(ArrayList<Todo> list, final Todo first) {
        list.add(0, first);
    }

}
