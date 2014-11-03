/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.spmatrix;

import java.util.ArrayList;

/**
 *
 * @author Keqiang Li
 */
public class PropertyGroupSetting {

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
