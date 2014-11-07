/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions;

import coolmap.application.CoolMapMaster;
import coolmap.application.io.external.ImportOBOFromFile;
import coolmap.application.utils.LongTask;
import coolmap.application.utils.TaskEngine;
import coolmap.application.widget.impl.console.CMConsole;
import coolmap.data.contology.model.COntology;
import coolmap.utils.Tools;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Keqiang Li
 */
public class ImportOBOAction extends AbstractAction{
    
    public ImportOBOAction() {
        super("from OBO ontology file");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "import OBO ontology from .obo files");
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
                        COntology ontology = ImportOBOFromFile.importOBOFromFile(f);
                        if (ontology == null) {
                            return;
                        }
                        
                        CoolMapMaster.addNewCOntology(ontology);
                        CMConsole.logInSuccess("OBO ontology imported " + f.getPath());
                    } catch (Exception ex) {
                        CMConsole.logError("Error: failed to import OBO ontology from " + f.getName());
                    }
                }
            };

            TaskEngine.getInstance().submitTask(task);

        }
    }
}
