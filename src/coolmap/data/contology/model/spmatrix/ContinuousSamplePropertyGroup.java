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
public class ContinuousSamplePropertyGroup extends SamplePropertyGroup<Double> {

    private double _min;
    private double _max;

    public ContinuousSamplePropertyGroup(String name, double min, double max) {
        super(name);
        this._min = min;
        this._max = max;
    }

    public boolean setMin(double min) {
        if (min <= _max) {
            _min = min;
            return true;
        }
        return false;
    }

    public boolean setMax(double max) {
        if (max >= _min) {
            _max = max;
            return true;
        }
        return false;
    }

    @Override
    public String getUniqueID() {
        return getDisplayName();
    }

    @Override
    public boolean contains(Double value) {
        if (value > _min && value <= _max) return true;
        return false;
    }
}
