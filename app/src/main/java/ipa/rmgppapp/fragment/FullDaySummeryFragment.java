package ipa.rmgppapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ipa.rmgppapp.R;
import ipa.rmgppapp.helper.Endpoints;

import static android.content.Context.MODE_PRIVATE;

public class FullDaySummeryFragment extends Fragment {

    TextView lineTargetTotal, lineOutputTotal, lineWip, remainingTarget, revisedTarget, remainingHours;
    Button buttonRefresh;

    public FullDaySummeryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.fragment_full_day_summery, container, false);

        lineTargetTotal = customView.findViewById(R.id.lineTargetTotal);
        lineOutputTotal = customView.findViewById(R.id.lineOutputTotal);
        remainingTarget = customView.findViewById(R.id.lineWIP);
        remainingHours = customView.findViewById(R.id.remainingHours);
        revisedTarget = customView.findViewById(R.id.revisedTarget);
        lineWip = customView.findViewById(R.id.lineWIP);
        buttonRefresh = customView.findViewById(R.id.buttonRefresh);

        getSummeryData();

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSummeryData();
            }
        });
        return customView;
    }

    private void getSummeryData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("supervisor", MODE_PRIVATE);
        String styleNo = sharedPreferences.getString("styleNo", "");
        String supervisorId = sharedPreferences.getString("supervisorId", "");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(new Date()).toString();

        String getUrl = Endpoints.GET_SUMMERY_DATA + "?styleNo=" + styleNo + "&entryTime=" + currentDate+ "&supervisorId="+supervisorId;
        getUrl = getUrl.replace(" ", "%20");
        Log.i("summeryYrl", getUrl);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int totalQuantity = 0;
                    if(jsonObject.getString("totalQuantity")!=null){
                        totalQuantity = Integer.parseInt(jsonObject.getString("totalQuantity"));
                    }
                    int totalHours = Integer.parseInt(jsonObject.getString("totalHours"));
                    int hours = Integer.parseInt(jsonObject.getString("hours"));
                    int totalTarget = Integer.parseInt(jsonObject.getString("totalTarget"));
                    lineOutputTotal.setText(totalQuantity + "");
                    lineTargetTotal.setText(totalTarget + "");
                    remainingTarget.setText((totalTarget - totalQuantity) + "");
                    remainingHours.setText((totalHours - hours) + "");
                    revisedTarget.setText(((totalTarget - totalQuantity) / (totalHours - hours)) + "");
                    } catch (Exception e) {
                    Log.e("SummeryData", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SummeryDataVolley", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }
}
