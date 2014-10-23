/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions;

import coolmap.application.CoolMapMaster;
import coolmap.application.utils.LongTask;
import coolmap.application.utils.TaskEngine;
import coolmap.data.contology.model.CSamplePropertyMatrix;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Keqiang Li
 */
public class FakeAction extends AbstractAction{
    
    public FakeAction() {
        super("fake");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "fake");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LongTask task = new LongTask("import sample property file...") {

            @Override
            public void run() {

                CSamplePropertyMatrix samplePropertyMatrix = CoolMapMaster.getFirst();
                samplePropertyMatrix.movePropertyToIndex(1, 2);
               

            }
        };

        TaskEngine.getInstance().submitTask(task);
    }
    
}
        