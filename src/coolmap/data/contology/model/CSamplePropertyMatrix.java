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

    public static final int NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE = 10;
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
    private LinkedHashMap<String, ArrayList<SamplePropertyGroup>> _propertyGroupInfo;
    // all properties objects
    private LinkedHashMap<String, ArrayList<SampleProperty>> _sampleProperties;
    // all properties objects using sample name as key
    private LinkedHashMap<String, ArrayList<SampleProperty>> _sampleNameToProperties;

    public CSamplePropertyMatrix(String name, LinkedHashMap propValuesForEachSample,
            ArrayList sampleNames, ArrayList propOrder, LinkedHashMap propUniqValues, HashMap propertyContinuity) {
        this.name = name;
        this._propValuesForEachSample = propValuesForEachSample;
        this._sampleNames = sampleNames;
        this._propOrder = propOrder;
        this._propUniqValues = propUniqValues;
        this._propertyContinuity = propertyContinuity;

        // generate default property group
        _initializeDefaultPropertyGroupInfo();
        // using the passed-in arguments to generate group information and other info
        _generateProps();
        // organize the groups of properties
        _organizeGroup();
        // generate ontology based on property order
        _reGenerateOntology();
    }
    
    // user has not specify any group information, instead, every group contains 1 single value
    private void _initializeDefaultPropertyGroupInfo() {
        _propertyGroupInfo = new LinkedHashMap();
        for (String propType : _propOrder) {
            ArrayList<SamplePropertyGroup> groupList = new ArrayList<>();
            switch (_propertyContinuity.get(propType)) {
                case PROPERTY_CONTINUITY_CATEGORIZED:
                    for (String value : _propUniqValues.get(propType)) {
                        CategorizedSamplePropertyGroup tmpGroup = new CategorizedSamplePropertyGroup(value);
                        tmpGroup.addValue(value);
                        groupList.add(tmpGroup);
                    }
                    break;
                case PROPERTY_CONTINUITY_CONTINUOUS:
                    if (_propUniqValues.get(propType).size() > NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE) {
                        groupList = _fakeDefaultGroup(propType);
                        break;
                    }
                    for (String value : _propUniqValues.get(propType)) {
                        ContinuousSamplePropertyGroup tmpGroup = new ContinuousSamplePropertyGroup("" + value, Double.parseDouble(value), Double.parseDouble(value));
                        groupList.add(tmpGroup);
                    }
                    break;
                default:
                    CMConsole.logError("invalid property continuity value");
                    continue;
            }
            _propertyGroupInfo.put(propType, groupList);
        }
    }

    // generate property objects based on values
    private void _generateProps() {
        _sampleProperties = new LinkedHashMap<>();
        _sampleNameToProperties = new LinkedHashMap<>();
        for (String propType : _propOrder) {
            ArrayList<String> values = _propValuesForEachSample.get(propType);
            boolean isContinuum = _propertyContinuity.get(propType).equals("con");
            ArrayList<SampleProperty> tmpProperties = new ArrayList<>();
            for (int i = 0; i < values.size(); ++i) {
                String value = values.get(i);
                String sampleName = _sampleNames.get(i);
                SampleProperty property;
                if (isContinuum) {
                    property = new ContinuousSampleProperty(propType, Double.parseDouble(value));
                } else {
                    property = new CategorizedSampleProperty(propType, value);
                }
                tmpProperties.add(property);
                ArrayList<SampleProperty> propertiesOfCurSample = _sampleNameToProperties.get(sampleName);
                if (propertiesOfCurSample == null) {
                    propertiesOfCurSample = new ArrayList<>();
                    _sampleNameToProperties.put(sampleName, propertiesOfCurSample);
                }
                propertiesOfCurSample.add(property);
            }
            _sampleProperties.put(propType, tmpProperties);
        }
    }

    // assign groups to properties
    private void _organizeGroup() {
        for (String propType : _propOrder) {
            ArrayList<SampleProperty> tmpProperties = _sampleProperties.get(propType);
            ArrayList<SamplePropertyGroup> groups = _propertyGroupInfo.get(propType);

            for (SampleProperty prop : tmpProperties) {
                for (SamplePropertyGroup group : groups) {
                    if (group.contains(prop.value)) {
                        prop.setGroup(group);
                        break;
                    }
                }
            }
        }
    }

    // function to generate group
    private ArrayList _fakeDefaultGroup(String propType) {
        ArrayList<SamplePropertyGroup<Double>> result = new ArrayList<>();

        if (_propUniqValues.get(propType).size() <= 0) {
            return result;
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (String value : _propUniqValues.get(propType)) {
            double curValue = Double.parseDouble(value);
            if (curValue < min) {
                min = curValue;
            }
            if (curValue >= max) {
                max = curValue;
            }
        }

        min = Math.floor(min) - 1;
        max = Math.ceil(max);

        long increment = (int) Math.ceil((max - min) / NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE);
        long curMin = (long) min;
        for (int i = 0; i < 10; ++i) {
            long curMax = curMin + increment;
            result.add(new ContinuousSamplePropertyGroup("(" + curMin + ", " + curMax + "]", curMin, curMax));
            curMin = curMax;
        }

        return result;
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
        if (from == to) {
            return;
        }
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
        if (from == to) {
            return;
        }
        if (from < _propOrder.size() && to < _propOrder.size() && from >= 0 && to >= 0
                && from + len <= _propOrder.size()) {

            ArrayList<String> tmpRemoved = new ArrayList<>();
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

        ArrayList<SamplePropertyGroup> curPropGroups = new ArrayList<>(_propertyGroupInfo.get(_propOrder.get(curPropIndex)));
        for (SamplePropertyGroup group : curPropGroups) {
            String curLevelLable = prefix + "-" + group.customizedName;
            _ontology.addRelationshipNoUpdateDepth(prefix, curLevelLable);
            _depthFirstBuildOntology(curLevelLable, curPropIndex + 1);
        }
    }

    // as long as the ontology has been generated, samples can be mapped to
    // leaf nodes
    private void _mapSamplesToOntology() {
        for (String sampleName : _sampleNames) {
            String prefix = "Root";
            ArrayList<SampleProperty> properties = _sampleNameToProperties.get(sampleName);
            for (SampleProperty property : properties) {
                prefix = prefix + "-" + property.getDisplayName();
            }    
            _ontology.addRelationshipNoUpdateDepth(prefix, prefix + "-" + sampleName);
        }
    }

    // user customized property group. users may group values from 1 to 100 as group "1-100"
    private abstract class SamplePropertyGroup<T> {

        public String customizedName;

        public SamplePropertyGroup(String name) {
            customizedName = name;
        }

        public abstract boolean contains(T value);
    }

    private class ContinuousSamplePropertyGroup extends SamplePropertyGroup<Double> {

        private double _min;
        private double _max;

        public ContinuousSamplePropertyGroup(String name, double min, double max) {
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
        public boolean contains(Double value) {
            return value > _min && value <= _max;
        }
    }

    private class CategorizedSamplePropertyGroup extends SamplePropertyGroup<String> {

        private final HashSet<String> _group;

        public CategorizedSamplePropertyGroup(String name) {
            super(name);
            _group = new HashSet<>();
        }

        public void addValue(String value) {
            _group.add(value);
        }

        public void removeValue(String value) {
            if (_group.contains(value)) {
                _group.remove(value);
            }
        }

        @Override
        public boolean contains(String value) {
            return _group.contains(value);
        }

    }

    public abstract class SampleProperty<T> {

        public String propType; // type of this property, such as the type of MALE is GENDER
        private SamplePropertyGroup<T> _group;
        public T value;

        public SampleProperty(String propType, T value) {
            this.propType = propType;
            this.value = value;
        }

        public void setGroup(SamplePropertyGroup<T> group) {
            _group = group;
        }

        public String getDisplayName() {
            if (_group != null) {
                return _group.customizedName;
            }
            return getDisplayValue();
        }

        public abstract String getDisplayValue();
    }

    public class CategorizedSampleProperty extends SampleProperty<String> {

        public CategorizedSampleProperty(String propType, String value) {
            super(propType, value);
        }

        @Override
        public String getDisplayValue() {
            return value;
        }
    }

    public class ContinuousSampleProperty extends SampleProperty<Double> {

        public ContinuousSampleProperty(String propType, double value) {
            super(propType, value);
        }

        @Override
        public String getDisplayValue() {
            return "" + value;
        }
    }
}
