/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.util.ArrayList;

/**
 *
 * @author Keqiang Li
 */
public class ContinuousPropertyGroupSetting extends PropertyGroupSetting {

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
