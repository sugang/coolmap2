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
import coolmap.data.contology.model.spmatrix.CategorizedPropertyGroupSetting;
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
public class ImportPropertyNameGroupSettingFromOBOAction extends AbstractAction {
    
    private final String _curPropString;
    
    public ImportPropertyNameGroupSettingFromOBOAction(String propType) {
        super("from OBO group setting file");
        this._curPropString = propType;
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "import OBO group setting from .obo files (map properties to names)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = Tools.getCustomFileChooser(new FileNameExtensionFilter(".tsv", "txt", "tsv", "obo"));
        int returnVal = chooser.showOpenDialog(CoolMapMaster.getCMainFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final File f = chooser.getSelectedFile();
        if (f != null && f.isFile() && f.exists()) {
            LongTask task = new LongTask("importing OBO file...") {

                @Override
                public void run() {
                    try {
                        CategorizedPropertyGroupSetting newSetting  = CategorizedPropertyGroupSetting.importGroupSettingFromOBOFile(_curPropString, new FileInputStream(f));
                       
                        if (newSetting == null) {
                            return;
                        }
                        
                        boolean result = SamplePropertyMaster.applyOBOGroupSetting(newSetting, _curPropString);

                        if (result) {
                            CMConsole.logInSuccess("OBO File imported and group settings applied " + f.getPath());
                        } else {
                            CMConsole.logError("Error: Failed to import OBO File group setting " + f.getPath());
                        }
                    } catch (Exception ex) {
                        CMConsole.logError("Error: failed to import OBO file from " + f.getName());
                    }
                }
            };

            TaskEngine.getInstance().submitTask(task);

        }
    }
    
}
