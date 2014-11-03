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
// user customized property group. users may group values from 1 to 100 as group "1-100"
public abstract class SamplePropertyGroup<T> {

    public String customizedName;

    public SamplePropertyGroup(String name) {
        customizedName = name;
    }

    public abstract boolean contains(T value);
}
