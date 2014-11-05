/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.model.spmatrix;

/**
 *
 * @author Keqiang Li
 */
public class CategorizedSampleProperty extends SampleProperty<String> {

    public CategorizedSampleProperty(String propType, String value) {
        super(propType, value);
    }

    @Override
    public String getDisplayValue() {
        return value;
    }
}
