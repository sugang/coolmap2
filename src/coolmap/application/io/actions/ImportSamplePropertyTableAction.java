/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.actions;

import coolmap.application.CoolMapMaster;
import coolmap.application.io.external.ImportSamplePropertyFromFile;
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
public class ImportSamplePropertyTableAction extends AbstractAction {
    
    public ImportSamplePropertyTableAction() {
        super("from sample-property table file");
        putValue(javax.swing.AbstractAction.SHORT_DESCRIPTION, "import sample-property table file, and then generate ontology on it");
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

            LongTask task = new LongTask("import sample property file...") {

                @Override
                public void run() {
                    try {
                        COntology ontology = ImportSamplePropertyFromFile.importSamplePropertyFromFile(f);
                        if(ontology == null)
                            return;
                        
                        ontology.setName(Tools.removeFileExtension(f.getName()));
                        CoolMapMaster.addNewCOntology(ontology);
                       
                        CMConsole.logInSuccess("File imported sample property file and ontology generated " + f.getPath());
                    } catch (Exception ex) {
                        CMConsole.logError("Error: failed to import sample-property file from " + f.getName());
                    }
                }
            };
            
            TaskEngine.getInstance().submitTask(task);

        }
        
        
    }
    
}
