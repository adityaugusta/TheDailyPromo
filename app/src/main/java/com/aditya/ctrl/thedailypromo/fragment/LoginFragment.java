package com.aditya.ctrl.thedailypromo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.ctrl.thedailypromo.R;
import com.aditya.ctrl.thedailypromo.activity.MainActivity;
import com.aditya.ctrl.thedailypromo.helper.Config;
import com.aditya.ctrl.thedailypromo.helper.SQLiteHandler;
import com.aditya.ctrl.thedailypromo.helper.SessionManager;
import com.aditya.ctrl.thedailypromo.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginFragment extends Fragment {

    public LoginFragment() {}

    @Bind(R.id.username) EditText i_username;
    @Bind(R.id.password) EditText i_password;
    @Bind(R.id.btn_login) Button login;

    protected SessionManager session;
    protected SQLiteHandler db;
    protected ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String error_msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getActivity());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public class CheckUser extends AsyncTask<String, Void, String> {

        @Override
        protected  void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            int success;
            String username = i_username.getText().toString();
            String password = i_password.getText().toString();
            String oUser = null;
            String error;

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_LOGIN, "GET", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    session.setLogin(true);
                    oUser = json.getString("username");
                    db.addUser(oUser);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return json.getString(TAG_MESSAGE);
                }
                else {
                    error = json.getString("message");
                    error_msg = error;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return oUser;
        }

        @Override
        protected void onPostExecute(String user) {
            pDialog.dismiss();
            if (user != null) {
                Toast.makeText(getActivity(), "Welcome!, "+ user , Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getActivity(), error_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        db = new SQLiteHandler(getActivity());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckUser().execute();
            }
        });

        return view;
    }

}

