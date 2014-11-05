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
public class MoveMultiPropertyAction extends AbstractAction {
    
    public MoveMultiPropertyAction() {
        super("move multi property");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "move multi property");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LongTask task = new LongTask("moving multi property...") {

            @Override
            public void run() {

                CSamplePropertyMatrix samplePropertyMatrix = SamplePropertyMaster.getFirst();
                samplePropertyMatrix.moveMultiPropertyToIndex(2, 2, 0);
               

            }
        };

        TaskEngine.getInstance().submitTask(task);
    }
    
    
}
