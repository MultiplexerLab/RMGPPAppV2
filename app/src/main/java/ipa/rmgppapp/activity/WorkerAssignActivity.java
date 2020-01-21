package ipa.rmgppapp.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import ipa.rmgppapp.R;
import ipa.rmgppapp.adapter.WorkerAssignAdapter;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.ProcessItem;
import ipa.rmgppapp.model.Worker;

public class WorkerAssignActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<ProcessItem> processItemArrayList;
    ArrayList<Worker> workerList;
    ArrayList<String> workerIdList;
    WorkerAssignAdapter adapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_assign);

        queue = Volley.newRequestQueue(this);
        mRecyclerView = findViewById(R.id.recyclerViewProcess);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        processItemArrayList = new ArrayList<>();
        workerList = new ArrayList<>();
        workerIdList = new ArrayList<>();
        AsyncGetWorkerData workerData = new AsyncGetWorkerData();
        workerData.execute();
    }

    private void setData() {
        SharedPreferences sharedPreferences = getSharedPreferences("supervisor", MODE_PRIVATE);
        String tag = sharedPreferences.getString("styleNoOB", "");
        String lineNo = sharedPreferences.getString("lineNo", "");
        String supervisorId = sharedPreferences.getString("supervisorId", "");
        Log.i("tagData", tag);

        String getUrl = Endpoints.GET_OPERATION_DATA_URL + "?styleNo=" + tag+"&lineNo="+lineNo+"&supervisorId="+supervisorId;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrlWorkersAssign", getUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("getPlanning", response.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<List<ProcessItem>>() {
                }.getType();
                processItemArrayList = gson.fromJson(response.toString(), type);
                Log.i("workerListPrev", workerIdList.toString());
                adapter = new WorkerAssignAdapter(WorkerAssignActivity.this, processItemArrayList, workerList, workerIdList);
                mRecyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorVolley", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void getAllWorkerId() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Endpoints.GET_HR_DATA_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("responseWorker", response.toString());

                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Worker>>() {
                    }.getType();
                    workerList = gson.fromJson(response.toString(), type);
                    Log.i("workerList", workerList.toString());
                } catch (Exception e) {
                    Log.e("WorkerErr", e.toString());
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String workerId = response.getJSONObject(i).getString("workerId");
                        Log.i("WorkerId", workerId);
                        workerIdList.add(workerId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void cancelWorkerAssign(View view) {
        finish();
    }

    public void continueWorkerAssign(View view) {
        adapter.saveData();
        //finish();
    }

    public void refreshData(View view) {
        AsyncGetWorkerData workerData = new AsyncGetWorkerData();
        workerData.execute();
    }

    private class AsyncGetProcessData extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            publishProgress("Data is Loading...");
            setData();
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(WorkerAssignActivity.this,
                    "Data is loading",
                    "Wait for a few moments");
        }
    }

    private class AsyncGetWorkerData extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(WorkerAssignActivity.this,
                    "Data is loading",
                    "Wait for a few moments");
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Data is Loading...");
            getAllWorkerId();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            AsyncGetProcessData process = new AsyncGetProcessData();
            process.execute();
        }

    }
}
