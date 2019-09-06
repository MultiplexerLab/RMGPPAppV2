package ipa.rmgppapp.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ipa.rmgppapp.R;
import ipa.rmgppapp.activity.ProductionActivity;
import ipa.rmgppapp.activity.StyleListActivity;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.PlanningData;
import ipa.rmgppapp.model.ProcessItem;

import static android.content.Context.MODE_PRIVATE;

public class StyleReportFragment extends Fragment {

    public StyleReportFragment() {

    }
    private static final String[] TABLE_HEADERS = {"SL No", "Buyer", "Style", "P.O.", "Color", "Input", "Output", "WIP"};

    RequestQueue queue;
    ArrayList<String[]> tableData;
    TableView tableView;
    SimpleTableDataAdapter adapter;
    Button buttonRefreshStyleSummery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.fragment_style_report, container, false);
        tableView = customView.findViewById(R.id.tableViewStyleReport);
        buttonRefreshStyleSummery = customView.findViewById(R.id.buttonRefreshStyleSummery);
        queue = Volley.newRequestQueue(getActivity());
        tableData = new ArrayList<>();
        adapter = new SimpleTableDataAdapter(getActivity(), tableData);
        tableView.setDataAdapter(adapter);

        getTableData();

        buttonRefreshStyleSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTableData();
            }
        });
        return customView;
    }

    public void getTableData() {
        TableColumnDpWidthModel columnModel1 = new TableColumnDpWidthModel(getActivity(), 8, 150);
        columnModel1.setColumnWidth(0, 120);

        tableView.setColumnModel(columnModel1);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        String styleNo = sharedPreferences.getString("styleNo", "");
        String lineNo = sharedPreferences.getString("lineNo", "");
        String supervisorId = sharedPreferences.getString("supervisorId", "");

        String getUrl = Endpoints.GET_STYLE_SUMMERY + supervisorId;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrl", getUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                tableData.clear();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        final String arr[] = new String[8];
                        arr[0]=String.valueOf(i+1);
                        arr[1]=jsonObject1.getString("buyer");
                        arr[2]=jsonObject1.getString("styleNo");
                        arr[3]=jsonObject1.getString("orderNumber");
                        arr[4]=jsonObject1.getString("color");
                        arr[5]=jsonObject1.getString("lineInput");
                        arr[6]=jsonObject1.getString("lineOutput");
                        arr[7]=jsonObject1.getString("wip");

                        tableData.add(arr);
                        adapter.notifyDataSetChanged();
                        setTableData();
                    }
                } catch (JSONException e) {
                    Log.e("JsonError", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorStyleSummery", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
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
        queue.add(jsonObjectRequest);
    }

    private void setTableData() {
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));
    }
}
