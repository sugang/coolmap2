/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions.spmatrix;

import coolmap.application.CoolMapMaster;
import coolmap.application.SamplePropertyMaster;
import coolmap.application.utils.LongTask;
import coolmap.application.utils.TaskEngine;
import coolmap.application.widget.impl.console.CMConsole;
import coolmap.data.contology.model.spmatrix.ContinuousPropertyGroupSetting;
import coolmap.utils.Tools;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Keqiang Li
 */
public class ImportContinuousPropertyGroupFromFileAction extends AbstractAction {

    private final String _curPropString;
    
    public ImportContinuousPropertyGroupFromFileAction(String propType) {
        this._curPropString = propType;
    }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = Tools.getCustomFileChooser(new FileNameExtensionFilter(".tsv", "txt", "tsv"));
        int returnVal = chooser.showOpenDialog(CoolMapMaster.getCMainFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final File f = chooser.getSelectedFile();
        if (f != null && f.isFile() && f.exists()) {
            LongTask task = new LongTask("importing group setting from file...") {

                @Override
                public void run() {
                    try {
                        ContinuousPropertyGroupSetting newSetting  = ContinuousPropertyGroupSetting.importGroupSettingFromTextFile(_curPropString, new FileInputStream(f));
                       
                        if (newSetting == null) {
                            return;
                        }
                        
                        SamplePropertyMaster.applyContGroupSetting(newSetting, _curPropString);

                       
                        CMConsole.logInSuccess("group setting File imported and group settings applied " + f.getPath());
                     
                    } catch (Exception ex) {
                        CMConsole.logError("Error: failed to import group setting from " + f.getName());
                    }
                }
            };

            TaskEngine.getInstance().submitTask(task);

        }
    }
    
}
