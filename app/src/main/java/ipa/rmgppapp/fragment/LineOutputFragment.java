package ipa.rmgppapp.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import ipa.rmgppapp.model.LineEntry;

import static android.content.Context.MODE_PRIVATE;

public class LineOutputFragment extends Fragment {

    ArrayList<String> problems, statusList;
    String times[] = {"Hour 1", "Hour 2", "Hour 3", "Hour 4", "Hour 5", "Hour 6", "Hour 7", "Hour 8", "Hour 9", "Hour 10" , "Hour 11", "Hour 12", "Hour 13", "Hour 14", "Hour 15"};
    Button saveHourlyEntry;
    EditText editTextOutput, editTextBuyer, editTextStyle, editTextPO, editTextColor, hourlyLineTarget;
    Spinner problemTypeSpinner, problemsSpinner, statusSpinner;
    String problemTypes[] = {"Choose a problem Type", "Input", "Maintenance", "Quality", "Production"};
    ArrayAdapter<String> problemsAdapter;
    boolean flag = false;
    ListView listViewLineData;
    ArrayList<String> arrayListLineData;
    ArrayAdapter<String> adapterLineData;
    ArrayList<String> idList;
    int flag1 = 0;

    public LineOutputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View customView = inflater.inflate(R.layout.fragment_line_output, container, false);
        final Spinner spinnerTime = customView.findViewById(R.id.spinnerTime);
        spinnerTime.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, times));
        editTextBuyer = customView.findViewById(R.id.editTextBuyer);
        editTextStyle = customView.findViewById(R.id.editTextStyleOutput);
        editTextPO = customView.findViewById(R.id.editTextOutputPurchaseOrderNo);
        editTextColor = customView.findViewById(R.id.editTextOutputColor);
        editTextOutput = customView.findViewById(R.id.editTextOutput);
        problemTypeSpinner = customView.findViewById(R.id.problemTypeSpinner);
        problemsSpinner = customView.findViewById(R.id.problemsSpinner);
        statusSpinner = customView.findViewById(R.id.spinnerStatus);
        hourlyLineTarget = customView.findViewById(R.id.editTextOutputTarget);
        saveHourlyEntry = customView.findViewById(R.id.saveHourlyEntry);
        listViewLineData = customView.findViewById(R.id.listViewLineData);

        arrayListLineData = new ArrayList<>();
        statusList = new ArrayList<>();
        idList = new ArrayList<>();

        getLineData();

        SharedPreferences preferences = getActivity().getSharedPreferences("outputdata", MODE_PRIVATE);
        editTextBuyer.setText(preferences.getString("buyer", ""));
        editTextStyle.setText(preferences.getString("style", ""));
        editTextColor.setText(preferences.getString("color", ""));
        editTextPO.setText(preferences.getString("orderno", ""));

        adapterLineData = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayListLineData);
        listViewLineData.setAdapter(adapterLineData);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        final String styleNo = sharedPreferences.getString("styleNo", "");
        final String buyer = sharedPreferences.getString("buyer", "");
        final String orderNo = sharedPreferences.getString("orderNo", "");
        final String color = sharedPreferences.getString("color", "");
        editTextStyle.setText(styleNo);
        editTextBuyer.setText(buyer);
        editTextPO.setText(orderNo);
        editTextColor.setText(color);


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(new Date()).toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String getUrl = Endpoints.CHECK_LINE_TARGET_URL + "?styleNo=" + styleNo + "&entryTime=" + currentDate;
        getUrl = getUrl.replace(" ", "%20");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("responseLineData", response.toString());
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    hourlyLineTarget.setText(jsonObject.getString("lineTarget"));
                } catch (JSONException e) {
                    Log.e("JSONLineDataErr", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LineDataErr", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
        spinnerTime.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, times));
        problems = new ArrayList<>();
        problems.add("Choose a Problem");

        problemTypeSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, problemTypes));
        problemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, problems);
        problemsSpinner.setAdapter(problemsAdapter);

        ArrayAdapter adapterStatus = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, statusList);
        statusList.add("Choose a status");
        statusList.add("Resolved");
        statusList.add("Not Resolved");
        statusSpinner.setAdapter(adapterStatus);

        problemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                flag = true;
                getProblemData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveHourlyEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String requiredDate = df.format(new Date()).toString();
                String problemType = "", problem = "", status = "";

                if (flag) {
                    if (!problemTypeSpinner.getSelectedItem().toString().contains("Choose")) {
                        problemType = problemTypeSpinner.getSelectedItem().toString();
                        problem = problemsSpinner.getSelectedItem().toString();
                    }
                }
                if (!statusSpinner.getSelectedItem().toString().contains("Choose")) {
                    status = statusSpinner.getSelectedItem().toString();
                }
                if (editTextOutput.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "আউটপুট সেট করুন", Toast.LENGTH_SHORT).show();
                }else if(editTextBuyer.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "বায়ার সেট করুন", Toast.LENGTH_SHORT).show();
                }else if(editTextStyle.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "স্টাইল সেট করুন", Toast.LENGTH_SHORT).show();
                }else {
                    LineEntry lineEntry = new LineEntry(editTextBuyer.getText().toString(), editTextStyle.getText().toString(),
                            editTextPO.getText().toString(), editTextColor.getText().toString(), spinnerTime.getSelectedItem().toString(), "",
                            editTextOutput.getText().toString(), problemType, problem, status, requiredDate, DateTimeInstance.getTimeStamp());
                    saveLineEntry(lineEntry);
                }
            }
        });

        listViewLineData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Resolved?").setCancelable(false).setMessage("Is the problem resolved now?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateStatus(position);
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

        listViewLineData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Delete?").setCancelable(false).setMessage("Do you want to delete this data? If you delete you can not see it anymore!");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteLineData(idList.get(position));
                    }
                });
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        editTextStyle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editTextStyle.getText().toString().isEmpty() && editTextStyle.getText().toString().length() > 5) {
                    flag1 = flag1 + 1;
                    if (flag1 == 1) {
                        getBuyerData("StyleNo", editTextStyle.getText().toString());
                    }
                }
            }
        });
        return customView;
    }

    private void getBuyerData(String tag, String val) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Endpoints.GET_STYLE_DETAILS_FROM_ORDERLIST + "?tag=" + tag + "&val=" + val;
        url = url.replace(" ", "%20");
        Log.i("Url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        editTextBuyer.setText(jsonObject.getString("buyer"));
                        editTextPO.setText(jsonObject.getString("orderNo"));
                        editTextColor.setText(jsonObject.getString("color"));
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

    private void deleteLineData(String id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Endpoints.DELETE_LINE_DATA_URL + "?id=" + id;
        url = url.replace(" ", "%20");
        Log.i("url", url.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("ResponseDeleteLineData", response.toString());
                if (response.contains("DONE")) {
                    getLineData();
                    Toast.makeText(getActivity(), "The Data is deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void updateStatus(int position) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String updateUrl = Endpoints.UPDATE_LINE_DATA_STATUS + "?id=" + idList.get(position);
        updateUrl = updateUrl.replace(" ", "%20");
        Log.i("updateUrl", updateUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("responseUpdateLineData", response.toString());
                        if (response.contains("DONE")) {
                            getLineData();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    public void getProblemData(int position) {
        problems.clear();
        problems.add("Choose a Problem");
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        /*String getUrl = Endpoints.GET_PROBLEM_DATA_URL + "?problemType=" + problemType;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("getUrl", getUrl);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String problem = response.getJSONObject(i).getString("Problem");
                        problems.add(problem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                problemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, problems);
                problemsSpinner.setAdapter(problemsAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonArrayRequest);*/

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
                        problemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, problems);
                        problemsSpinner.setAdapter(problemsAdapter);
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

    private void saveLineEntry(final LineEntry obj) {
        Log.i("objData", obj.getColor() + obj.getOrderNumber());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.POST_LINE_DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("SUCCESS")) {
                    Log.i("Response", response.toString());
                    Toast.makeText(getContext(), "ডাটা সেভ হয়েছে!", Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences = getActivity().getSharedPreferences("outputdata", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("buyer", obj.getBuyer());
                    editor.putString("style", obj.getStyleNo());
                    editor.putString("orderno", obj.getOrderNumber());
                    editor.putString("color", obj.getColor());
                    editor.commit();

                    getLineData();
                    removeViews();
                } else {
                    Toast.makeText(getContext(), "ডাটা সেভ হয়নি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("InsertIndividualEntry", error.toString());
                Toast.makeText(getContext(), "ডাটা সেভ হয়নি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
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
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
                String supervisor = sharedPreferences.getString("supervisorId", "");
                String styleNo = sharedPreferences.getString("styleNo", "");
                String lineNo = sharedPreferences.getString("lineNo", "");
                String buildingNo = sharedPreferences.getString("buildingNo", "");
                String unitNo = sharedPreferences.getString("unitNo", "");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = df.format(new Date()).toString();

                SharedPreferences factoryPref = getActivity().getSharedPreferences("factoryPref", MODE_PRIVATE);
                String factoryCode = factoryPref.getString("factoryCode", "");

                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("supervisor", supervisor);
                params.put("lineNo", lineNo);
                params.put("buildingNo", buildingNo);
                params.put("unitNo", unitNo);
                params.put("factoryCode", factoryCode);
                params.put("uniqueKey", "out" + lineNo +supervisor + styleNo + obj.getOrderNumber() + obj.getColor() + obj.getHour() + currentDate);
                Log.i("jsonStringOutput", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void removeViews() {
        editTextOutput.setText("");
        /*editTextStyle.setText("");
        editTextBuyer.setText("");
        editTextColor.setText("");
        editTextPO.setText("");*/
        problemTypeSpinner.setSelection(0);
        problemsSpinner.setSelection(0);
        statusSpinner.setSelection(0);
    }

    public void getLineData() {
        arrayListLineData.clear();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        final String styleNo = sharedPreferences.getString("styleNo", "");
        final String superVisorId = sharedPreferences.getString("supervisorId", "");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(new Date()).toString();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = Endpoints.GET_LINE_RECORD + "?styleNo=" + styleNo +
                "&entryTime=" + currentDate+"&supervisorId="+superVisorId;
        url = url.replace(" ", "%20");
        Log.i("urlLineOutput", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("responseLineDataOut", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        if (response.getJSONObject(i).getString("output").length() > 0) {
                            idList.add(response.getJSONObject(i).getString("id"));
                            String data = response.getJSONObject(i).getString("hour") +
                                    ", Output: " + response.getJSONObject(i).getString("output") +
                                    "\nInput color: " + response.getJSONObject(i).getString("color")+
                                    "\nProblem Type: " + response.getJSONObject(i).getString("problemType") +
                                    ", Prob: " + response.getJSONObject(i).getString("problem") +
                                    "\nStatus: " + response.getJSONObject(i).getString("status");
                            arrayListLineData.add(data);
                            adapterLineData.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e("jsonOutputLineData", e.toString());
                    }
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
