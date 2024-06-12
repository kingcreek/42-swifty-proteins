package es.kingcreek.swifty_proteins.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.kingcreek.swifty_proteins.models.Atom;

public class PDBParser {

    private static final String TAG = "PDBParser";
    public static ArrayList<Atom> parsePDB(String urlString) {
        ArrayList<Atom> atoms = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            List<String> connectionsData = new ArrayList<>();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ATOM")) {
                    int serial = Integer.parseInt(line.substring(6, 11).trim());
                    String name = line.substring(12, 16).trim();
                    char altLoc = line.charAt(16);
                    String resName = line.substring(17, 20).trim();
                    char chainID = line.charAt(21);
                    int resSeq = Integer.parseInt(line.substring(22, 26).trim());
                    char iCode = line.charAt(26);
                    float x = Float.parseFloat(line.substring(30, 38).trim());
                    float y = Float.parseFloat(line.substring(38, 46).trim());
                    float z = Float.parseFloat(line.substring(46, 54).trim());
                    float occupancy = Float.parseFloat(line.substring(54, 60).trim());
                    float tempFactor = Float.parseFloat(line.substring(60, 66).trim());
                    String element = line.substring(76, 78).trim();

                    Atom atom = new Atom(serial, name, altLoc, resName, chainID, resSeq, iCode, x, y, z, occupancy, tempFactor, element);
                    atoms.add(atom);
                } else if (line.startsWith("CONECT")) {
                    connectionsData.add(line);
                }
            }
            // Parse connections after all atoms have been parsed
            parseConnections(connectionsData, atoms);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return atoms;
    }

    private static void parseConnections(List<String> connectionsData, List<Atom> atoms) {
        for (String line : connectionsData) {
            String[] parts = line.split("\\s+");
            int sourceAtomSerial = Integer.parseInt(parts[1]);

            // Find atom
            Atom sourceAtom = null;
            for (Atom atom : atoms) {
                if (atom.serial == sourceAtomSerial) {
                    sourceAtom = atom;
                    break;
                }
            }

            // If the atom is found, add its connections
            if (sourceAtom != null) {
                for (int i = 2; i < parts.length; i++) {
                    int connectedAtomSerial = Integer.parseInt(parts[i]);
                    for (Atom atom : atoms) {
                        if (atom.serial == connectedAtomSerial) {
                            sourceAtom.addConnection(atom);
                            break;
                        }
                    }
                }
            }
        }
    }
}