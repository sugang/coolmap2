/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model;

import coolmap.application.widget.impl.console.CMConsole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * This data object should be maintained during users modifying the order of
 * properties thus generating the new ontology for properties
 *
 * @author Keqiang Li
 */
public class CSamplePropertyMatrix {

    // continurity of the property
    public static final String PROPERTY_CONTINUITY_CONTINUOUS = "con";
    public static final String PROPERTY_CONTINUITY_CATEGORIZED = "cat";
    // each sample-property table will be named as the imported file's name
    public final String name;
    // use the property name as key, each entry contains what this property value is for each sample
    private final LinkedHashMap<String, ArrayList<String>> _propValuesForEachSample;
    // names of samples, mainly used for displaying
    private final ArrayList<String> _sampleNames;
    // current order of the properties
    private final ArrayList<String> _propOrder;
    // whether the property is continuous or categorized
    private final HashMap<String, String> _propertyContinuity;
    // reflected ontology of the current property order
    private COntology _ontology;
    // propUniqValues are the unique property values a property could have, property name as the key
    private final LinkedHashMap<String, LinkedHashSet<String>> _propUniqValues;
    // the grouping information for a certain type of property.
    private LinkedHashMap<String, ArrayList<SamplePropertyGroup> > _propertyGroupInfo;

    public CSamplePropertyMatrix(String name, LinkedHashMap propValuesForEachSample,
            ArrayList sampleNames, ArrayList propOrder, LinkedHashMap propUniqValues, HashMap propertyContinuity) {
        this.name = name;
        this._propValuesForEachSample = propValuesForEachSample;
        this._sampleNames = sampleNames;
        this._propOrder = propOrder;
        this._propUniqValues = propUniqValues;
        this._propertyContinuity = propertyContinuity;
        
        _initializeDefaultPropertyGroupInfo();
        // generate ontology based on property order
        _reGenerateOntology();
    }
    
    // user has not specify any group information, instead, every group contains 1 single value
    private void _initializeDefaultPropertyGroupInfo() {
        _propertyGroupInfo = new LinkedHashMap ();
        for (String type : _propOrder) {
            ArrayList<SamplePropertyGroup> groupList = new ArrayList<>();
            switch (_propertyContinuity.get(type)) {
                case PROPERTY_CONTINUITY_CATEGORIZED:
                    for (String value : _propUniqValues.get(type)) {
                        SamplePropertyGroup<String> tmpGroup = new CategorizedSamplePropertyGroup(value);
                        groupList.add(tmpGroup);
                    }   break;
                case PROPERTY_CONTINUITY_CONTINUOUS:
                    for (String value : _propUniqValues.get(type)) {
                        SamplePropertyGroup<Double> tmpGroup = new ContinuousSamplePropertyGroup("" + value, Double.parseDouble(value), Double.parseDouble(value));
                        groupList.add(tmpGroup);
                    }   break;
                default:
                    CMConsole.logError("invalid property continuity value");
                    continue;
            }
            _propertyGroupInfo.put(type, groupList);
        }
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

        _depthFirstBuildOntology("Root", 0);
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
    
    // user customized property group. users may group values from 1 to 100 as group "1-100"
    private abstract class SamplePropertyGroup <T> {
        public String customizedName;
        
        public SamplePropertyGroup(String name) {
            customizedName = name;
        }
        
        public abstract boolean contains(T value);
    }
    
    private class ContinuousSamplePropertyGroup extends SamplePropertyGroup <Double> {
        private double _min;
        private double _max;
        
        public ContinuousSamplePropertyGroup (String name, double min, double max) {
            super(name);
            this._min = min;
            this._max = max;
        }
        
        public boolean setMin(double min) {
            if (min <= _max) {
                _min = min;
                return true;
            }
            return false;
        }
        
        public boolean setMax(double max) {
            if (max >= _min) {
                _max = max;
                return true;
            }
            return false;
        }
        
        @Override
        public boolean contains (Double value) {
            return value >= _min && value <= _max;
        }
    }

    private class CategorizedSamplePropertyGroup extends SamplePropertyGroup <String> {
        private HashSet<String> _group;
        
        public CategorizedSamplePropertyGroup(String name) {
            super(name);
            _group = new HashSet<>();
        }
        
        public void addValue(String value) {
            _group.add(value);
        }
        
        public void removeValue (String value) {
            if (_group.contains(value)) {
                _group.remove(value);
            }
        }
        
        @Override
        public boolean contains(String value) {
            return _group.contains(value);
        }
        
    }
    
    public abstract class SampleProperty <T> {
        public String type; // type of this property, such as the type of MALE is GENDER
        private SamplePropertyGroup<T> _group;
        public T value;

        public SampleProperty(String type, T value) {
            this.type = type;
            this.value = value;
        }
        
        public void setGroup (SamplePropertyGroup group) {
            _group = group;
        }
        
        public String getDisplayName() {
            if (_group != null) {
                return _group.customizedName;
            }
            return getDisplayName();
        }

        public abstract String getDisplayValue();
    }
    
    public class CategorizedSampleProperty extends SampleProperty<String> {
        public CategorizedSampleProperty(String type, String value) {
            super(type, value);
        }
        
        public String getDisplayValue() {
            return value;
        }
    }

    public class ContinuousSampleProperty extends SampleProperty<Double> {

        public ContinuousSampleProperty(String type, double value) {
            super(type, value);
        }

        public String getDisplayValue() {
            return "" + value;
        }
    }
}