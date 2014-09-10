package com.test.cameron.todolist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.test.cameron.todolist.adapters.TodosAdapter;
import com.test.cameron.todolist.models.Todo;
import com.test.cameron.todolist.utilities.AppController;

import java.util.HashMap;
import java.util.Map;


public class TodoDetailsEdit extends Activity {

    private EditText mId;
    private EditText mTitle;
    private EditText mDescription;
    private int mPosition;
    private View mEditTodoForm;
    private View mEditTodoFormButtons;
    private View mEditTodoProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details_edit);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mId = (EditText) findViewById(R.id.edit_details_id);
        mTitle = (EditText) findViewById(R.id.edit_details_title);
        mDescription = (EditText) findViewById(R.id.edit_details_description);
        mEditTodoForm = findViewById(R.id.edit_todo_form);
        mEditTodoFormButtons = findViewById(R.id.edit_todo_buttons);
        mEditTodoProgress = findViewById(R.id.edit_todo_progess);

        Button editTodo = (Button) findViewById(R.id.edit_todo_button);
        Button deleteTodo = (Button) findViewById(R.id.delete_todo_button);

        Bundle b = getIntent().getExtras();
        String rowId = b.getString("id");
        String rowTitle = b.getString("title");
        String rowDescription = b.getString("description");
        mPosition = b.getInt("position");

        mId.setText(rowId);
        mTitle.setText(rowTitle);

        if (rowDescription.isEmpty()) {
            mDescription.setHint("Description");
        } else {
            mDescription.setText(rowDescription);
        }


        editTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTitle.getText().toString().isEmpty()) {
                    mTitle.setError(null);
                    View focusView = null;
                    mTitle.setError(getString(R.string.error_field_required));
                    focusView = mTitle;
                } else {
                    showProgress(true);
                    editTodo();
                }
            }
        });

        deleteTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                deleteTodo();
            }
        });

    }

    private void editTodo() {
        SharedPreferences sharedPref = getSharedPreferences("appPrefs", this.MODE_PRIVATE);
        String userId = sharedPref.getString("user_id", "");

        String URL = AppController.appApiBaseUrl + "/users/" + userId + "/todos/" + mId.getText().toString();

        StringRequest addTodoRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Todo todo = TodosAdapter.itemList.get(mPosition);
                todo.title = mTitle.getText().toString();
                todo.description = mDescription.getText().toString();

                TodoList.itemsAdapter.notifyDataSetChanged();

                Intent intent = new Intent(getApplicationContext(), TodoList.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.d("ERROR", "error");
            }
        }){
            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params = new HashMap<String, String>();
                params.put("id", mId.getText().toString());
                params.put("title", mTitle.getText().toString());
                params.put("description", mDescription.getText().toString());
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(addTodoRequest);
    }

    private void deleteTodo() {
        SharedPreferences sharedPref = getSharedPreferences("appPrefs", this.MODE_PRIVATE);
        String userId = sharedPref.getString("user_id", "");

        String URL = AppController.appApiBaseUrl + "/users/" + userId + "/todos/" + mId.getText().toString();

        StringRequest addTodoRequest = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                TodosAdapter.itemList.remove(mPosition);
                TodoList.itemsAdapter.notifyDataSetChanged();

                Intent intent = new Intent(getApplicationContext(), TodoList.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Log.d("ERROR", "error");
            }
        });

        AppController.getInstance().addToRequestQueue(addTodoRequest);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEditTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditTodoForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mEditTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mEditTodoProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mEditTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mEditTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_details_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent todoListActivity = new Intent(getApplicationContext(), TodoList.class);
        startActivity(todoListActivity);
        return true;
    }
}
