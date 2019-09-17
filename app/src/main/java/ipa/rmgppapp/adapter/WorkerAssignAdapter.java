package ipa.rmgppapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ipa.rmgppapp.R;
import ipa.rmgppapp.activity.ProductionActivity;
import ipa.rmgppapp.helper.Endpoints;
import ipa.rmgppapp.model.HourlyEntry;
import ipa.rmgppapp.model.ProcessItem;
import ipa.rmgppapp.model.Worker;

import static android.content.Context.MODE_PRIVATE;

public class WorkerAssignAdapter extends RecyclerView.Adapter<WorkerAssignAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProcessItem> processItemArrayList;
    ArrayList<Worker> workerArrayList;
    ArrayList<String> workerIdList;
    ArrayList<ProcessItem> assignedWorkerData;

    public WorkerAssignAdapter(Context context, ArrayList<ProcessItem> processItemArrayList, ArrayList<Worker> workerArrayList, ArrayList<String> workerIdList) {
        this.context = context;
        this.processItemArrayList = processItemArrayList;
        this.workerArrayList = workerArrayList;
        this.workerIdList = workerIdList;
        assignedWorkerData = new ArrayList<>();
    }

    private Worker getWorkerInfo(String s) {
        Worker worker = null;
        for (int i = 0; i < workerArrayList.size(); i++) {
            if (workerArrayList.get(i).getWorkerId().contains(s)) {
                worker = workerArrayList.get(i);
            }
        }
        if(worker==null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = df.format(new Date()).toString();
            worker = new Worker(s, "", "", "");
        }
        return worker;
    }

    public void saveData() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_ASSIGNED_WORKER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("savedWorkerData", response);
                if (response.contains("Error")) {
                    Toast.makeText(context, "ডাটা সেভ হয়নি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    String jsonProcess = gson.toJson(assignedWorkerData);
                    SharedPreferences.Editor editor = context.getSharedPreferences("hourlyEntry", MODE_PRIVATE).edit();
                    editor.putString("data", jsonProcess);
                    editor.commit();

                    Toast.makeText(context, "ডাটা সেভ হয়েছে!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, ProductionActivity.class);
                    context.startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorInWorkerSave", error.toString());
                Toast.makeText(context, "ডাটা সেভ হয়নি! আবার চেষ্টা করুন!", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String jsonArrayString = "";
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    jsonArrayString = gson.toJson(processItemArrayList);
                } catch (Exception e) {
                    Log.e("ArrayException", e.toString());
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences("supervisor", MODE_PRIVATE);
                String styleNoOB = sharedPreferences.getString("styleNoOB", "");
                String lineNo = sharedPreferences.getString("lineNo", "");

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String requiredDate = df.format(new Date()).toString();
                Log.i("EntryTime", requiredDate);
                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonArrayString", jsonArrayString);
                params.put("tag", styleNoOB);
                params.put("entryTime", requiredDate);
                params.put("lineNo", lineNo);
                Log.i("jsonArrayString", jsonArrayString);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView processName, machineType;
        EditText hourlyTarget;
        AutoCompleteTextView workerIdView;

        public MyViewHolder(View view) {
            super(view);
            processName = view.findViewById(R.id.processNameItem);
            machineType = view.findViewById(R.id.machineTypeItem);
            hourlyTarget = view.findViewById(R.id.hourlyTargetItem);

            Log.i("workerIdList", workerIdList.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, workerIdList);
            workerIdView = (AutoCompleteTextView)
                    view.findViewById(R.id.workerId);
            workerIdView.setAdapter(adapter);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_assign_custom_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WorkerAssignAdapter.MyViewHolder holder, final int position) {
        final ProcessItem processItem = processItemArrayList.get(position);

        holder.workerIdView.setText(processItem.getAssignedWorkerId());
        holder.processName.setText(processItem.getProcessName());
        holder.machineType.setText(processItem.getMachineType());
        holder.hourlyTarget.setText(Math.round(processItem.getHourlyTarget()) + "");

        holder.workerIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence value, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!holder.workerIdView.getText().toString().isEmpty() && holder.workerIdView.getText().toString().length() > 3) {
                    try {
                        if(!holder.workerIdView.getText().toString().isEmpty()) {
                            Log.i("Dhukse", "Dhukse");
                            Worker worker = getWorkerInfo(holder.workerIdView.getText().toString());
                            processItemArrayList.remove(position);
                            processItemArrayList.add(position, new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                                    holder.workerIdView.getText().toString(), worker.getName()));
                            assignedWorkerData.add(new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                                    holder.workerIdView.getText().toString(), worker.getName()));
                        }
                    } catch (Exception e) {
                        Log.e("ArrayListErr", e.toString());
                    }
                } else if(holder.workerIdView.getText().toString().isEmpty()){
                    processItemArrayList.remove(position);
                    processItemArrayList.add(position, new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                            "", ""));
                    assignedWorkerData.add(new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                            "", ""));
                }
            }
        });

        holder.hourlyTarget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!holder.hourlyTarget.getText().toString().isEmpty() && holder.hourlyTarget.getText().toString().length() > 1) {
                    try {
                        if(!holder.hourlyTarget.getText().toString().isEmpty()) {
                            Log.i("Dhukse", "Dhukse");
                            Worker worker = getWorkerInfo(holder.workerIdView.getText().toString());
                            processItemArrayList.remove(position);
                            processItemArrayList.add(position, new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                                    holder.workerIdView.getText().toString(), worker.getName()));
                            assignedWorkerData.add(new ProcessItem(processItem.getId(), processItem.getProcessName(), processItem.getMachineType(), new Double(Math.round(Integer.parseInt(holder.hourlyTarget.getText().toString()))),
                                    holder.workerIdView.getText().toString(), worker.getName()));
                        }else{

                        }
                    } catch (Exception e) {
                        Log.e("ArrayListErr", e.toString());
                    }
                } else {
                    Toast.makeText(context, "Please enter some value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return processItemArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
