/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.util.HashSet;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedSamplePropertyGroup extends SamplePropertyGroup<String> {
    // imported from OBO files, so every group has an unique ID
    private String _uniqueID;
    private String _uniqueName;
    
    private String _parent;
    private HashSet<String> _allValues;

    public CategorizedSamplePropertyGroup(String name, String ID) {
        // use the unique name as the display name as default
        super(name);
        this._uniqueName = name;
        this._uniqueID = ID;
        setDisplayName(_uniqueName);
    }
    
    public CategorizedSamplePropertyGroup() {
        super("");
        _uniqueID = "";
        _uniqueName = "";
        setDisplayName(_uniqueName);
    }
    
    public HashSet<String> getAllValues() {
        if (_allValues == null) _allValues = new HashSet<>();
        return _allValues;
    }
    
    public void addValue (String value) {
        getAllValues().add(value);
    }
    
    @Override
    public String getUniqueID() {
        return _uniqueID;
    }
    
    public void setUniqueID(String ID) {
        _uniqueID = ID;
    }
    
    public void setUniqueName(String name) {
        _uniqueName = name;
    }
    
    public String getUniqueName() {
        return _uniqueName;
    }
    
    public void setParent(String parent) {
        _parent = parent;
    }
    
    public String getParent() {
        return _parent;
    }
    
    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean contains(String value) {
        return getAllValues().contains(value);
    }
}
