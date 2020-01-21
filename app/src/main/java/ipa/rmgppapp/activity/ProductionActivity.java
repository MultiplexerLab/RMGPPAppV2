package ipa.rmgppapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ipa.rmgppapp.R;
import ipa.rmgppapp.adapter.ViewPagerAdapter;
import ipa.rmgppapp.fragment.FullDaySummeryFragment;
import ipa.rmgppapp.fragment.HourlyEntryFragment;
import ipa.rmgppapp.fragment.HourlyReportFragment;
import ipa.rmgppapp.fragment.LineInputFragment;
import ipa.rmgppapp.fragment.LineOutputFragment;
import ipa.rmgppapp.fragment.StyleReportFragment;
import ipa.rmgppapp.helper.DateTimeInstance;
import ipa.rmgppapp.helper.Endpoints;

public class ProductionActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;
    Button buttonJumpWorkerAssign, buttonLineTarget;
    String totalTarget, totalHours;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production);

        queue = Volley.newRequestQueue(this);
        tabLayout = findViewById(R.id.tabLayoutProfile);
        viewPager = findViewById(R.id.viewPagerProfile);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        buttonJumpWorkerAssign = findViewById(R.id.buttonJumpWorkerAssign);
        buttonLineTarget = findViewById(R.id.buttonLineTarget);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragments(new HourlyEntryFragment(), "Hourly Input");
        adapter.addFragments(new HourlyReportFragment(), "Hourly Report");
        adapter.addFragments(new LineInputFragment(), "Line\nInput");
        adapter.addFragments(new LineOutputFragment(), "Line\nOutput");
        adapter.addFragments(new FullDaySummeryFragment(), "Full Day Summery");
        adapter.addFragments(new StyleReportFragment(), "Style Summery");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        buttonJumpWorkerAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductionActivity.this, WorkerAssignActivity.class);
                startActivity(intent);
            }
        });

        getPreviousLineTargetData();

        buttonLineTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLineTargetDialog();
            }
        });
    }

    private void getPreviousLineTargetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
        String styleNo = sharedPreferences.getString("styleNo", "");
        String supervisorId = sharedPreferences.getString("supervisorId", "");
        String lineNo = sharedPreferences.getString("lineNo", "");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(new Date()).toString();

        String getUrl = Endpoints.CHECK_LINE_TARGET_URL + "?styleNo=" + styleNo + "&entryTime=" + currentDate + "&supervisorId=" + supervisorId +"&lineNo=" + lineNo ;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrlLineTarget", getUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("responseLineData", response.toString());
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    totalTarget = jsonObject.getString("totalTarget");
                    totalHours = jsonObject.getString("totalHours");
                } catch (JSONException e) {
                    showLineTargetDialog();
                    Log.e("JSONLineDataErr", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLineTargetDialog();
                Log.e("LineDataErr", error.toString());
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }

    private void showLineTargetDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(ProductionActivity.this).create();
        dialog.setTitle("Total Line Target");
        dialog.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dialog_layout_line_input, null);

        dialog.setView(customView);

        Button saveButton = customView.findViewById(R.id.lineInputBtn);
        final EditText lineTarget = customView.findViewById(R.id.editTextLineInput);
        final EditText totalHoursEd = customView.findViewById(R.id.editTextTotalHours);
        lineTarget.setText(totalTarget);
        totalHoursEd.setText(totalHours);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lineTargetStr = lineTarget.getText().toString();
                if (!lineTargetStr.isEmpty()) {
                    insertLineTarget(lineTargetStr, totalHoursEd.getText().toString());
                    dialog.dismiss();
                } else {
                    Toast.makeText(ProductionActivity.this, "Insert Data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void insertLineTarget(final String lineTargetStr, final String totalHours) {
        RequestQueue queue = Volley.newRequestQueue(this);

        SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
        String factoryCode = factoryPref.getString("factoryCode", "");
        int factoryC = Integer.parseInt(factoryCode);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_LINE_TARGET_URL+"?factoryCode="+factoryC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("SUCCESS")) {
                    Toast.makeText(ProductionActivity.this, "Data is saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("lineTargetEntryResponse", response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("lineTargetEntry", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String requiredDate = df.format(new Date()).toString();

                SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
                String superVisorId = sharedPreferences.getString("supervisorId", "");
                String lineNo = sharedPreferences.getString("lineNo", "");
                String styleNo = sharedPreferences.getString("styleNo", "");

                int lineInput = Integer.parseInt(lineTargetStr) / Integer.parseInt(totalHours);

                Map<String, String> params = new HashMap<String, String>();
                params.put("styleNo", styleNo);
                params.put("secret_key", superVisorId + lineNo + styleNo+requiredDate);
                params.put("lineTarget", lineInput + "");
                params.put("totalTarget", lineTargetStr);
                params.put("totalHours", totalHours);
                params.put("entryTime", requiredDate);
                params.put("time", DateTimeInstance.getTimeStamp());
                params.put("lineNo", lineNo);
                params.put("supervisorId", superVisorId);

                Log.i("ParamsStr", params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void jumpToOBStyle(View view) {
        Intent intent = new Intent(ProductionActivity.this, OBStyle.class);
        startActivity(intent);
    }
}
