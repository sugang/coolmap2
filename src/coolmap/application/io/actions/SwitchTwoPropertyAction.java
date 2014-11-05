/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions;

import coolmap.application.SamplePropertyMaster;
import coolmap.application.utils.LongTask;
import coolmap.application.utils.TaskEngine;
import coolmap.data.contology.model.spmatrix.CSamplePropertyMatrix;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Keqiang Li
 */
public class SwitchTwoPropertyAction extends AbstractAction{
    
    public SwitchTwoPropertyAction() {
        super("switch two property");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "switch 2 property");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LongTask task = new LongTask("switching 2 property...") {

            @Override
            public void run() {

                CSamplePropertyMatrix samplePropertyMatrix = SamplePropertyMaster.getFirst();
                samplePropertyMatrix.movePropertyToIndex(1, 2);
               

            }
        };

        TaskEngine.getInstance().submitTask(task);
    }
    
}
        