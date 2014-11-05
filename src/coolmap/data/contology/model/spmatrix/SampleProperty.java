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
public abstract class SampleProperty<T> {

    public String propType; // type of this property, such as the type of MALE is GENDER
    private SamplePropertyGroup<T> _group;
    public T value;

    public SampleProperty(String propType, T value) {
        this.propType = propType;
        this.value = value;
    }

    public void setGroup(SamplePropertyGroup<T> group) {
        _group = group;
    }

    public String getDisplayName() {
        if (_group != null) {
            return _group.customizedName;
        }
        return getDisplayValue();
    }

    public abstract String getDisplayValue();
}
