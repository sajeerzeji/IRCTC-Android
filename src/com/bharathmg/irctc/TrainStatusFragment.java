package com.bharathmg.irctc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bharathmg.irctc.utils.HMACGenerator;
import com.bharathmg.irctc.utils.SecureConstants;

import mobi.vserv.android.ads.ViewNotEmptyException;
import mobi.vserv.android.ads.VservController;
import mobi.vserv.android.ads.VservManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by bharathmg on 11/01/14.
 */
public class TrainStatusFragment extends Fragment {

	private VservController controller;
	private EditText train_no_view;
	private EditText journey_date_view;
	private AutoCompleteTextView from_view;
	private AutoCompleteTextView to_view;
	private Spinner class_spinner;
	private Handler handler;
	private Spinner quota_spinner;
	private HashMap<String, String> toStationsByCode, fromStationsByCode;
	private ArrayList<String> classCodes, quotaCodes;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_train_status, container, false);
		handler = new Handler();
		VservManager renderAdManager = VservManager.getInstance(getActivity());
		ViewGroup vservGroup = (ViewGroup) rootView.findViewById(R.id.vserv_ads_group);
		try {
			if (controller == null) {
				controller = renderAdManager.renderAd("7f8ac297", vservGroup);
				controller.setRefresh(30);
			}

		} catch (ViewNotEmptyException e) {
			Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		train_no_view = (EditText) rootView.findViewById(R.id.editText1);
		journey_date_view = (EditText) rootView.findViewById(R.id.editText2);
		from_view = (AutoCompleteTextView) rootView.findViewById(R.id.from_text);
		to_view = (AutoCompleteTextView) rootView.findViewById(R.id.to_text);
		class_spinner = (Spinner) rootView.findViewById(R.id.spinner1);
		quota_spinner = (Spinner) rootView.findViewById(R.id.spinner2);

		classCodes = new ArrayList<String>();
		quotaCodes = new ArrayList<String>();
		ArrayList<String> classes = new ArrayList<String>();
		classes.add("First AC");
		classes.add("Second AC");
		classes.add("Third AC");
		classes.add("3 AC Economy");
		classes.add("AC Chair Car");
		classes.add("First Class");
		classes.add("Sleeper Class");
		classes.add("Second seating");

		classCodes.add("1A");
		classCodes.add("2A");
		classCodes.add("3A");
		classCodes.add("3E");
		classCodes.add("CC");
		classCodes.add("FC");
		classCodes.add("SL");
		classCodes.add("2S");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, classes);
		class_spinner.setAdapter(adapter);

		ArrayList<String> quotas = new ArrayList<String>();
		quotas.add("Tatkal Quota");
		quotas.add("Ladies Quota");
		quotas.add("Defence Quota");
		quotas.add("Foreign Tourist");
		quotas.add("Duty Pass Quota");
		quotas.add("Handicaped Quota");
		quotas.add("Parliament House Quota");
		quotas.add("Lower Berth Quota");
		quotas.add("Yuva Quota");
		quotas.add("GENERAL QUOTA");

		quotaCodes.add("CK");
		quotaCodes.add("LD");
		quotaCodes.add("DF");
		quotaCodes.add("FT");
		quotaCodes.add("DP");
		quotaCodes.add("HP");
		quotaCodes.add("PH");
		quotaCodes.add("SS");
		quotaCodes.add("YU");
		quotaCodes.add("GN");
		ArrayAdapter<String> quotas_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, quotas);
		quota_spinner.setAdapter(quotas_adapter);

		to_view.setThreshold(1);
		to_view.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final String search_key = s.toString();
				handler.removeCallbacksAndMessages(null);
				handler.removeCallbacks(null);
				if (!search_key.isEmpty() && search_key != "") {
					handler.postDelayed(new Runnable() {
						public void run() {

							String hmac_input = "json" + search_key + "1" + SecureConstants.public_key;
							HMACGenerator generator = new HMACGenerator(hmac_input.toLowerCase(), SecureConstants.private_key);
							RequestQueue queue = Volley.newRequestQueue(getActivity());

							String url = "http://pnrbuddy.com/api/station_by_name/name/" + search_key + "/partial/1/format/json/pbapikey/"
									+ SecureConstants.public_key + "/pbapisign/" + generator.generateHMAC();
							JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url, null, toViewPopulate(),
									createMyReqErrorListener());
							queue.add(jsonReq);
						}
					}, 1000);
				}
			}

		});
		from_view.setThreshold(1);
		from_view.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final String search_key = s.toString();
				handler.removeCallbacksAndMessages(null);
				handler.removeCallbacks(null);
				if (!search_key.isEmpty() && search_key != "") {
					handler.postDelayed(new Runnable() {
						public void run() {

							String hmac_input = "json" + search_key + "1" + SecureConstants.public_key;
							HMACGenerator generator = new HMACGenerator(hmac_input.toLowerCase(), SecureConstants.private_key);
							RequestQueue queue = Volley.newRequestQueue(getActivity());

							String url = "http://pnrbuddy.com/api/station_by_name/name/" + search_key + "/partial/1/format/json/pbapikey/"
									+ SecureConstants.public_key + "/pbapisign/" + generator.generateHMAC();
							JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url, null, createMyReqSuccessListener(),
									createMyReqErrorListener());
							queue.add(jsonReq);
						}
					}, 1000);
				}
			}

		});

		return rootView;
	}

	public void getStatus(View v) {
		String train_number = train_no_view.getText().toString();
		String journey_date = journey_date_view.getText().toString();
		String from_text = from_view.getText().toString();
		String to_text = to_view.getText().toString();
		int class_selected = class_spinner.getSelectedItemPosition();
		int quota_selected = quota_spinner.getSelectedItemPosition();
		// tnum,fscode,tscode,date,class,quota,format,pbapikey
		// class,date,format,fscode,pbapikey,quota,tnum,tscode.

		String hmac_input = class_selected + journey_date + "json" + from_text + SecureConstants.public_key + quota_selected + train_number
				+ to_text;
		HMACGenerator generator = new HMACGenerator(hmac_input, SecureConstants.private_key);
		String url = "http://pnrbuddy.com/api/check_avail/tnum/" + train_number + "/fscode/" + fromStationsByCode.get(from_text)
				+ "/tscode/" + toStationsByCode.get(to_text) + "/date/" + journey_date + "/class/" + classCodes.get(class_selected)
				+ "/quota/" + quotaCodes.get(quota_selected) + "/format/json/pbapikey/" + SecureConstants.public_key + "/pbapisign/"
				+ generator.generateHMAC();
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getString("response_code").equals("200")) {
						fromStationsByCode = new HashMap<String, String>();
						ArrayList<String> stations_list = new ArrayList<String>();
						JSONArray stationsList = response.getJSONArray("stations");
						fromStationsByCode.clear();
						for (int i = 0; i < stationsList.length(); i++) {
							JSONObject stationObj = stationsList.getJSONObject(i);
							String full_name = stationObj.getString("name");
							String code_name = stationObj.getString("code");
							String adapter_value = full_name + " (" + code_name + ")";
							fromStationsByCode.put(adapter_value, code_name);
							stations_list.add(adapter_value);
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
								android.R.layout.simple_spinner_dropdown_item, stations_list);
						from_view.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						from_view.showDropDown();
					}
				} catch (Exception e) {
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		};
	}

	private Response.Listener<JSONObject> toViewPopulate() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getString("response_code").equals("200")) {
						ArrayList<String> stations_list = new ArrayList<String>();
						JSONArray stationsList = response.getJSONArray("stations");
						toStationsByCode = new HashMap<String, String>();
						toStationsByCode.clear();
						for (int i = 0; i < stationsList.length(); i++) {
							JSONObject stationObj = stationsList.getJSONObject(i);
							String full_name = stationObj.getString("name");
							String code_name = stationObj.getString("code");
							String adapter_value = full_name + " (" + code_name + ")";
							toStationsByCode.put(adapter_value, code_name);
							stations_list.add(adapter_value);
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
								android.R.layout.simple_spinner_dropdown_item, stations_list);
						to_view.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						to_view.showDropDown();
					}
				} catch (Exception e) {
				}
			}
		};
	}

}
