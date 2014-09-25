/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coolmap.canvas.action;

import coolmap.application.CoolMapMaster;
import coolmap.application.state.StateStorageMaster;
import coolmap.data.CoolMapObject;
import coolmap.data.state.CoolMapState;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author sugang
 */
public class CollapseColumnNodesUpAction extends AbstractAction{
        private final String id;
    
    public CollapseColumnNodesUpAction(String objectID){
        super("Collapse all");
        putValue(SHORT_DESCRIPTION, "Collapse all column nodes to previous level");
        id = objectID;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            CoolMapObject obj = CoolMapMaster.getCoolMapObjectByID(id);
            CoolMapState state = CoolMapState.createStateColumns("Collapse columns to previous level", obj, null);
            obj.collapseColumnNodesOneLayer();
            StateStorageMaster.addState(state);
        }
        catch(Exception ex){
            
        }
    }
}
