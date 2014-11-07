/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedPropertyGroupTest extends SamplePropertyGroup<String> {
    private final HashSet<String> _thisLevelGroup;
    private final ArrayList<CategorizedPropertyGroupTest> _subGroups;

    public CategorizedPropertyGroupTest(HashSet<String> flatGroup) {
        super(flatGroup.toString());
        this._thisLevelGroup = flatGroup;
        this._subGroups = new ArrayList<>();
    }
    
    public void addSubGroup(HashSet<String> flatGroup) {
        CategorizedPropertyGroupTest newSubGroup = new CategorizedPropertyGroupTest(flatGroup);
        addSubGroup(newSubGroup);
    }
    
    public void addSubGroup(CategorizedPropertyGroupTest subGroup) {
        _subGroups.add(subGroup);
        _thisLevelGroup.addAll(subGroup.getThisLevelGroup());
    }
    
    public HashSet<String> getThisLevelGroup() {
        return _thisLevelGroup;
    }
    
    public ArrayList<CategorizedPropertyGroupTest> getSubGroups() {
        return _subGroups;
    }
    
    public int getNumberOfSubGroups() {
        return _subGroups.size();
    }
    
    public int getNumberOfValues() {
        return _thisLevelGroup.size();
    }

    @Override
    public boolean contains(String value) {
        return _thisLevelGroup.contains(value);
    }
    
    public void combineWith(CategorizedPropertyGroupTest anotherGroup) {
        _thisLevelGroup.addAll(anotherGroup.getThisLevelGroup());
        _subGroups.addAll(anotherGroup.getSubGroups());
    }
    
    public void addValue(String value) {
        if (!_thisLevelGroup.contains(value)) {
            _thisLevelGroup.add(value);
            HashSet<String> newFlatGroup = new HashSet<>();
            newFlatGroup.add(value);
            CategorizedPropertyGroupTest newGroup = new CategorizedPropertyGroupTest(newFlatGroup);
            _subGroups.add(newGroup);
        }
    }

    // recursively add the value
    public void removeValue(String value) {
        if (_thisLevelGroup.contains(value)) {
            _thisLevelGroup.remove(value);
            
            for (CategorizedPropertyGroupTest group : _subGroups) {
                if(group.contains(value)) {
                    group.removeValue(value);
                    if (group.getNumberOfValues() <= 0) {
                        _subGroups.remove(group);
                    }
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        return _thisLevelGroup.toString();
    }
}
