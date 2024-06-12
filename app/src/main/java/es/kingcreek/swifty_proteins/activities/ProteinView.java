package es.kingcreek.swifty_proteins.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ProcessLifecycleOwner;

import java.util.List;

import es.kingcreek.swifty_proteins.MyApplication;
import es.kingcreek.swifty_proteins.R;
import es.kingcreek.swifty_proteins.interfaces.AtomCallback;
import es.kingcreek.swifty_proteins.models.Atom;
import es.kingcreek.swifty_proteins.helper.CPKColor;
import es.kingcreek.swifty_proteins.helper.PDBParser;
import es.kingcreek.swifty_proteins.models.Element;
import es.kingcreek.swifty_proteins.observers.AppLifecycleObserver;
import es.kingcreek.swifty_proteins.periodictable.PeriodicTableUtils;
import es.kingcreek.swifty_proteins.renderer.SceneRender;
import es.kingcreek.swifty_proteins.share.ShareHelper;
import es.kingcreek.swifty_proteins.views.CustomPopup;


import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;

public class ProteinView extends AppCompatActivity implements AtomCallback {


    private final String TAG = "ProteinView";
    private AppLifecycleObserver appLifecycleObserver;

    private String baseUrl = "https://files.rcsb.org/ligands/%s/%s/%s_ideal.pdb";
    private SceneView sceneView;
    private SceneRender sceneRenderer;
    private boolean HiddeHydrogen = false;
    private ProgressBar progressBar;
    private ImageView share, toggleHydrogen, toggle;

    public static final int REQUEST_CODE_SHARE = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protein);

        MyApplication.getInstance().addActivity(this);

        sceneView = findViewById(R.id.sceneView);

        // get lifecycle
        appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

        // Init views
        progressBar     = findViewById(R.id.progressBar);
        toggleHydrogen  = findViewById(R.id.toggleHydrogen);
        share           = findViewById(R.id.share);
        toggle          = findViewById(R.id.toggle);

        // Set init view as invisible
        toggleHydrogen.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        toggle.setVisibility(View.GONE);

        // Init scene
        initScene();

        // Getting protein
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String protein = bundle.getString("protein");

            // Build url
            if (protein != null) {
                String firstChar = protein.substring(0, 1);
                String formattedUrl = String.format(baseUrl, firstChar, protein, protein);
                // Execute task to process web request, download ligand data, parse and show
                new LoadPDBTask().execute(formattedUrl);
            } else {
                Toast.makeText(this, "Protein not provided", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No bundle received", Toast.LENGTH_LONG).show();
        }

        //////////////////
        // CLICK EVENTS //
        //////////////////

        // Share
        share.setOnClickListener(view -> {
            ShareHelper.captureAndShareSceneView(this, sceneView, appLifecycleObserver);
        });

        // Hidde hydrogen like rcsb
        toggleHydrogen.setOnClickListener(view -> {
            sceneRenderer.toggleHydrogen(HiddeHydrogen);
            HiddeHydrogen = !HiddeHydrogen;
        });

        // Toggle move/rotate mode
        toggle.setOnClickListener(view -> {
            sceneRenderer.toggleMode();
            // True = rotation, then show move icon
            if (sceneRenderer.getMode())
                toggle.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.move));
            else
                toggle.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.rotate));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SHARE) {
            // Nice, now set share boolean to false again to allow close app if user leave
            appLifecycleObserver.setSharing(false);
        }
    }

    private void initScene() {
        // Simply initialize scene
        sceneRenderer = new SceneRender();
        sceneRenderer.initSceneView(this, sceneView, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // If we have scene, resume it
        if (sceneView != null) {
            try {
                sceneView.resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause scene
        if (sceneView != null) {
            sceneView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
        // Destroy it!
        if (sceneView != null) {
            sceneView.destroy();
        }
    }


    @Override
    public void onAtomClicked(String atomSymbol) {
        // This is a callback for clicked atom in scene to show information about it
        Element element = PeriodicTableUtils.getElementBySymbol(this, atomSymbol);
        if(element == null)
            return;
        CustomPopup customPopup = new CustomPopup(ProteinView.this, element);
        customPopup.setCanceledOnTouchOutside(true);
        customPopup.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_bg);
        customPopup.show();
    }

    @Override
    public void onViewLoaded() {
        // When sceneView notify the scene are loaded, hidde progressbar and show share and toggle images
        progressBar.setVisibility(View.GONE);
        toggleHydrogen.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        toggle.setVisibility(View.VISIBLE);
    }

    // Class to get data in background
    private class LoadPDBTask extends AsyncTask<String, Void, List<Atom>> {

        @Override
        protected List<Atom> doInBackground(String... urls) {
            // Call parser class to get a List of atoms, using url as param
            return PDBParser.parsePDB(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Atom> atoms) {
            // Ok we catch! now we have a list of atoms in requested protein, let's draw it
            if (atoms != null && atoms.size() > 0) {
                //First, iterate over all atoms
                for (Atom atom : atoms) {
                    // Draw spheres
                    Vector3 atomPos = new Vector3(atom.x, atom.y, atom.z);
                    sceneRenderer.setSphere(getApplicationContext(), atomPos, CPKColor.getColor(getApplicationContext(), atom.element), atom.element);
                    // Every atom have connections with other atoms, then let's iterate over it to draw cylinders
                    for(Atom connection : atom.connections) {
                        Vector3 pointStart = new Vector3(atom.x, atom.y, atom.z);
                        Vector3 pointEnd = new Vector3(connection.x, connection.y, connection.z);
                        String name = (atom.element.equals("H") || connection.element.equals("H")) ? "cylinderH" : "cylinder";
                        sceneRenderer.setCylinder(getApplicationContext(), pointStart, pointEnd, CPKColor.getColor(getApplicationContext(), atom.element), name);
                    }
                }
                // We request all atoms and connections, let's set a callback to know when atoms are drawed
                sceneRenderer.onAtomsRendered();
            } else {
                // Ops, cant load atoms, show warning message and go back
                Toast.makeText(getApplicationContext(), "Failed to load atoms, probably .pdb file are empty.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}