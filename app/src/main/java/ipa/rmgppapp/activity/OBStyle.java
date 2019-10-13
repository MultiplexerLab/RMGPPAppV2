package ipa.rmgppapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ipa.rmgppapp.R;
import ipa.rmgppapp.helper.Endpoints;

public class OBStyle extends AppCompatActivity {

    EditText SMV, buyer, item, manpower;
    AutoCompleteTextView styleNo;
    ArrayAdapter<String> adapter1;
    ArrayList<String> styleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obstyle);

        styleNo = findViewById(R.id.editTextStyleOB);
        SMV = findViewById(R.id.editTextSMVOB);
        buyer = findViewById(R.id.editTextBuyerOB);
        item = findViewById(R.id.editTextItemOB);
        manpower = findViewById(R.id.editTextManPower);
        styleList = new ArrayList<>();

        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, styleList);
        styleNo.setAdapter(adapter1);

        getAllStyles();
    }

    public void getOrderListData(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Endpoints.GET_STYLE_DETAILS + "?tag=Style_OB" + "&val=" + styleNo.getText().toString();
        url = url.replace(" ", "%20");
        Log.i("Url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        buyer.setText(jsonObject.getString("buyer"));
                        SMV.setText(jsonObject.getString("smv"));
                        item.setText(jsonObject.getString("item"));
                        Log.i("OB Data", jsonObject.toString());
                    }
                } catch (JSONException e) {
                    Log.e("JSONExceptionErr", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PlanningError", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }

    public void saveOBStyleData(View view) {
        Toast.makeText(OBStyle.this, "Successfully Inserted!", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = getSharedPreferences("supervisor", MODE_PRIVATE).edit();
        editor.putString("styleNoOB", styleNo.getText().toString());
        editor.commit();
        finish();
        /*RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_OB_STYLE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("OBStyle", response.toString());
                if (response.contains("Successfully")) {
                    Toast.makeText(OBStyle.this, "Successfully Inserted!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("supervisor", MODE_PRIVATE).edit();
                    editor.putString("styleNoOB", styleNo.getText().toString());
                    editor.commit();
                    finish();
                } else {
                    //Toast.makeText(OBStyle.this, "Server Problem!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("StyleEntryErr", error.toString());
                //Toast.makeText(OBStyle.this, "Server Problem!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
                String lineNo = sharedPreferences.getString("lineNo", "");
                String superVisorId = sharedPreferences.getString("supervisorId", "");

                Calendar cal = Calendar.getInstance();
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                String uniqueKey = sdf.format(cal.getTime())+lineNo+superVisorId+styleNo.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("lineNo", lineNo);
                params.put("buyer", buyer.getText().toString());
                params.put("styleNo", styleNo.getText().toString());
                params.put("SMV", SMV.getText().toString());
                params.put("item", item.getText().toString());
                params.put("manpower", manpower.getText().toString());
                params.put("entryTime", sdf.format(cal.getTime()));
                params.put("supervisorId", superVisorId);
                params.put("uniqueKey", uniqueKey );
                Log.i("paramsStyleOB", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);*/
    }

    private void getAllStyles() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Endpoints.GET_ALL_STYLES_OB, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("DescriptionData", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        styleList.add(response.getJSONObject(i).getString("Style"));
                    } catch (JSONException e) {
                        Log.e("ArrayAssignErr", e.toString());
                    }
                }
                adapter1.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DescListErr", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }
}
