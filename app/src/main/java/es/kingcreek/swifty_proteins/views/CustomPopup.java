package es.kingcreek.swifty_proteins.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.MessageFormat;

import es.kingcreek.swifty_proteins.R;
import es.kingcreek.swifty_proteins.models.Element;

public class CustomPopup extends Dialog {

    private final Element element;

    public CustomPopup(@NonNull Context context, Element element) {
        super(context);
        this.element = element;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_information);

        // Inicializar vistas
        TextView nameTextView = findViewById(R.id.text_name);
        TextView atomicMassTextView = findViewById(R.id.text_atomic_mass);
        TextView boilTextView = findViewById(R.id.text_boil);
        TextView categoryTextView = findViewById(R.id.text_category);
        TextView densityTextView = findViewById(R.id.text_density);
        TextView discoveredByTextView = findViewById(R.id.text_discovered_by);
        TextView meltTextView = findViewById(R.id.text_melt);
        TextView molarHeatTextView = findViewById(R.id.text_molar_heat);
        TextView namedByTextView = findViewById(R.id.text_named_by);
        TextView numberTextView = findViewById(R.id.text_number);
        TextView periodTextView = findViewById(R.id.text_period);
        TextView phaseTextView = findViewById(R.id.text_phase);
        TextView summaryTextView = findViewById(R.id.text_summary);
        TextView symbolTextView = findViewById(R.id.text_symbol);

        // Asignar valores a los TextViews concatenando el texto actual con el valor de cada campo
        nameTextView.setText(MessageFormat.format("{0}: {1}", nameTextView.getText().toString(), element.getName()));
        atomicMassTextView.setText(MessageFormat.format("{0}: {1}", atomicMassTextView.getText().toString(), element.getAtomic_mass()));
        boilTextView.setText(MessageFormat.format("{0}: {1}", boilTextView.getText().toString(), element.getBoil()));
        categoryTextView.setText(MessageFormat.format("{0}: {1}", categoryTextView.getText().toString(), element.getCategory()));
        densityTextView.setText(MessageFormat.format("{0}: {1}", densityTextView.getText().toString(), element.getDensity()));
        discoveredByTextView.setText(MessageFormat.format("{0}: {1}", discoveredByTextView.getText().toString(), element.getDiscovered_by()));
        meltTextView.setText(MessageFormat.format("{0}: {1}", meltTextView.getText().toString(), element.getMelt()));
        molarHeatTextView.setText(MessageFormat.format("{0}: {1}", molarHeatTextView.getText().toString(), element.getMolar_heat()));
        namedByTextView.setText(MessageFormat.format("{0}: {1}", namedByTextView.getText().toString(), element.getNamed_by()));
        numberTextView.setText(MessageFormat.format("{0}: {1}", numberTextView.getText().toString(), element.getNumber()));
        periodTextView.setText(MessageFormat.format("{0}: {1}", periodTextView.getText().toString(), element.getPeriod()));
        phaseTextView.setText(MessageFormat.format("{0}: {1}", phaseTextView.getText().toString(), element.getPhase()));
        summaryTextView.setText(MessageFormat.format("{0}: {1}", summaryTextView.getText().toString(), element.getSummary()));
        symbolTextView.setText(MessageFormat.format("{0}: {1}", symbolTextView.getText().toString(), element.getSymbol()));
    }
}