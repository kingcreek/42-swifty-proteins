package es.kingcreek.swifty_proteins.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.kingcreek.swifty_proteins.MyApplication;
import es.kingcreek.swifty_proteins.R;
import es.kingcreek.swifty_proteins.adapters.ProteinAdapter;
import es.kingcreek.swifty_proteins.helper.NetworkUtils;
import es.kingcreek.swifty_proteins.interfaces.AdapterHandler;


public class MainActivity extends AppCompatActivity implements AdapterHandler {

    private final String TAG = "MainActivity";

    // Data
    List<String> proteins;
    List<String> filteredProteins;

    // Views
    private Toolbar toolbar;
    RecyclerView rvProteins;
    ProteinAdapter protAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getInstance().addActivity(this);

        toolbar = findViewById(R.id.toolbar);
        rvProteins = findViewById(R.id.recyclerViewProteins);

        // Toolbar
        setSupportActionBar(toolbar);

        //Load provided file and fill rv
        try {
            loadData();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "OOPS!! Error readding file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return true;}

            @Override
            public boolean onQueryTextChange(String newText) {
                protAdapter.filter(newText);
                return true;
            }
        });
        return true;
    }

    private void loadData() throws Exception{
        proteins            = new ArrayList<>();
        filteredProteins    = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.ligands);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            proteins.add(line);
            filteredProteins.add(line);
        }
        reader.close();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvProteins.setLayoutManager(layoutManager);

        protAdapter = new ProteinAdapter(getApplicationContext(), this, proteins, filteredProteins);
        rvProteins.setAdapter(protAdapter);
    }

    @Override
    public void onItemClicked(String protein) {
        if(NetworkUtils.isNetworkAvailable(this)) {
            Intent i = new Intent(this, ProteinView.class);
            i.putExtra("protein", protein);
            startActivity(i);
        } else {
            Toast.makeText(this, "Please, check your internet connection.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

}