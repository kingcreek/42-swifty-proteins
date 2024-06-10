package es.kingcreek.swifty_proteins.helper;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import es.kingcreek.swifty_proteins.R;

public class CPKColor {

    private static final Map<String, Integer> elementColorMap = new HashMap<>();

    // Colors based on CPK's https://es.wikipedia.org/wiki/Esquema_de_colores_CPK
    static {
        // Map individual elements
        elementColorMap.put("H", R.color.white);
        elementColorMap.put("C", R.color.black);
        elementColorMap.put("N", R.color.dark_blue);
        elementColorMap.put("O", R.color.red);
        elementColorMap.put("F", R.color.green);
        elementColorMap.put("Cl", R.color.green);
        elementColorMap.put("Br", R.color.dark_red);
        elementColorMap.put("I", R.color.dark_violet);
        elementColorMap.put("P", R.color.orange);
        elementColorMap.put("S", R.color.yellow);
        elementColorMap.put("Ti", R.color.gray);
        elementColorMap.put("Fe", R.color.orange);

        // Map groups of elements
        elementColorMap.put("He", R.color.turquoise);
        elementColorMap.put("Ne", R.color.turquoise);
        elementColorMap.put("Ar", R.color.turquoise);
        elementColorMap.put("Xe", R.color.turquoise);
        elementColorMap.put("Kr", R.color.turquoise);

        // Alkali metals
        elementColorMap.put("Li", R.color.violet);
        elementColorMap.put("Na", R.color.violet);
        elementColorMap.put("K", R.color.violet);
        elementColorMap.put("Rb", R.color.violet);
        elementColorMap.put("Cs", R.color.violet);

        // Alkaline earth metals
        elementColorMap.put("Be", R.color.dark_green);
        elementColorMap.put("Mg", R.color.dark_green);
        elementColorMap.put("Ca", R.color.dark_green);
        elementColorMap.put("Sr", R.color.dark_green);
        elementColorMap.put("Ba", R.color.dark_green);
        elementColorMap.put("Ra", R.color.dark_green);

        // Transition metals and boron
        elementColorMap.put("B", R.color.peach);
        String[] transitionMetals = {"Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "La", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Ac", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"};
        for (String element : transitionMetals) {
            elementColorMap.put(element, R.color.peach);
        }
    }

    // Simple function to get color from map based on the atom element
    public static int getColor(Context context, String element) {
        Integer colorResId = elementColorMap.get(element);
        if (colorResId != null) {
            return ContextCompat.getColor(context, colorResId);
        } else {
            return ContextCompat.getColor(context, R.color.pink);
        }
    }
}