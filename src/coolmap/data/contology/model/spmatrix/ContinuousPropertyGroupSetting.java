/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Keqiang Li
 */
public class ContinuousPropertyGroupSetting extends PropertyGroupSetting <Double> {

    private double _min;
    private double _max;

    public ContinuousPropertyGroupSetting(String propType, double min, double max) {
        super(propType);
        this._min = min;
        this._max = max;
    }
    
    public ContinuousPropertyGroupSetting(String propType) {
        super(propType);
    }
    
    public void setMax(double max) {
        _max = max;
    }
    
    public void setMin(double min) {
        _min = min;
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

    @Override
    public SamplePropertyGroup assignGroup(SamplePropertyGroup curGroup, Double propertyValue) {
        return curGroup;
    }
    
    public static ContinuousPropertyGroupSetting importGroupSettingFromTextFile(String propType, FileInputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        ContinuousPropertyGroupSetting groupSetting = new ContinuousPropertyGroupSetting(propType);

        String line;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) {
                    continue;
                }
                int index = line.indexOf(":");
                String groupName = line.substring(0, index).trim();
                String groupRange = line.substring(index + 1).trim();
                
                index = groupRange.indexOf(",");
                String minString = groupRange.substring(1, index);
                String maxString = groupRange.substring(index + 1, groupRange.length() - 1);
                
                double curMin = Double.parseDouble(minString);
                double curMax = Double.parseDouble(maxString);
                ContinuousSamplePropertyGroup currentGroup = new ContinuousSamplePropertyGroup(groupName, curMin, curMax);
                
                
                if (curMin < min) {
                    min = curMin;
                }
                
                if (curMax > max) {
                    max = curMax;
                }
                
                groupSetting.addGroup(currentGroup);
            }
            reader.close();
        } catch (Exception e) {
            return null;
        }

        groupSetting.setMax(max);
        groupSetting.setMin(min);
        
        
        return groupSetting;
    }
}
