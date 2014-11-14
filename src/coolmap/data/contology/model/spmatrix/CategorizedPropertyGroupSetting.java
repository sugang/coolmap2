/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

import com.google.common.collect.HashMultimap;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedPropertyGroupSetting extends PropertyGroupSetting<String> {

    // stores all the parent-children relations
    private final HashMultimap<String, String> _groupTree = HashMultimap.create();
    private String _rootNodeUniqueID;

    public CategorizedPropertyGroupSetting(String propType) {
        super(propType);
    }

    // add a branch starting from a parent to children
    public void addGroupBranch(String parentGroupID, String childGroupID) {
        _groupTree.put(parentGroupID, childGroupID);
    }
    
    public String getRootName() {
        return _rootNodeUniqueID;
    }
    
    public Set<String> getChildren(String nodeName) {
        return _groupTree.get(nodeName);
    }

    /**
     * @author Keqiang Li.
     * @param propType to which property the settings will be applied
     * @param in the input stream from the OBO file
     * @return the imported OBO file as a tree structured data object
     *
     */
    public static CategorizedPropertyGroupSetting importGroupSettingFromOBOFile(String propType, InputStream in) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        CategorizedSamplePropertyGroup currentGroup = null;
        CategorizedPropertyGroupSetting groupSetting = new CategorizedPropertyGroupSetting(propType);

        String line;
        String currentChildTerm = null;

        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    if (currentGroup != null) {
                        groupSetting.addGroup(currentGroup); //if it's not null, add a new entry
                        currentGroup = null;
                    }
                } else if (line.startsWith("[Term]")) {
                    currentGroup = new CategorizedSamplePropertyGroup();
                } else if (currentGroup != null) {
                    int index = line.indexOf(":");
                    if (line.startsWith("id")) {
                        currentChildTerm = line.substring(index + 1).trim();
                        currentGroup.setUniqueID(currentChildTerm);
                    } else if (line.startsWith("name")) {
                        String uniqueName = line.substring(index + 1).trim();
                        currentGroup.setUniqueName(uniqueName);
                        currentGroup.setDisplayName(uniqueName);
                    } else if (line.startsWith("is_a")) {
                        line = line.substring(index + 1);
                        currentGroup.setParent(line);
                        groupSetting.addGroupBranch(line, currentChildTerm);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            return null;
        }

        if (currentGroup != null) {
            groupSetting.addGroup(currentGroup); //if it's not null, add a new entry
        }
        groupSetting.validate();
        return groupSetting;
    }

    @Override
    public SamplePropertyGroup assignGroup(SamplePropertyGroup curGroup, String propertyValue) {
        Set<String> childGroups = _groupTree.get(curGroup.getUniqueID());
        for (String child : childGroups) {
            CategorizedSamplePropertyGroup childGroup = (CategorizedSamplePropertyGroup) getGroup(child);
            if (childGroup != null && childGroup.contains(propertyValue)) {
                return assignGroup(childGroup, propertyValue);
            }
        }

        return curGroup;
    }

    public void validate() {
        for (SamplePropertyGroup<String> group : getGroups()) {
            String curGroupID = group.getUniqueID();
            Set<String> curGroupChildSet = _groupTree.get(curGroupID);
            // if current group has no children, it's a leaf node group then.
            if (curGroupChildSet == null || curGroupChildSet.isEmpty()) {
                ((CategorizedSamplePropertyGroup) group).addValue(group.getUniqueID());
                _addValuesToParents(group.getUniqueID());
            }
        }
        
       CategorizedSamplePropertyGroup group = (CategorizedSamplePropertyGroup)getGroups().toArray()[0];
       while (group.getParent() != null && !group.getParent().equals("")) {
           group = (CategorizedSamplePropertyGroup)getGroup(group.getParent());
       }
       _rootNodeUniqueID = group.getUniqueID();
    }

    private void _addValuesToParents(String childID) {
        CategorizedSamplePropertyGroup curGroup = (CategorizedSamplePropertyGroup) getGroup(childID);
        String parentID = curGroup.getParent();
        CategorizedSamplePropertyGroup parentGroup = (CategorizedSamplePropertyGroup) getGroup(parentID);

        while (parentGroup != null) {
            parentGroup.addValue(childID);
            parentGroup = (CategorizedSamplePropertyGroup) getGroup(parentGroup.getParent());
        }
    }
}
