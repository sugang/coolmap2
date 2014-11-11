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
import java.util.HashMap;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedPropertyGroupSetting extends PropertyGroupSetting {

    // stores all the parent-children relations

    private final HashMultimap<String, String> _groupTree = HashMultimap.create();
    private final HashMap<String, CategorizedSamplePropertyGroup> _groups = new HashMap<>();

    public CategorizedPropertyGroupSetting(String propType) {
        super(propType);
    }

    // add a branch starting from a parent to children
    public void addGroupBranch(String parentGroupID, String childGroupID) {
        _groupTree.put(parentGroupID, childGroupID);
    }

    /**
     * @author Keqiang Li.
     * @param propType to which property the settings will be applied 
     * @param in the input stream from the OBO file
     * @return the imported OBO file as a tree
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
                        String customizedName = line.substring(index + 1).trim();
                        currentGroup.setCustomizedName(customizedName);
                    } else if (line.startsWith("is_a")) {
                        line = line.substring(index + 1);
                        groupSetting.addGroupBranch(line, currentChildTerm);                       
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            return null;
        }
        
        return groupSetting;
    }
}
