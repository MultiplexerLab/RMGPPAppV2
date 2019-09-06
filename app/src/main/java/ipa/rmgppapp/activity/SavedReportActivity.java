package ipa.rmgppapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.OnScrollListener;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ipa.rmgppapp.R;

public class SavedReportActivity extends AppCompatActivity {
    private static final String[] TABLE_HEADERS = { "SL No" , "Ship Date","Buyer", "Style", "Order", "Target", "Output", "Difference" };

    private static final String[][] DATA_TO_SHOW = { { "1", "10 August, 2018", "OVS", "OVS123", "PO123" , "10000",  "7000", "3000"},
            { "2", "30 August, 2018", "SICEM", "OVS124", "PO124" , "20000",  "15000", "5000"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_report);

        TableView tableView = (TableView) findViewById(R.id.tableView);

        tableView.addOnScrollListener(new SavedReportActivity.MyOnScrollListener());
        TableColumnWeightModel columnModel = new TableColumnWeightModel(8);
        columnModel.setColumnWeight(1, 2);
        columnModel.setColumnWeight(2, 2);
        tableView.setColumnModel(columnModel);

        TableColumnDpWidthModel columnModel1 = new TableColumnDpWidthModel(this, 8, 100);
        columnModel1.setColumnWidth(0, 80);
        tableView.setColumnModel(columnModel1);

        tableView.setDataAdapter(new SimpleTableDataAdapter(this, DATA_TO_SHOW));
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
    }

    private class MyOnScrollListener implements OnScrollListener {
        @Override
        public void onScroll(final ListView tableDataView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
            // listen for scroll changes
        }

        @Override
        public void onScrollStateChanged(final ListView tableDateView, final ScrollState scrollState) {
            // listen for scroll state changes
        }
    }
}
