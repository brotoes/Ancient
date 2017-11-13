/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actionTracker;

import ancient.map.Province;

/**
 *
 * @author brock
 */
public class BuildAction extends Action {
    Province prov;
    int facId;

    /**
     * Empty Constructor for kryo serializing
     */
    private BuildAction() {}

    public BuildAction(Province prov, int facId) {
        this.prov = prov;
        this.facId = facId;
    }

    @Override
    void perform() {
        prov.build(facId);
    }
}
