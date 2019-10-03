package ipa.rmgppapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ipa.rmgppapp.R;
import ipa.rmgppapp.adapter.CustomSpinnerAdapter;
import ipa.rmgppapp.helper.Endpoints;

public class LoginActivity extends AppCompatActivity {

    EditText eTSuperVisorId;
    Button buttonContinue;
    Spinner spinnerLine, spinnerSection, spinnerBuilding, spinnerUnit;
    RequestQueue queue;
    ArrayList<Integer> lineData, buildingData, unitData;
    Snackbar snackbar;
    LinearLayout rootLayout;
    String[] sectionArr = {"Full Line", "Front Part", "Back Part", "Output", "Input"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eTSuperVisorId = findViewById(R.id.editTextSuperVisorId);
        spinnerLine = findViewById(R.id.spinnerLine1);
        spinnerSection = findViewById(R.id.spinnerSection);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        rootLayout = findViewById(R.id.rootLayout);
        buttonContinue = findViewById(R.id.buttonContinue);

        lineData = new ArrayList<Integer>();
        buildingData = new ArrayList<Integer>();
        unitData = new ArrayList<>();
        queue = Volley.newRequestQueue(this);

        if (internetConnected()) {
            checkFactoryCode();
            getBuildingData();
            if (snackbar != null) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
            }
        } else {
            showSnackBar();
        }

        spinnerSection.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sectionArr));

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
                String factoryCode = factoryPref.getString("factoryCode", "");

                String url = Endpoints.GET_BUILDING_DATA + factoryCode + "/" + buildingData.get(position);
                Log.i("urlUnitData", url);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                unitData.clear();
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        unitData.add(jsonArray.getJSONObject(i).getInt("unit"));
                                    }
                                } catch (JSONException e) {
                                    Log.e("DeviceCheckErr", e.toString());
                                }
                                CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(unitData, LoginActivity.this);
                                spinnerUnit.setAdapter(spinnerAdapter);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        buttonContinue.setEnabled(false);
                        Log.e("FactoryCodeErr", error.toString());
                    }
                });
                queue.add(jsonObjectRequest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
                String factoryCode = factoryPref.getString("factoryCode", "");
                int positionOfBuilding = spinnerBuilding.getSelectedItemPosition();
                String url = Endpoints.GET_BUILDING_DATA + factoryCode + "/" + buildingData.get(positionOfBuilding)  + "/" + unitData.get(position);

                Log.i("urlLineData", url);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                lineData.clear();
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        lineData.add(jsonArray.getJSONObject(i).getInt("line"));
                                    }

                                } catch (JSONException e) {
                                    Log.e("DeviceCheckErr", e.toString());
                                }
                                CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(lineData, LoginActivity.this);
                                spinnerLine.setAdapter(spinnerAdapter);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        buttonContinue.setEnabled(false);
                        Log.e("FactoryCodeErr", error.toString());
                    }
                });
                queue.add(jsonObjectRequest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBuildingData() {

        SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
        String factoryCode = factoryPref.getString("factoryCode", "");

        String url = Endpoints.GET_BUILDING_DATA + factoryCode;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        buildingData.clear();
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                buildingData.add(jsonArray.getJSONObject(i).getInt("building"));
                            }
                        } catch (JSONException e) {
                            Log.e("DeviceCheckErr", e.toString());
                        }
                        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(buildingData, LoginActivity.this);
                        spinnerBuilding.setAdapter(spinnerAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                buttonContinue.setEnabled(false);
                Log.e("FactoryCodeErr", error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void checkFactoryCode() {
        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.i("DeviceId", deviceId);
        String url = "https://beta.rmgppapp.com/api/checkValidFactory/" + deviceId;

        Log.i("urlDeviceCheck", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String factoryCode = jsonObject.getString("data");
                            SharedPreferences.Editor factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE).edit();
                            factoryPref.putString("factoryCode", factoryCode);
                            factoryPref.commit();

                            Log.i("factoryCode", factoryCode);
                            if (!factoryCode.contains("Data")) {
                                Toast.makeText(LoginActivity.this, "You are an authorized user. Welcome!", Toast.LENGTH_SHORT).show();
                            } else {
                                buttonContinue.setEnabled(false);
                                Toast.makeText(LoginActivity.this, "You are not an authorized user.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("DeviceCheckErr", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                buttonContinue.setEnabled(false);
                Log.e("FactoryCodeErr", error.toString());
                Toast.makeText(LoginActivity.this, "You are not an authorized user.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void cancelProcess(View view) {
        finish();
    }

    public void continueProcess(View view) {
        int id = view.getId();

        if (id == R.id.buttonContinue) {
            boolean error = false;
            String supervisorId = eTSuperVisorId.getText().toString();
            String lineNo = spinnerLine.getSelectedItem()+"";

            if (supervisorId.isEmpty()) {
                eTSuperVisorId.setError("Supervisor Id is missing!");
                error = true;
            }

            if (error) {
                Toast.makeText(this, "Insert all valid information", Toast.LENGTH_LONG).show();
            } else {
                if (internetConnected()) {
                    checkValidSupervisor(supervisorId, lineNo);
                }
            }
        }
    }

    private void checkValidSupervisor(final String supervisorId, final String lineNo) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
        String factoryCode = factoryPref.getString("factoryCode", "");
        String url = Endpoints.CHECK_SUPERVISOR_URL + "?supervisorId=" + supervisorId + "&factoryCode=" + factoryCode;
        Log.i("urlSupervisor", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SuperVisorResponse", response.toString());
                if (response.contains("SUCCESS")) {
                    SharedPreferences.Editor editor = getSharedPreferences("supervisor", MODE_PRIVATE).edit();
                    editor.putString("supervisorId", supervisorId);
                    editor.putString("lineNo", String.valueOf(lineData.get(spinnerLine.getSelectedItemPosition())));
                    editor.putString("buildingNo", String.valueOf(buildingData.get(spinnerBuilding.getSelectedItemPosition())));
                    editor.putString("unitNo", String.valueOf(unitData.get(spinnerUnit.getSelectedItemPosition())));
                    editor.putString("sectionNo", String.valueOf(spinnerSection.getSelectedItem()));
                    Log.i("editordata", String.valueOf(lineData.get(spinnerLine.getSelectedItemPosition())));
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, StyleListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "You are not a valid supervisor!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckSuperVisor", error.toString());
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 1000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(stringRequest);
    }

    private boolean internetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void showSnackBar() {
        snackbar = Snackbar
                .make(rootLayout, "Internet is not connected!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Connect", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivityForResult(settingsIntent, 9003);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9003) {
            if (internetConnected()) {

            } else {
                showSnackBar();
            }
        }
    }

    public void refreshLines(View view) {
        if (internetConnected()) {

            getBuildingData();

            if (snackbar != null) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
            }
        } else {
            showSnackBar();
        }
    }
}
