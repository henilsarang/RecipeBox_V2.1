package com.viaviweb.recipebox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ActivitySplash extends Activity {

	MyApplication App;
	String str_package;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		JsonUtils.setStatusBarGradiant(ActivitySplash.this);
		App = MyApplication.getAppInstance();

		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
		jsObj.addProperty("method_name", "get_app_details");
		if (JsonUtils.isNetworkAvailable(ActivitySplash.this)) {
			new MyTaskDev(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
		} else {
			showToast(getString(R.string.network_msg));
		}
	}
		@SuppressLint("StaticFieldLeak")
		private class MyTaskDev extends AsyncTask<String, Void, String> {

			String base64;

			private MyTaskDev(String base64) {
				this.base64 = base64;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected String doInBackground(String... params) {
				return JsonUtils.getJSONString(params[0], base64);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (null == result || result.length() == 0) {
					showToast(getString(R.string.no_data_found));
				} else {

					try {
						JSONObject mainJson = new JSONObject(result);
						JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
						JSONObject objJson;
						for (int i = 0; i < jsonArray.length(); i++) {
							objJson = jsonArray.getJSONObject(i);
							if(objJson.has("status")){
								final PrettyDialog dialog = new PrettyDialog(ActivitySplash.this);
								dialog.setTitle(getString(R.string.dialog_error))
										.setTitleColor(R.color.dialog_text)
										.setMessage(getString(R.string.restart_msg))
										.setMessageColor(R.color.dialog_text)
										.setAnimationEnabled(false)
										.setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
											@Override
											public void onClick() {
												dialog.dismiss();
												finish();
											}
										})
										.addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
											@Override
											public void onClick() {
												dialog.dismiss();
												finish();
											}
										});
								dialog.setCancelable(false);
								dialog.show();
							}else {
 							str_package = objJson.getString(Constant.APP_PACKAGE_NAME);

							if (str_package.equals(getPackageName())) {

								if (App.getIsLogin()) {
									Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									finish();
								} else {
									Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
									startActivity(intent);
									finish();
								}

							}}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void showToast (String msg){
			Toast.makeText(ActivitySplash.this, msg, Toast.LENGTH_LONG).show();
		}
	}
