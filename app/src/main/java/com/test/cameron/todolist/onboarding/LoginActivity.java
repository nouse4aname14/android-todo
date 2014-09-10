package com.test.cameron.todolist.onboarding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.test.cameron.todolist.R;
import com.test.cameron.todolist.TodoList;
import com.test.cameron.todolist.adapters.TodosAdapter;
import com.test.cameron.todolist.models.Todo;
import com.test.cameron.todolist.utilities.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private GetUsersTodoList mTodoListTask = null;

    public RequestQueue mRequestQueue;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("appPrefs", this.MODE_PRIVATE);
        String token = sharedPref.getString("auth_token", "");

        setContentView(R.layout.activity_login);

        if (!token.isEmpty()) {
            mTodoListTask = new GetUsersTodoList(sharedPref.getString("user_id", ""));
            mTodoListTask.execute((Void) null);
        }



        mRequestQueue = Volley.newRequestQueue(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(Boolean register) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, getApplicationContext(), register);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }




    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mEmail;
        private String mPassword;
        private Context mContext;
        private Boolean mRegister;
        private String mUserId;

        UserLoginTask(String email, String password, Context context, Boolean register) {
            mEmail = email;
            mPassword = password;
            mContext = context;
            mRegister = register;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String URL;

            if (mRegister) {
                URL = AppController.appApiBaseUrl + "/users";
            } else {
                URL = AppController.appApiBaseUrl + "/login";
            }

            Map<String, String>  loginCredentials = new HashMap<String, String>();
            loginCredentials.put("email", mEmail);
            loginCredentials.put("password", mPassword);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(loginCredentials),
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {

                            SharedPreferences sharedPref = getSharedPreferences("appPrefs", mContext.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();

                            try {
                                editor.putString("auth_token", response.getString("auth_token"));
                                editor.putString("user_id", response.getString("user_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.commit();

                            String token = sharedPref.getString("auth_token", "");
                            mUserId = sharedPref.getString("user_id", "");

                            mTodoListTask = new GetUsersTodoList(mUserId);
                            mTodoListTask.execute((Void) null);

//                            Log.d("TOKEN", token);
                            Log.d("ID", mUserId);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (mRegister) {
                                Toast.makeText(mContext, "There was an error while registering.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, "The email submitted does not exist.", Toast.LENGTH_LONG).show();
                            }
                            showProgress(false);

                        }
                    }
            );

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(postRequest);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class GetUsersTodoList extends AsyncTask<Void, Void, Boolean> {

        private String mUserId;
        private Context mContext;

        GetUsersTodoList(String userId) {
            mUserId = userId;
//            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String URL = AppController.appApiBaseUrl + "/users/" + mUserId + "/todos";
            Log.d("URL", URL);

            JsonArrayRequest postRequest = new JsonArrayRequest(URL,
                    new Response.Listener<JSONArray>()
                    {
                        @Override
                        public void onResponse(JSONArray response) {

                            ArrayList<Todo> todos = new ArrayList<Todo>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    Todo todo = new Todo(
                                            response.getJSONObject(i).get("id").toString(),
                                            response.getJSONObject(i).get("title").toString(),
                                            response.getJSONObject(i).get("description").toString()
                                    );
                                    todo.id = response.getJSONObject(i).get("id").toString();
                                    todo.title = response.getJSONObject(i).get("title").toString();
                                    todo.description = response.getJSONObject(i).get("description").toString();

                                    todos.add(todo);

                                    Log.d("REPSONSE", response.getJSONObject(i).get("title").toString());
                                    Log.d("REPSONSE", response.getJSONObject(i).get("description").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            TodosAdapter.itemList = todos;
                            try {
                                TodoList.itemsAdapter.notifyDataSetChanged();
                            } catch (NullPointerException e){

                            }


                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR", error.toString() + " ");
//                            Toast.makeText(mContext, "There was an error", Toast.LENGTH_LONG).show();
                        }


                    }
            );

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(postRequest);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTodoListTask = null;

            if (success) {
                Intent intent = new Intent(getApplicationContext(), TodoList.class);
                startActivity(intent);
            }

        }

        @Override
        protected void onCancelled() {
            mTodoListTask = null;
            showProgress(false);
        }
    }
}



