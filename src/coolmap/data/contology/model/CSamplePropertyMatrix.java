/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * This data object should be maintained during users modifying the order of
 * properties thus generating the new ontology for properties
 *
 * @author Keqiang Li
 */
public class CSamplePropertyMatrix {

    // each sample-property table will be named as the imported file's name
    public final String name;

    // use the property name as key, each entry contains what this property value is for each sample
    private final LinkedHashMap<String, ArrayList<String>> _propValuesForEachSample;
    // names of samples, mainly used for displaying
    private final ArrayList<String> _sampleNames;
    // current order of the properties
    private final ArrayList<String> _propOrder;
    // reflected ontology of the current property order
    private COntology _ontology;
    // propUniqValues are the unique property values a property could have, property name as the key
    private final LinkedHashMap<String, LinkedHashSet<String>> _propUniqValues;

    public CSamplePropertyMatrix(String name, LinkedHashMap propValuesForEachSample,
            ArrayList sampleNames, ArrayList propOrder, LinkedHashMap propUniqValues) {
        this.name = name;
        this._propValuesForEachSample = propValuesForEachSample;
        this._sampleNames = sampleNames;
        this._propOrder = propOrder;
        this._propUniqValues = propUniqValues;
        // generate ontology based on property order
        _reGenerateOntology();
    }

    public ArrayList<String> getSampleNames() {
        return _sampleNames;
    }

    public ArrayList<String> getPropNames() {
        return _propOrder;
    }

    public COntology getOntology() {
        return _ontology;
    }

    // modify the order of property
    public void switchProperty(int index1, int index2) {
        if (index1 < _propOrder.size() && index2 < _propOrder.size() && index1 >= 0 && index2 >= 0) {

            String tmpName = _propOrder.get(index1);
            _propOrder.set(index1, _propOrder.get(index2));
            _propOrder.set(index2, tmpName);

            _reGenerateOntology();
        }
    }

    // modify the order of property
    public void movePropertyToIndex(int from, int to) {
        if (from == to) return;
        if (from < _propOrder.size() && to < _propOrder.size() && from >= 0 && to >= 0) {

            String tmpName = _propOrder.get(from);
            if (from > to) {
                for (int i = from; i > to; --i) {
                    _propOrder.set(i, _propOrder.get(i - 1));
                }
            } else {
                for (int i = from; i < to; ++i) {
                    _propOrder.set(i, _propOrder.get(i + 1));
                }
            }
            _propOrder.set(to, tmpName);

            _reGenerateOntology();
        }
    }
    
    // modify the order of property
    public void moveMultiPropertyToIndex(int from, int len, int to) {
        if (from == to) return;
        if (from < _propOrder.size() && to < _propOrder.size() && from >= 0 && to >= 0
                && from + len <= _propOrder.size()) {
            
            ArrayList <String> tmpRemoved = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                tmpRemoved.add(_propOrder.remove(from));
            }
            
            if (to > _propOrder.size()) {
                to = _propOrder.size();
            }
            
            _propOrder.addAll(to, tmpRemoved);
            
            _reGenerateOntology();
        }
    }

    // invoked each time user modifies the order of property
    private void _reGenerateOntology() {
        String ontologyName = _propOrder.toString();
        _ontology = new COntology(ontologyName, "default ontology generated on properties");

        _depthFirstBuildOntology("fakeRoot", 0);
        _mapSamplesToOntology();

        _ontology.validate();
    }

    // recursively build up the ontology tree
    private void _depthFirstBuildOntology(String prefix, int curPropIndex) {
        if (curPropIndex >= _propOrder.size()) {
            return;
        }

        ArrayList<String> curProp = new ArrayList<>(_propUniqValues.get(_propOrder.get(curPropIndex)));
        for (String value : curProp) {
            String curLevelLable = prefix + "-" + value;
            _ontology.addRelationshipNoUpdateDepth(prefix, curLevelLable);
            _depthFirstBuildOntology(curLevelLable, curPropIndex + 1);
        }
    }

    // as long as the ontology has been generated, samples can be mapped to
    // leaf nodes
    private void _mapSamplesToOntology() {
        for (int i = 0; i < _sampleNames.size(); ++i) {
            String prefix = "Root";
            for (int j = 0; j < _propOrder.size(); ++j) {
                ArrayList<String> curProp = new ArrayList<>(_propValuesForEachSample.get(_propOrder.get(j)));
                prefix = prefix + "-" + curProp.get(i);
            }

            _ontology.addRelationshipNoUpdateDepth(prefix, prefix + "-" + _sampleNames.get(i));
        }
    }
}