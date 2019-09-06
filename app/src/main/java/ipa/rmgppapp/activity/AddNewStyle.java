package ipa.rmgppapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddNewStyle extends AppCompatActivity {

    EditText item, quantity, shipmentdate, color;
    Calendar myCalendar;
    AutoCompleteTextView buyer, styleNo, order;
    ArrayList<String> buyers;
    ArrayList<String> styleList;
    ArrayList<String> orderList;
    ArrayAdapter<String> adapter, adapter1, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_style);

        styleNo = (AutoCompleteTextView) findViewById(R.id.styleNo);
        order = (AutoCompleteTextView) findViewById(R.id.order);
        buyer = (AutoCompleteTextView) findViewById(R.id.buyer);

        item = findViewById(R.id.item);
        quantity = findViewById(R.id.quantity);
        shipmentdate = findViewById(R.id.shipMentDate);
        color = findViewById(R.id.color);
        buyers = new ArrayList<>();
        styleList = new ArrayList<>();
        orderList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(AddNewStyle.this,
                android.R.layout.simple_dropdown_item_1line, buyers);
        buyer.setAdapter(adapter);

        adapter1 = new ArrayAdapter<String>(AddNewStyle.this,
                android.R.layout.simple_dropdown_item_1line, styleList);
        styleNo.setAdapter(adapter1);

        adapter2 = new ArrayAdapter<String>(AddNewStyle.this,
                android.R.layout.simple_dropdown_item_1line, orderList);
        order.setAdapter(adapter2);

        getAllData();

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        shipmentdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNewStyle.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void getPlannigDataFromOrderList(View view) {
        if (styleNo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Insert StyleNo then Search", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Endpoints.GET_STYLE_DETAILS_FROM_ORDERLIST + "?tag=StyleNo" + "&val=" + styleNo.getText().toString();
            url = url.replace(" ", "%20");
            Log.i("getUrl", url);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            styleNo.setText(jsonObject.getString("style"));
                            buyer.setText(jsonObject.getString("buyer"));
                            order.setText(jsonObject.getString("orderNo"));
                            quantity.setText(jsonObject.getString("quantity"));
                            item.setText(jsonObject.getString("item"));
                            if (jsonObject.getString("color") != null) {
                                color.setText("");
                            } else {
                                color.setText(jsonObject.getString("color"));
                            }

                            shipmentdate.setText(jsonObject.getString("shipmentDate"));
                            Log.i("PlanningData", jsonObject.toString());
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
    }

    private void getAllData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Endpoints.GET_ALL_STYLES, null, new Response.Listener<JSONArray>() {
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
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DescListErr", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        shipmentdate.setText(sdf.format(myCalendar.getTime()));
    }

    public void cancelStyle(View view) {
        finish();
    }

    public void saveStyle(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_NEW_STYLE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("NewStyle", response.toString());
                if (response.contains("SUCCESS")) {
                    Intent intent = new Intent(AddNewStyle.this, StyleListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AddNewStyle.this, "Server Problem!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("StyleEntryErr", error.toString());
                Toast.makeText(AddNewStyle.this, "Server Problem!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
                String lineNo = sharedPreferences.getString("lineNo", "");

                Calendar cal = Calendar.getInstance();
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
                String factoryCode = factoryPref.getString("factoryCode", "");

                Map<String, String> params = new HashMap<String, String>();
                params.put("plannedLine", lineNo);
                params.put("buyer", buyer.getText().toString());
                params.put("style", styleNo.getText().toString());
                params.put("description", "");
                params.put("item", item.getText().toString());
                params.put("orderNo", order.getText().toString());
                params.put("shipmentDate", shipmentdate.getText().toString());
                params.put("quantity", quantity.getText().toString());
                params.put("sewingStart", sdf.format(cal.getTime()));
                params.put("factoryCode", factoryCode);
                Log.i("StyleData", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
