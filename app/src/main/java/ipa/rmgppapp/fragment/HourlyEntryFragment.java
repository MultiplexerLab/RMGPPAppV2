package ipa.rmgppapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ipa.rmgppapp.R;
import ipa.rmgppapp.adapter.IndividualEntryAdapter;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.ProcessItem;

import static android.content.Context.MODE_PRIVATE;

public class HourlyEntryFragment extends Fragment {

    RecyclerView mRecyclerView;
    RequestQueue queue;
    Button refreshButton;
    String problemTypes[] = {"Choose a problem Type", "Input", "Maintenance", "Quality", "Production"};

    public HourlyEntryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View customView = inflater.inflate(R.layout.fragment_hourly_entry, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) customView.findViewById(R.id.recyclerView);
        refreshButton = (Button) customView.findViewById(R.id.buttonRefeshWorker);
        mRecyclerView.setLayoutManager(layoutManager);

        getAssignedWorkerData();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAssignedWorkerData();
            }
        });
        return customView;
    }

    private void getAssignedWorkerData() {
        queue = Volley.newRequestQueue(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        String lineNo = sharedPreferences.getString("lineNo", "");
        String tag = sharedPreferences.getString("styleNo", "");

        String getUrl = Endpoints.GET_ASSIGNED_WORKER_URL + "?tag=" + tag+"&lineNo="+lineNo;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrlIndividual", getUrl);

        Log.i("TagGet", tag);
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ProcessItem>>() {
                }.getType();
                Log.i("DataAssignedWorker", response.toString());
                ArrayList<ProcessItem> processItems = gson.fromJson(response.toString(), type);

                IndividualEntryAdapter adapter = new IndividualEntryAdapter(getActivity(), processItems);
                mRecyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorWorkerAssign", error.toString());
            }
        });
        queue.add(stringRequest);
    }
}
