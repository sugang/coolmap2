/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application.io.external;

import coolmap.data.contology.model.COntology;
import coolmap.utils.bioparser.simpleobo.SimpleOBOTree;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Keqiang Li
 */
public class ImportOBOFromFile {
    
    public static COntology importOBOFromFile(File f) throws IOException {
        SimpleOBOTree oboTree = SimpleOBOTree.parse1(f.getName(), new FileInputStream(f));
        
        COntology ontology = oboTree.ontology;
        return ontology;
    }  
}
