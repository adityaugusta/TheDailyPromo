package com.aditya.ctrl.thedailypromo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aditya.ctrl.thedailypromo.R;
import com.aditya.ctrl.thedailypromo.adapter.AdapterArea;
import com.aditya.ctrl.thedailypromo.helper.Config;
import com.aditya.ctrl.thedailypromo.helper.SQLiteHandler;
import com.aditya.ctrl.thedailypromo.helper.SessionManager;
import com.aditya.ctrl.thedailypromo.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@Bind(R.id.btn_logout) ImageView bLogout;
	@Bind(R.id.txt_user) TextView txtUser;
	@Bind(R.id.sp_area) Spinner getSp_area;
	@Bind(R.id.recyclerView) RecyclerView recyclerView_Promo;

	protected SessionManager session;
	protected SQLiteHandler db;
	protected String[] areaNameList;
	protected ProgressDialog pDialog;

	protected ArrayList<HashMap<String, String>> areaList = new ArrayList<>();
	protected ArrayList<HashMap<String, String>> promoList = new ArrayList<>();
	protected ArrayList<String> spinnerList;
	protected ArrayAdapter<String> dataAdapter;
	protected AdapterArea adapterArea;

	JSONParser jsonParser = new JSONParser();

	private static final String PARAM_CODE = "apikey";
	private static final String API_KEY = "test123";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String PARAM_RESULTS = "results";
	private static final String PARAM_AREA = "area";
	private static final String PARAM_PROMO = "promo";
	private static final String AREA_NAME = "area_name";
	private static final String AREA_CODE = "area_code";

	public static final String PROMOLIST = "promo_list";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		session = new SessionManager(getApplicationContext());
		if (!session.isLoggedIn()) {
			logoutUser();
		}

		new FetchDataArea().execute("list");

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		recyclerView_Promo.setLayoutManager(layoutManager);

		db = new SQLiteHandler(this);
		String userdata = db.getUserDetails();
		txtUser.setText(userdata);

		bLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView image = new ImageView(MainActivity.this);
				image.setImageResource(R.drawable.btn_logout);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Log out ?").setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								logoutUser();
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						}).show();
			}
		});

	}

	private void logoutUser() {
		session.setLogin(false);
		db.deleteUsers();
		db.close();
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
		finish();
	}

	class FetchDataArea extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... area_code) {
			int success;
			spinnerList = new ArrayList<>();
			try {
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair(PARAM_CODE, API_KEY));
				params.add(new BasicNameValuePair(PARAM_AREA, area_code[0]));

				JSONObject json = jsonParser.makeHttpRequest(Config.URL_PROMO, "GET", params);
				Log.d("TAG", Config.URL_PROMO.toString());
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					areaList.clear();
					JSONArray results = json.getJSONArray(PARAM_RESULTS);
					for (int i = 0; i < results.length(); i++) {
						JSONObject areas = results.getJSONObject(i);
						HashMap<String, String> map = new HashMap<>();
						String itemAreaName = areas.getString("area-name");
						String itemAreaCode = areas.getString("area-code");
						map.put("area_name", itemAreaName);
						map.put("area_code", itemAreaCode);
						spinnerList.add(itemAreaName);
						areaList.add(map);
					}
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String args) {
			areaNameList = new String[areaList.size()];
			for(int i = 0; i < areaList.size(); i++){
				areaNameList[i] = areaList.get(i).get(AREA_NAME);
			}
			dataAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item, areaNameList);
			dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			getSp_area.setAdapter(dataAdapter);
			getSp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					HashMap<String, String> params = areaList.get(position);
					new FetchDataPromo().execute(params.get(AREA_CODE));
				}
				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
	}

	class FetchDataPromo extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Fetching Promo...");
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... area_code) {
			int success;
			try {
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair(PARAM_CODE, API_KEY));
				params.add(new BasicNameValuePair(PARAM_AREA, area_code[0]));

				JSONObject json = jsonParser.makeHttpRequest(Config.URL_PROMO, "GET", params);
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					promoList.clear();
					String itemPromos = null;
					JSONObject results = json.getJSONObject(PARAM_RESULTS);
					JSONArray promo = results.getJSONArray(PARAM_PROMO);
					for (int i = 0; i < promo.length(); i++) {
						HashMap<String, String> map = new HashMap<>();
						itemPromos = promo.getString(i);
						map.put("promo_list", itemPromos);
						promoList.add(map);
					}
					return itemPromos;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String dataPromo) {
			pDialog.dismiss();
			if (dataPromo != null) {
				adapterArea = new AdapterArea(promoList);
				recyclerView_Promo.setAdapter(adapterArea);
			}
		}
	}
}
