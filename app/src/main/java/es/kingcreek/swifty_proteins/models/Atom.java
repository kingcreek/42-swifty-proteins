package es.kingcreek.swifty_proteins.models;

import java.util.ArrayList;
import java.util.List;

public class Atom {
    public int serial;
    public String name;
    public char altLoc;
    public String resName;
    public char chainID;
    public int resSeq;
    public char iCode;
    public float x, y, z;
    public float occupancy;
    public float tempFactor;
    public String element;
    public  List<Atom> connections;

    public Atom(int serial, String name, char altLoc, String resName, char chainID, int resSeq, char iCode,
                float x, float y, float z, float occupancy, float tempFactor, String element) {
        this.serial = serial;
        this.name = name;
        this.altLoc = altLoc;
        this.resName = resName;
        this.chainID = chainID;
        this.resSeq = resSeq;
        this.iCode = iCode;
        this.x = x;
        this.y = y;
        this.z = z;
        this.occupancy = occupancy;
        this.tempFactor = tempFactor;
        this.element = element;
        this.connections = new ArrayList<>();
    }

    public void addConnection(Atom atom) {
        connections.add(atom);
    }
}
