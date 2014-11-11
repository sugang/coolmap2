/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import coolmap.application.CoolMapMaster;
import coolmap.application.widget.impl.console.CMConsole;
import coolmap.data.contology.model.COntology;
import java.util.ArrayList;
import java.util.Collection;
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
                    setting = _generateDefaultGroupForCategorizedProp(propType);
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
            Collection<SamplePropertyGroup> groups = _propGroupInfo.get(propType).getGroups();

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

    // function to generate default groups for categorized property
    private PropertyGroupSetting _generateDefaultGroupForCategorizedProp(String propType) {
        PropertyGroupSetting setting = new CategorizedPropertyGroupSetting(propType);

        for (String value : _propUniqValues.get(propType)) {
            CategorizedSamplePropertyGroup tmpGroup = new CategorizedSamplePropertyGroup(value, value);
            HashSet<String> values = new HashSet<>();
            values.add(value);
            tmpGroup.setValues(values);
            setting.addGroup(tmpGroup);
        }

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
        ArrayList<SamplePropertyGroup> groupList = new ArrayList<>();

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

        setting.addAllGroups(groupList);
        return setting;
    }

    public ArrayList<String> getSampleNames() {
        return _sampleNames;
    }

    public ArrayList<String> getPropOrder() {
        return _propOrder;
    }

    /**
     * takes a new order of properties and apply it to current data matrix
     * ontology regeneration takes place if the order is different from the original order
     * @param newOrder the new order of the properties
     */
    public void setPropOrder(ArrayList<String> newOrder) {
    
        if (_propOrder == newOrder || _propOrder.equals(newOrder)) {
            return;
        }
       
        _propOrder.clear();
        _propOrder.addAll(newOrder);
        _setUp();
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

        Collection<SamplePropertyGroup> curPropGroups = _propGroupInfo.get(_propOrder.get(curPropIndex)).getGroups();
        for (SamplePropertyGroup group : curPropGroups) {
            String curLevelLable = prefix + "-" + group.getCustomizedName();
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

    private void _updateGroups() {
        _assignGroup();
        _reGenerateOntology();
    }

    public void setPropGroup(String catePropType, PropertyGroupSetting groupSetting) {
        _propGroupInfo.put(catePropType, groupSetting);
        _updateGroups();
    }

    
    /*
    public boolean setCatePropGroup(int index, ArrayList<HashSet> sets) {
        if (index < 0 || index > _propOrder.size() - 1) {
            return false;
        }
        return setCatePropGroup(_propOrder.get(index), sets);
    }

    public boolean setCatePropGroup(String catePropType, ArrayList<HashSet> sets) {
        if (!_propContinuity.get(catePropType).equals(PROPERTY_CONTINUITY_CATEGORIZED)) {
            return false;
        }
        CategorizedPropertyGroupSetting setting = (CategorizedPropertyGroupSetting) _propGroupInfo.get(catePropType);
        if (setting.setWithSets(sets)) {
            _propGroupInfo.put(catePropType, setting);
            _updateGroups();
            return true;
        }
        return false;
    } */

    public boolean setContPropGroup(int index, ArrayList<Double> values) {
        if (index < 0 || index > _propOrder.size() - 1) {
            return false;
        }
        return setContPropGroup(_propOrder.get(index), values);
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

    public PropertyGroupSetting getGroupSettingForProperty(int col) {
        if (col < 0 || col > _propOrder.size() - 1) {
            return null;
        }
        String propType = _propOrder.get(col);
        return getGroupSettingForProperty(propType);
    }

    public PropertyGroupSetting getGroupSettingForProperty(String propType) {
        return _propGroupInfo.get(propType);
    }

    public String getPropType(int index) {
        if (index < 0 || index > _propOrder.size() - 1) {
            return null;
        }
        return _propOrder.get(index);
    }
    
    public boolean isCategorizedProp(int index) {
        String propType = _propOrder.get(index);
        return isCategorizedProp(propType);
    }
    
    public boolean isCategorizedProp(String propType) {
        if (_propContinuity.containsKey(propType)) {
            String continuity = _propContinuity.get(propType);
            switch(continuity) {
                case PROPERTY_CONTINUITY_CONTINUOUS:
                    return false;
                case PROPERTY_CONTINUITY_CATEGORIZED:
                    return true;
            }
        }
        
        return false;
    }
}
