/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.external;

import coolmap.data.contology.model.COntology;
import coolmap.data.contology.model.CSamplePropertyMatrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;

/**
 *
 * @author Keqiang Li
 */
public class ImportSamplePropertyFromFile {
    public static COntology importSamplePropertyFromFile (File file) throws Exception {
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        ArrayList<String> propOrder = new ArrayList<>();
        if ((line = reader.readLine()) != null) {
            try {
                propOrder.addAll(Arrays.asList(line.split("\t", -1)));
                propOrder.remove(0);
                // doesn't have any properties
                if (propOrder.size() < 1) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        
        LinkedHashMap <String, ArrayList<String>> propValuesForEachSample = new LinkedHashMap<>();
        
        ArrayList<String> sampleNames = new ArrayList<>();
       
        // propUniqValues are the property values a property could have
        LinkedHashMap <String, LinkedHashSet<String>> propUniqValues = new LinkedHashMap <> ();        
        
        while ((line = reader.readLine()) != null) {
            try {
                String[] elements = line.split("\t", -1);
                // data is valid
                if (elements.length == propOrder.size() + 1) {
                    sampleNames.add(elements[0]); // add sample names
                    ArrayList<String> dataLine = new ArrayList<>(Arrays.asList(elements));
                    dataLine.remove(0);
                    LinkedHashSet curSet;
                    ArrayList<String> curList;
                    for (int i = 0; i < propOrder.size(); ++i) {
                        if (propValuesForEachSample.get(propOrder.get(i)) == null) {
                            curList = new ArrayList<>();
                            propValuesForEachSample.put(propOrder.get(i), curList);
                        } else {
                            curList = propValuesForEachSample.get(propOrder.get(i));
                        }
                        curList.add(dataLine.get(i));
                        
                        if (propUniqValues.get(propOrder.get(i)) == null) {
                             curSet = new LinkedHashSet<> ();
                             propUniqValues.put(propOrder.get(i), curSet);
                        } else {
                            curSet = propUniqValues.get(propOrder.get(i));
                        }
                        curSet.add(dataLine.get(i));
                    }
                    if(Thread.interrupted()){
                        return null;
                    }
                }
            } catch (Exception e) {
//                System.out.println(line + " malformed");
            }
        }
        reader.close();
        
        CSamplePropertyMatrix samplePropertyMatrix = new CSamplePropertyMatrix(file.getPath(), propValuesForEachSample, sampleNames, propOrder, propUniqValues);
        
        samplePropertyMatrix.movePropertyToIndex(3, 1);
        
        return samplePropertyMatrix.getOntology();
    }
}
