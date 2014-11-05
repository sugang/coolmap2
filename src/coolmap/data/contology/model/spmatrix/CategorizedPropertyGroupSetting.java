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
public class CategorizedPropertyGroupSetting extends PropertyGroupSetting {

    private final HashSet<String> _allValues;

    public CategorizedPropertyGroupSetting(String propType, HashSet<String> allValues) {
        super(propType);
        this._allValues = allValues;
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
