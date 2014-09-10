package com.test.cameron.todolist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.test.cameron.todolist.adapters.TodosAdapter;
import com.test.cameron.todolist.models.Todo;
import com.test.cameron.todolist.utilities.AppController;
import com.test.cameron.todolist.utilities.GsonRequestClass;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class TodoDetails extends Activity {

    private EditText mTitleView;
    private EditText mDescriptionView;
    private View mAddTodoProgress;
    private View mAddTodoForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        mTitleView = (EditText) findViewById(R.id.details_title);
        mDescriptionView = (EditText) findViewById(R.id.details_description);
        mAddTodoForm = findViewById(R.id.add_todo_form);
        mAddTodoProgress = findViewById(R.id.add_todo_progess);

        Button addTodoButton = (Button) findViewById(R.id.add_todo);
        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTitleView.setError(null);

                View focusView = null;

                if (TextUtils.isEmpty(mTitleView.getText().toString())) {
                    mTitleView.setError(getString(R.string.error_field_required));
                    focusView = mTitleView;
                } else {
                    showProgress(true);
                    addTodo();
                }


            }
        });

    }

    private void addTodo() {
        SharedPreferences sharedPref = getSharedPreferences("appPrefs", this.MODE_PRIVATE);
        String userId = sharedPref.getString("user_id", "");

        String URL = AppController.appApiBaseUrl + "/users/" + userId + "/todos";

        StringRequest addTodoRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Integer totalTodos = TodosAdapter.itemList.size();
                String todoPosition = "0";

                if (totalTodos > 0) {
                    todoPosition = TodosAdapter.itemList.get(totalTodos - 1).id + 1;
                } else {
                    todoPosition = "0";
                }

                Todo todo = new Todo(
                        todoPosition,
                        mTitleView.getText().toString(),
                        mDescriptionView.getText().toString()
                );

                TodosAdapter.prepend(TodosAdapter.itemList, todo);

                Intent intent = new Intent(getApplicationContext(), TodoList.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "asdasd");
            }
        }){
            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params = new HashMap<String, String>();
                params.put("title", mTitleView.getText().toString());
                params.put("description", mDescriptionView.getText().toString());
                return params;
            }

        };


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

            mAddTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddTodoForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mAddTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddTodoProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAddTodoProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddTodoForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
