package ipa.rmgppapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ipa.rmgppapp.R;

public class CustomSpinnerAdapter extends BaseAdapter {

    ArrayList<Integer> arr = new ArrayList<>();
    Context context;

    public CustomSpinnerAdapter(ArrayList<Integer> arr, Context context) {
        this.arr = arr;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.spinner_item_layout, null);

        TextView text1 = customView.findViewById(R.id.text1);
        text1.setText(arr.get(position)+"");
        return customView;
    }
}
