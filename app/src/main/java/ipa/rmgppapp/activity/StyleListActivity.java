package ipa.rmgppapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ipa.rmgppapp.R;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.PlanningData;

public class StyleListActivity extends AppCompatActivity {

    private static final String[] TABLE_HEADERS = {"SL No", "Buyer", "Style", "Item", "Order", "Quantity", "Ship Date", "Status"};

    RequestQueue queue;
    ArrayList<PlanningData> planningDataArrayList;
    ArrayList<String[]> tableData;
    TableView tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylelist);

        tableView = findViewById(R.id.tableViewReport);
        queue = Volley.newRequestQueue(this);
        planningDataArrayList = new ArrayList<>();
        tableData = new ArrayList<>();

        getStyles();
    }

    private void getStyles() {
        planningDataArrayList.clear();
        tableData.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
        final String lineNo = sharedPreferences.getString("lineNo", "");
        Log.i("lineNoStyle", lineNo);

        SharedPreferences factoryPref = getSharedPreferences("factoryPref", MODE_PRIVATE);
        String factoryCode = factoryPref.getString("factoryCode", "");

        Log.i("lineNo", lineNo);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Endpoints.GET_PLANNING_DATA_URL + "?lineNo=" + lineNo+"&factoryCode="+factoryCode, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.i("responseStyles", jsonArray.toString());
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<PlanningData>>() {
                    }.getType();
                    planningDataArrayList = gson.fromJson(jsonArray.toString(), type);

                    for (int i = 0; i < planningDataArrayList.size(); i++) {
                        PlanningData obj = planningDataArrayList.get(i);
                        tableData.add(i, new String[]{(i + 1) + "", obj.getBuyer(), obj.getStyle(), obj.getItem(), obj.getOrderNo(), obj.getPlannedQuantity(), obj.getShipmentData(),
                                obj.getStatus()});
                    }
                    setTableData();
                } catch (Exception e) {
                    Log.e("ErrorInPlanningData", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorVolley", error.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }

    public void setTableData() {
        tableView = findViewById(R.id.tableViewReport);
        TableColumnDpWidthModel columnModel1 = new TableColumnDpWidthModel(this, 8, 160);
        columnModel1.setColumnWidth(0, 60);
        columnModel1.setColumnWidth(1, 180);
        columnModel1.setColumnWidth(2, 220);
        columnModel1.setColumnWidth(3, 250);
        columnModel1.setColumnWidth(4, 200);
        columnModel1.setColumnWidth(5, 120);

        tableView.setColumnModel(columnModel1);

        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        Log.i("tableData", tableData.toString());
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, tableData));

        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                String styleNo = planningDataArrayList.get(rowIndex).getStyle();
                Log.i("styleNo", styleNo);

                SharedPreferences.Editor editor = getSharedPreferences("supervisor", MODE_PRIVATE).edit();
                editor.putString("styleNo", planningDataArrayList.get(rowIndex).getStyle());
                editor.commit();

                if (styleNo.isEmpty()) {
                    Toast.makeText(StyleListActivity.this, "You have to choose a style!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StyleListActivity.this, ProductionActivity.class);
                    startActivity(intent);
                }
            }
        });

        tableView.addDataLongClickListener(new TableDataLongClickListener() {
            @Override
            public boolean onDataLongClicked(final int rowIndex, Object clickedData) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(StyleListActivity.this);
                dialog.setTitle("Delete a style?").setCancelable(false).setMessage("Do you want to complete this style?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PlanningData planningData = planningDataArrayList.get(rowIndex);
                        updateStatus(planningData.getId());
                    }
                });
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void deleteStyleFromPlanning(final String lineNo, final String style) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Endpoints.DELETE_STYLE_URL + "?lineNo=" + lineNo + "&styleNo=" + style;
        try {
            url = URLEncoder.encode(url, "utf-8");
            url = url.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("urlDelete", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response.toString());
                if (response.contains("DONE")) {
                    getStyles();
                    Toast.makeText(StyleListActivity.this, "The style is deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StyleListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })/*{
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                String accessToken = pref.getString("accessToken", "");
                Log.i("accessToken", accessToken);
                Map<String, String> params = new HashMap<String, String>();
                params.put("styleNo", style);
                params.put("lineNo", lineNo);
                return params;
            }*/;
        queue.add(stringRequest);
    }

    public void addStyle(View view) {
        Intent intent = new Intent(StyleListActivity.this, AddNewStyle.class);
        startActivity(intent);
    }

    private void updateStatus(String id) {
        RequestQueue queue = Volley.newRequestQueue(StyleListActivity.this);
        String updateUrl = Endpoints.UPDATE_STYLE_STATUS + "?id=" + id;
        updateUrl = updateUrl.replace(" ", "%20");
        Log.i("updateUrl", updateUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("responseUpdateStatus", response.toString());
                        if (response.contains("DONE")) {
                            getStyles();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onResume() {
        tableView.getDataAdapter().notifyDataSetChanged();
        super.onResume();
    }

    public void refreshStyles(View view) {
        getStyles();
    }
}
