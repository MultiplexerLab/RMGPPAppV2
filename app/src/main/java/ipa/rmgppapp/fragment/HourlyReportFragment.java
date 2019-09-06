package ipa.rmgppapp.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ipa.rmgppapp.R;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.ProcessItem;

import static android.content.Context.MODE_PRIVATE;

public class HourlyReportFragment extends Fragment {

    public HourlyReportFragment(){

    }
    ArrayList<String[]> tableData;
    private static final String[] TABLE_HEADERS = { "Worker\nName" , "Worker\nId","Process\nName", "Total", "Hour 1", "Hour 2", "Hour 3", "Hour 4", "Hour 5", "Hour 6", "Hour 7", "Hour 8", "Hour 9", "Hour 10", "Hour 11", "Hour 12", "Hour 13", "Hour 14", "Hour 15"};
    TableView tableView;
    Button buttonRefreshHourlyReport;
    SimpleTableDataAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.fragment_hourly_report, container, false);
        tableView = (TableView) customView.findViewById(R.id.tableView);
        buttonRefreshHourlyReport = customView.findViewById(R.id.buttonRefreshHourlyReport);

        tableData = new ArrayList<>();

        TableColumnDpWidthModel columnModel1 = new TableColumnDpWidthModel(getActivity(), 20, 130);
        columnModel1.setColumnWidth(2, 220);
        columnModel1.setColumnWidth(0, 200);
        columnModel1.setColumnWidth(1, 200);
        tableView.setColumnModel(columnModel1);

        adapter = new SimpleTableDataAdapter(getActivity(), tableData);
        tableView.setDataAdapter(adapter);
        AsyncGetHourlyReport obj = new AsyncGetHourlyReport();
        obj.execute();

        buttonRefreshHourlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncGetHourlyReport obj = new AsyncGetHourlyReport();
                obj.execute();
            }
        });
        return customView;
    }

    private void setTableData() {
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), TABLE_HEADERS));
    }

    private void getTableData() {
        tableData.clear();
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        String tag = sharedPreferences.getString("styleNo", "");
        String lineNo = sharedPreferences.getString("lineNo", "");

        String getUrl = Endpoints.GET_ASSIGNED_WORKER_URL + "?tag=" + tag +"&lineNo="+lineNo;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrl", getUrl);

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ProcessItem>>() {
                }.getType();
                Log.i("DataAssignedWorker", response.toString());
                final ArrayList<ProcessItem> processItems = gson.fromJson(response.toString(), type);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = df.format(new Date()).toString();
                Log.i("processItems.size()", processItems.size()+"");
                for(int i=0; i<processItems.size(); i++){
                    final String arr[] = new String[19];
                    final String workerId = processItems.get(i).getAssignedWorkerId();
                    final int finalI = i;
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Endpoints.GET_HOURLY_RECORD_DATA + "?workerId=" + workerId+"&entryTime="+currentDate, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                arr[1] = workerId;
                                arr[0] = processItems.get(finalI).getAssignedWorkerName();
                                arr[2] = processItems.get(finalI).getProcessName();

                                int totalQuantity=0;

                                for (int j = 0; j < response.length(); j++) {
                                    try {
                                        JSONObject jsonObject = response.getJSONObject(j);
                                        Log.i("HourlyData", jsonObject.toString());

                                        String hour = jsonObject.getString("hour");
                                        if (hour.equals("Hour 1")) {
                                            arr[4] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[4]);
                                        } else if (hour.contains("Hour 2")) {
                                            arr[5] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[5]);
                                        } else if (hour.contains("Hour 3")) {
                                            arr[6] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[6]);
                                        } else if (hour.contains("Hour 4")) {
                                            arr[7] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[7]);
                                        } else if (hour.contains("Hour 5")) {
                                            arr[8] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[8]);
                                        } else if (hour.contains("Hour 6")) {
                                            arr[9] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[9]);
                                        } else if (hour.contains("Hour 7")) {
                                            arr[10] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[10]);
                                        } else if (hour.contains("Hour 8")) {
                                            arr[11] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[11]);
                                        } else if (hour.contains("Hour 9")) {
                                            Log.i("9th Hour", "Dhukse");
                                            arr[12] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[12]);
                                        } else if (hour.contains("Hour 10")) {
                                            Log.i("10th Hour", "Dhukse");
                                            arr[13] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[13]);
                                        }else if (hour.contains("Hour 11")) {
                                            arr[14] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[14]);
                                        } else if (hour.contains("Hour 12")) {
                                            arr[15] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[15]);
                                        } else if (hour.contains("Hour 13")) {
                                            arr[16] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[16]);
                                        } else if (hour.contains("Hour 14")) {
                                            Log.i("9th Hour", "Dhukse");
                                            arr[17] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[17]);
                                        } else if (hour.contains("Hour 15")) {
                                            Log.i("10th Hour", "Dhukse");
                                            arr[18] = jsonObject.getString("quantity");
                                            totalQuantity = totalQuantity+ Integer.parseInt(arr[18]);
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JsonException", e.toString());
                                    }catch (Exception e){
                                        Log.e("ExceptionGeneral", e.toString());
                                    }
                                }
                                arr[3] = totalQuantity+"";
                                tableData.add(arr);
                                setTableData();
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("HourlyReport", error.toString());
                            }
                        });
                        queue.add(jsonArrayRequest);
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorWorkerAssign", error.toString());
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

    private class AsyncGetHourlyReport extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            getTableData();
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                    "Data is loading",
                    "Wait for a few moments");
        }
    }
}
