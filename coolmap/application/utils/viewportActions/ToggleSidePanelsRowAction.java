/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coolmap.application.utils.viewportActions;

import coolmap.application.CoolMapMaster;
import coolmap.canvas.CoolMapView;
import coolmap.data.CoolMapObject;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author sugang
 */
public class ToggleSidePanelsRowAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            CoolMapObject object = CoolMapMaster.getActiveCoolMapObject();
                if (object != null) {
                    CoolMapView view = object.getCoolMapView();
                    if (view.isRowPanelsVisible()) {
                        view.setRowPanelsVisible(false);
                    } else {
                        view.setRowPanelsVisible(true);
                    }
                }
        }
        catch(Exception ex){
            
        }
    }
    
}
