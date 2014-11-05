/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.application;

import coolmap.application.widget.WidgetMaster;
import coolmap.application.widget.impl.WidgetSamplePropertyTable;
import coolmap.data.contology.model.spmatrix.CSamplePropertyMatrix;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author Keqiang Li
 */
public class SamplePropertyMaster {

    /* edited by Keqiang Li, Oct. 23
     This map keeps track for all property files imported
     */
    private final static LinkedHashMap<String, CSamplePropertyMatrix> _samplePropertyMatrice = new LinkedHashMap<>();

    public static void addNewSamplePropertyMatrix(CSamplePropertyMatrix samplePropertyMatrix) {
        _samplePropertyMatrice.put(samplePropertyMatrix.getMatrixName(), samplePropertyMatrix);
        if (samplePropertyMatrix.getOntology() != null) {
            CoolMapMaster.addNewCOntology(samplePropertyMatrix.getOntology());
        }
        samplePropertyMatrix.setIsAdded(true);
    }

    public static void removeSamplePropertyMatrix(CSamplePropertyMatrix samplePropertyMatrix) {
        removeSamplePropertyMatrix(samplePropertyMatrix.getMatrixName());
    }

    public static void removeSamplePropertyMatrix(String matrixName) {
        if (_samplePropertyMatrice.containsKey(matrixName)) {
            _samplePropertyMatrice.get(matrixName).setIsAdded(false);
            _samplePropertyMatrice.remove(matrixName);
        }
    }

    public static CSamplePropertyMatrix getSamplePropertyMatrix(String matrixName) {
        return _samplePropertyMatrice.get(matrixName);
    }

    // only for test
    public static CSamplePropertyMatrix getFirst() {
        Set<String> tmp = _samplePropertyMatrice.keySet();
        for (String key : tmp) {
            return _samplePropertyMatrice.get(key);
        }
        return null;
    }

    public static void updateSamplePropertyWidget(CSamplePropertyMatrix matrix) {
        String widgetName = WidgetSamplePropertyTable.class.getName();
        WidgetSamplePropertyTable samplePropertyWidget = (WidgetSamplePropertyTable) WidgetMaster.getWidget(widgetName);

        samplePropertyWidget.updateTable(matrix);
    }

}
