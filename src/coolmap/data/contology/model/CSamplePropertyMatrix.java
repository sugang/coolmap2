/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model;

import coolmap.application.CoolMapMaster;
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

    private boolean _isAdded = false;
    // each sample-property table will be named as the imported file's name
    private final String _matrixName;

    // continurity of the property 
    public static final String PROPERTY_CONTINUITY_CONTINUOUS = "con";
    public static final String PROPERTY_CONTINUITY_CATEGORIZED = "cat";

    // if a continuous property has more that 10 unique values, it will be automatically grouped
    public static final int NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE = 10;
    // use the property name as key, each entry contains what this property value is for each sample
    private final LinkedHashMap<String, ArrayList<String>> _propNameToPropValues;
    // names of samples, mainly used for displaying
    private final ArrayList<String> _sampleNames;
    // current order of the properties
    private final ArrayList<String> _propOrder;
    // whether the property is continuous or categorized
    private final HashMap<String, String> _propContinuity;
    // reflected ontology of the current property order
    private COntology _ontology;
    // propUniqValues are the unique property values a property could have, property name as the key
    private final LinkedHashMap<String, LinkedHashSet<String>> _propUniqValues;
    // the grouping information for a certain type of property.
    private LinkedHashMap<String, PropertyGroupSetting> _propGroupInfo;
    // all properties objects
    private LinkedHashMap<String, ArrayList<SampleProperty>> _propNameToProperties;
    // all properties objects using sample name as key
    private LinkedHashMap<String, ArrayList<SampleProperty>> _sampleNameToProperties;

    public CSamplePropertyMatrix(String matrixName, LinkedHashMap propNameToPropValues,
            ArrayList sampleNames, ArrayList propOrder, LinkedHashMap propUniqValues, HashMap propContinuity) {
        this._matrixName = matrixName;
        this._propNameToPropValues = propNameToPropValues;
        this._sampleNames = sampleNames;
        this._propOrder = propOrder;
        this._propUniqValues = propUniqValues;
        this._propContinuity = propContinuity;

        /* generate default property group, default groups will be generated only
         once when the data are read from the files */
        _initializeDefaultPropertyGroupInfo();
        // using the passed-in arguments to generate property objects
        _generateProps();
        // assign group to each input property
        _assignGroup();

        _setUp();
    }
    
    public ArrayList<String> getPropertyValuesForSample(String sampleName) {
        ArrayList<String> result = new ArrayList<>();
        
        for (SampleProperty property : _sampleNameToProperties.get(sampleName)) {
            result.add(property.getDisplayValue());
        }
        return result;
    }

    public void setIsAdded(boolean isAdded) {
        _isAdded = isAdded;
    }

    public String getMatrixName() {
        return _matrixName;
    }

    // configurations need to be done each time properties are reordered
    private void _setUp() {
        // relink properties
        _linkProperties();
        // generate ontology based on property order
        _reGenerateOntology();
    }

    // user has not specify any group information, instead, every group contains 1 single value
    private void _initializeDefaultPropertyGroupInfo() {
        _propGroupInfo = new LinkedHashMap();
        for (String propType : _propOrder) {
            PropertyGroupSetting setting;
            switch (_propContinuity.get(propType)) {
                case PROPERTY_CONTINUITY_CATEGORIZED:
                    setting = _generateDefaultGroupForCatagorizedProp(propType);
                    break;
                case PROPERTY_CONTINUITY_CONTINUOUS:
                    setting = _generateDefaultGroupForContinuousProp(propType);
                    break;
                default:
                    CMConsole.logError("invalid property continuity value");
                    continue;
            }

            _propGroupInfo.put(propType, setting);
        }
    }

    private void _linkProperties() {
        _sampleNameToProperties = new LinkedHashMap<>();
        for (String propType : _propOrder) {
            ArrayList<SampleProperty> propertiesOfType = _propNameToProperties.get(propType);
            for (int i = 0; i < propertiesOfType.size(); ++i) {
                SampleProperty prop = propertiesOfType.get(i);
                String sampleName = _sampleNames.get(i);
                ArrayList<SampleProperty> propertiesOfCurSample = _sampleNameToProperties.get(sampleName);
                if (propertiesOfCurSample == null) {
                    propertiesOfCurSample = new ArrayList<>();
                    _sampleNameToProperties.put(sampleName, propertiesOfCurSample);
                }
                propertiesOfCurSample.add(prop);
            }
        }
    }

    // generate property objects based on values
    private void _generateProps() {
        _propNameToProperties = new LinkedHashMap<>();
        for (String propType : _propOrder) {
            ArrayList<String> values = _propNameToPropValues.get(propType);
            boolean isContinuum = _propContinuity.get(propType).equals("con");
            ArrayList<SampleProperty> tmpProperties = new ArrayList<>();
            for (int i = 0; i < values.size(); ++i) {
                String value = values.get(i);
                SampleProperty property;
                if (isContinuum) {
                    property = new ContinuousSampleProperty(propType, Double.parseDouble(value));
                } else {
                    property = new CategorizedSampleProperty(propType, value);
                }
                tmpProperties.add(property);
            }
            _propNameToProperties.put(propType, tmpProperties);
        }
    }

    // assign groups to properties
    private void _assignGroup() {
        for (String propType : _propOrder) {
            ArrayList<SampleProperty> tmpProperties = _propNameToProperties.get(propType);
            ArrayList<SamplePropertyGroup> groups = _propGroupInfo.get(propType).getGroups();

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

    // function to generate groups for continuous property
    private PropertyGroupSetting _generateDefaultGroupForCatagorizedProp(String propType) {
        PropertyGroupSetting setting = new CategorizedPropertyGroupSetting(propType);
        ArrayList<SamplePropertyGroup<String>> groupList = new ArrayList<>();

        for (String value : _propUniqValues.get(propType)) {
            HashSet<String> set = new HashSet<>();
            set.add(value);
            CategorizedSamplePropertyGroup tmpGroup = new CategorizedSamplePropertyGroup(set.toString());
            tmpGroup.addAll(set);
            groupList.add(tmpGroup);
        }
        setting.addAll(groupList);
        return setting;
    }

    // function to generate groups for continuous property
    private ContinuousPropertyGroupSetting _generateDefaultGroupForContinuousProp(String propType) {
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

        ContinuousPropertyGroupSetting setting = new ContinuousPropertyGroupSetting(propType, min, max);
        ArrayList<SamplePropertyGroup<Double>> groupList = new ArrayList<>();

        if (_propUniqValues.get(propType).size() <= NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE) {
            for (String value : _propUniqValues.get(propType)) {
                ContinuousSamplePropertyGroup tmpGroup = new ContinuousSamplePropertyGroup("" + value, Double.parseDouble(value), Double.parseDouble(value));
                groupList.add(tmpGroup);
            }
        } else {
            min = Math.floor(min) - 1;
            max = Math.ceil(max);

            long increment = (int) Math.ceil((max - min) / NUMBER_LIMIT_OF_UNIQUE_CONTINUOUS_VALUE);
            long curMin = (long) min;
            for (int i = 0; i < 10; ++i) {
                long curMax = curMin + increment;
                groupList.add(new ContinuousSamplePropertyGroup("(" + curMin + ", " + curMax + "]", curMin, curMax));
                curMin = curMax;
            }
        }

        setting.addAll(groupList);
        return setting;
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

            _setUp();
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

            _setUp();
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

            _setUp();
        }
    }

    // invoked each time user modifies the order of property
    private void _reGenerateOntology() {
        if (_isAdded) {
            CoolMapMaster.destroyCOntology(_ontology);
        }

        String ontologyName = _propOrder.toString();
        _ontology = new COntology(ontologyName, "default ontology generated on properties");

        _depthFirstBuildOntology("Root", 0);
        _mapSamplesToOntology();

        _ontology.validate();
        
        if (_isAdded) {
            CoolMapMaster.addNewCOntology(_ontology);
        }
    }

    // recursively build up the ontology tree
    private void _depthFirstBuildOntology(String prefix, int curPropIndex) {
        if (curPropIndex >= _propOrder.size()) {
            return;
        }

        ArrayList<SamplePropertyGroup> curPropGroups = _propGroupInfo.get(_propOrder.get(curPropIndex)).getGroups();
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
        
        public void addAll(HashSet<String> values) {
            _group.addAll(values);
        }
        
        public void clear() {
            _group.clear();
        }
        
        public int getSize() {
            return _group.size();
        }

        @Override
        public boolean contains(String value) {
            return _group.contains(value);
        }

    }

    private abstract class SampleProperty<T> {

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
    
    private void _updateGroups() {
        _assignGroup();
        _reGenerateOntology();
    }
    
    public boolean setCatePropGroup(String catePropType, ArrayList<HashSet> sets) {
        if (!_propContinuity.get(catePropType).equals(PROPERTY_CONTINUITY_CATEGORIZED)) {
            return false;
        }
        CategorizedPropertyGroupSetting setting = (CategorizedPropertyGroupSetting)_propGroupInfo.get(catePropType);
        if (setting.setWithSets(sets)) {
            _propGroupInfo.put(catePropType, setting);
            _updateGroups();
            return true;
        }
        return false;
    }

    public boolean setContPropGroup(String contPropType, ArrayList<Double> values) {
        if (!_propContinuity.get(contPropType).equals(PROPERTY_CONTINUITY_CONTINUOUS)) {
            return false;
        }
        ContinuousPropertyGroupSetting setting = (ContinuousPropertyGroupSetting) _propGroupInfo.get(contPropType);
        if (setting.setWithMarks(values)) {
            _propGroupInfo.put(contPropType, setting);
            _updateGroups();
            return true;
        }
        return false;
    }

    private class PropertyGroupSetting {
        private final String _propType;
        private final ArrayList<SamplePropertyGroup> _groups;

        public PropertyGroupSetting(String propType) {
            this._propType = propType;
            _groups = new ArrayList<>();
        }

        public void addAll(ArrayList groups) {
            _groups.addAll(groups);
        }

        public void clear() {
            _groups.clear();
        }

        public void addGroup(SamplePropertyGroup newGroup) {
            _groups.add(newGroup);
        }

        public void setWithNewGroups(ArrayList<SamplePropertyGroup> newGroups) {
            clear();
            addAll(newGroups);
        }

        public int getGroupNum() {
            return _groups.size();
        }

        public ArrayList<SamplePropertyGroup> getGroups() {
            return _groups;
        }
    }
    
    private class CategorizedPropertyGroupSetting extends PropertyGroupSetting {
        private final HashSet<String> _allValues;
        
        public CategorizedPropertyGroupSetting(String propType) {
            super(propType);
            _allValues = _propUniqValues.get(propType);
        }
        
        public boolean setWithSets(ArrayList<HashSet> sets) {
            ArrayList<SamplePropertyGroup> newGroups = new ArrayList<>();

            for (HashSet set : sets) {
                if (!_allValues.containsAll(set)) {
                    return false;
                }
                CategorizedSamplePropertyGroup group = new CategorizedSamplePropertyGroup(set.toString());
                group.addAll(set);
                newGroups.add(group);
            }

            setWithNewGroups(newGroups);
            return true;
        }
    }

    private class ContinuousPropertyGroupSetting extends PropertyGroupSetting {

        private final double _min;
        private final double _max;

        public ContinuousPropertyGroupSetting(String propType, double min, double max) {
            super(propType);
            this._min = min;
            this._max = max;
        }

        public double getMin() {
            return _min;
        }

        public double getMax() {
            return _max;
        }

        public boolean setWithMarks(ArrayList<Double> marks) {
            ArrayList<SamplePropertyGroup> newGroups = new ArrayList<>();

            marks.add(marks.size(), _max);
            marks.add(0, _min - 1);
            for (int i = 0; i < marks.size() - 1; ++i) {
                if (marks.get(i) > marks.get(i + 1)) {
                    return false;
                }
                SamplePropertyGroup group = new ContinuousSamplePropertyGroup("(" + marks.get(i) + "," + marks.get(i + 1) + "]", marks.get(i), marks.get(i + 1));
                newGroups.add(group);
            }

            setWithNewGroups(newGroups);
            return true;
        }
    }
}
