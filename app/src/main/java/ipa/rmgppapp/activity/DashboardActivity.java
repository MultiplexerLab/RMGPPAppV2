package ipa.rmgppapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ipa.rmgppapp.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu:
                return true;
            case R.id.save:
                return true;
            case R.id.send:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void newReport(View view) {
        Intent intent = new Intent(DashboardActivity.this, StyleListActivity.class);
        startActivity(intent);
    }

    public void savedReport(View view) {
        Intent intent = new Intent(DashboardActivity.this, SavedReportActivity.class);
        startActivity(intent);
    }

    public void cancelDashboard(View view) {
        finish();
    }
}
