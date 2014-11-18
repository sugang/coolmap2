/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Keqiang Li
 * @param <T>
 */
public abstract class PropertyGroupSetting <T> {

    private final String _propType;
    private final HashMap<String, SamplePropertyGroup> _groups;

    public PropertyGroupSetting(String propType) {
        this._propType = propType;
        _groups = new HashMap<>();
    }
    
    public void clear() {
        _groups.clear();
    }

    public void addAllGroups(ArrayList<SamplePropertyGroup> groups) {
        for (SamplePropertyGroup group : groups) {
            addGroup(group);
        }
    }
    
    public void addGroup(SamplePropertyGroup newGroup) {
        _groups.put(newGroup.getUniqueID(), newGroup);
    }

    public void setWithNewGroups(ArrayList<SamplePropertyGroup> newGroups) {
        clear();
        addAllGroups(newGroups);
    }

    public int getGroupNum() {
        return _groups.size();
    }

    public Collection<SamplePropertyGroup> getGroups() {
        return _groups.values();
    }
    
    public SamplePropertyGroup getGroup(String groupID) {
        return _groups.get(groupID);
    }
    
    public abstract SamplePropertyGroup assignGroup(SamplePropertyGroup curGroup, T propertyValue);
}
