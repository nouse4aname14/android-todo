package com.test.cameron.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;


/**
 * An activity representing a list of Todos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TodoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TodoListFragment} and the item details
 * (if present) is a {@link TodoDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link TodoListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TodoListActivity extends Activity
        implements TodoListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        if (findViewById(R.id.todo_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

//            if (mTwoPane) {
//                Bundle arguments = new Bundle();
//                arguments.putString(TodoDetailFragment.ARG_ITEM_ID, "1");
//                TodoDetailFragment fragment = new TodoDetailFragment();
//                fragment.setArguments(arguments);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.todo_detail_container, fragment)
//                        .commit();
//            }

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TodoListFragment) getFragmentManager()
                    .findFragmentById(R.id.todo_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link TodoListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Log.d("ITEM CLICKED", " " + id);
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TodoDetailFragment.ARG_ITEM_ID, id);
            TodoDetailFragment fragment = new TodoDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.todo_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TodoDetailActivity.class);
            detailIntent.putExtra(TodoDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
