/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions;

import coolmap.application.CoolMapMaster;
import coolmap.application.utils.LongTask;
import coolmap.application.utils.TaskEngine;
import coolmap.data.contology.spmatrix.CSamplePropertyMatrix;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.AbstractAction;

/**
 *
 * @author Keqiang Li
 */
public class SetGroupSettingForProperty extends AbstractAction{
    public SetGroupSettingForProperty() {
        super("set group setting");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "set group setting");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LongTask task = new LongTask("set group setting") {

            @Override
            public void run() {

                Double[] tmp = {35.7, 48.2, 67.3, 80.0};
                ArrayList<Double> newGroup = new ArrayList<>(Arrays.asList(tmp));
                CSamplePropertyMatrix samplePropertyMatrix = CoolMapMaster.getFirst();
                samplePropertyMatrix.setContPropGroup("BMI", newGroup);
               
                
                HashSet<String> set1 = new HashSet<>();
                set1.add("MUSCLE");
                set1.add("OTHER");
                
                HashSet<String> set2 = new HashSet<>();
                set2.add("FAT");
                
                ArrayList<HashSet> arrList = new ArrayList<>();
                arrList.add(set1);
                arrList.add(set2);
                
                samplePropertyMatrix.setCatePropGroup("TOS", arrList);

            }
        };

        TaskEngine.getInstance().submitTask(task);
    }
    
}
