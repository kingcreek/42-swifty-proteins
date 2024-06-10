package es.kingcreek.swifty_proteins.periodictable;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import es.kingcreek.swifty_proteins.R;
import es.kingcreek.swifty_proteins.models.Element;

public class PeriodicTableUtils {
    private static final String TAG = "PeriodicTableUtils";
    private static PeriodicTable periodicTable;

    // SOURCE https://raw.githubusercontent.com/Bowserinator/Periodic-Table-JSON/master/PeriodicTableJSON.json
    private static void loadJson(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.periodictable);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Gson gson = new Gson();
            periodicTable = gson.fromJson(reader, PeriodicTable.class);
        } catch (Exception e) {

        }
    }

    // Obtener un elemento por su símbolo
    public static Element getElementBySymbol(Context context, String symbol) {
        if (periodicTable == null) {
            loadJson(context);
        }
        List<Element> elements = periodicTable.getElements();
        for (Element element : elements) {
            if (element.getSymbol().equalsIgnoreCase(symbol)) {
                return element;
            }
        }
        return null;
    }
}