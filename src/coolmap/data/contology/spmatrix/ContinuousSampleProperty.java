/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coolmap.data.contology.spmatrix;

/**
 *
 * @author Keqiang Li
 */
public class ContinuousSampleProperty extends SampleProperty<Double> {

    public ContinuousSampleProperty(String propType, double value) {
        super(propType, value);
    }

    @Override
    public String getDisplayValue() {
        return "" + value;
    }
}
