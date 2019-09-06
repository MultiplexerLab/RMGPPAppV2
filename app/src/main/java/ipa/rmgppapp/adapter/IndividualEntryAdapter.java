package ipa.rmgppapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ipa.rmgppapp.R;
import ipa.rmgppapp.helper.DateTimeInstance;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.HourlyEntry;
import ipa.rmgppapp.model.ProcessItem;

import static android.content.Context.MODE_PRIVATE;

public class IndividualEntryAdapter extends RecyclerView.Adapter<IndividualEntryAdapter.MyViewHolder> {

    ArrayList<ProcessItem> processItems;
    ArrayList<String> problems;
    Context context;
    String[] times = {"Hour 1", "Hour 2", "Hour 3", "Hour 4", "Hour 5", "Hour 6", "Hour 7", "Hour 8", "Hour 9", "Hour 10" , "Hour 11", "Hour 12", "Hour 13", "Hour 14", "Hour 15"};
    String problemTypes[] = {"Choose a problem Type", "Input", "Maintenance", "Quality", "Production"};


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView workerId, workerName, processName, hourlyTarget;
        EditText hourlyOutput, note;
        Button saveIndividualEntry;
        Spinner spinnerTime, problemTypeSpinner, problemsSpinner;
        boolean flag = false;
        ArrayAdapter<String> problemAdapter;

        public MyViewHolder(View view) {
            super(view);
            spinnerTime = view.findViewById(R.id.spinnerTime);
            workerId = view.findViewById(R.id.workerId);
            workerName = view.findViewById(R.id.workerName);
            processName = view.findViewById(R.id.processName);
            hourlyOutput = view.findViewById(R.id.hourlyOutput);
            saveIndividualEntry = view.findViewById(R.id.saveIndividualEntry);
            problemTypeSpinner = view.findViewById(R.id.problemTypeSpinner);
            problemsSpinner = view.findViewById(R.id.problemsSpinner);
            hourlyTarget = view.findViewById(R.id.hourlyTarget);
            note = view.findViewById(R.id.note);

            spinnerTime.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, times));

            problems = new ArrayList<>();
            problems.add("Choose a Problem");

            problemTypeSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, problemTypes));

            problemAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, problems);
            problemsSpinner.setAdapter(problemAdapter);

            problemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    problems.clear();
                    problems.add("Choose a Problem");
                    RequestQueue queue = Volley.newRequestQueue(context);
                    if(!problemTypes[position].contains("Choose")) {
                        final JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, Endpoints.GET_PROBLEM_DATA_URL + problemTypes[position], null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            String problem = jsonArray.getJSONObject(i).getString("Problem");
                                            Log.i("problem", problem);
                                            problems.add(problem);
                                        } catch (JSONException e) {
                                            Log.e("ProblemLoadingErr", e.toString());
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }finally {
                                    problemAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, problems);
                                    problemsSpinner.setAdapter(problemAdapter);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        queue.add(jsonArrayRequest);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            saveIndividualEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hourlyOutput.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Insert hourly output!", Toast.LENGTH_SHORT).show();
                    } else {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDate = df.format(new Date()).toString();

                        String problemType = "";
                        String problem = "";

                        if(!problemTypeSpinner.getSelectedItem().toString().contains("Choose")) {
                            problemType = problemTypeSpinner.getSelectedItem().toString();
                            if(!problemsSpinner.getSelectedItem().toString().contains("Choose")) {
                                problem = problemsSpinner.getSelectedItem().toString();
                            }
                        }

                        SharedPreferences factoryPref = context.getSharedPreferences("factoryPref", MODE_PRIVATE);
                        String factoryCode = factoryPref.getString("factoryCode", "");
                        int factoryC = Integer.parseInt(factoryCode);

                        HourlyEntry obj = new HourlyEntry(spinnerTime.getSelectedItem().toString(), workerId.getText().toString(), workerName.getText().toString(),
                                processName.getText().toString(), hourlyTarget.getText().toString(), Integer.parseInt(hourlyOutput.getText().toString()), currentDate, problemType,
                                problem, DateTimeInstance.getTimeStamp(), note.getText().toString(), factoryC);
                        saveIndividualEntry(obj);
                    }
                }
            });
        }
    }

    private void saveIndividualEntry(final HourlyEntry obj) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_HOURLY_DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("SUCCESS")) {
                    Toast.makeText(context, "ডাটা সেভ হয়েছে!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "ডাটা সেভ হয়নি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
                }
                Log.i("Response", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("InsertIndividualEntry", error.toString());
                Toast.makeText(context, "ডাটা সেভ হয়নি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String jsonString = "";
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    jsonString = gson.toJson(obj);

                } catch (Exception e) {
                    Log.e("ArrayException", e.toString());
                }
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = df.format(new Date()).toString();

                SharedPreferences sharedPreferences = context.getSharedPreferences("supervisor", MODE_PRIVATE);
                String supervisor = sharedPreferences.getString("supervisorId", "");
                String styleNo = sharedPreferences.getString("styleNo", "");
                String lineNo = sharedPreferences.getString("lineNo", "");

                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("supervisor", supervisor);
                params.put("styleNo", styleNo);
                params.put("lineNo", lineNo);
                params.put("uniqueKey", obj.getWorkerId()+supervisor+styleNo+currentDate+obj.getHour());
                Log.i("jsonString", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }


    public IndividualEntryAdapter(Context context, ArrayList<ProcessItem> processItems) {
        this.processItems = processItems;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receycler_view_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (processItems.get(position).getAssignedWorkerId() != null || !processItems.get(position).getAssignedWorkerId().isEmpty()) {
            ProcessItem processItem = processItems.get(position);
            holder.workerId.setText(processItem.getAssignedWorkerId());
            holder.workerName.setText(processItem.getAssignedWorkerName());
            holder.processName.setText(processItem.getProcessName());
            holder.hourlyTarget.setText(Math.round(processItem.getHourlyTarget())+"");
        }
    }
    @Override
    public int getItemCount() {
        return processItems.size();
    }
}
