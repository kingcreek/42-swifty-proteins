package es.kingcreek.swifty_proteins.periodictable;

import java.util.List;

import es.kingcreek.swifty_proteins.models.Element;

public class PeriodicTable {

    // Basic class to Gson
    private List<Element> elements;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}