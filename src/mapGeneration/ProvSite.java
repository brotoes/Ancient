/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import kn.uni.voronoitreemap.j2d.Site;

/**
 * Extension of Site that links to a province.
 * 
 * @author brock
 */
public class ProvSite {
    private Province province;
    private final Site site;
    
    public ProvSite(Site site){
        this.site = site;
    }
    
    public Site getSite() {
        return site;
    }
    
    public void setProvince(Province prov) {
        this.province = prov;
    }
    
    public Province getProvince() {
        return province;
    }
}
