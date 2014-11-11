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
    private HashSet<String> _allValues;

    public CategorizedSamplePropertyGroup(String customizedName, String ID) {
        super(customizedName);
        this._uniqueID = ID;
    }
    
    public CategorizedSamplePropertyGroup() {
        super("");
        _uniqueID = "";
    }
    
    @Override
    public String getUniqueID() {
        return _uniqueID;
    }
    
    public void setUniqueID(String ID) {
        _uniqueID = ID;
    }
    
    public void setValues(HashSet<String> values) {
        _allValues = values;
    }
    
    @Override
    public String toString() {
        return getCustomizedName();
    }

    @Override
    public boolean contains(String value) {
        return _allValues.contains(value);
    }
}
