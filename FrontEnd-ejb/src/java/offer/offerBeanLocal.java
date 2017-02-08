/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offer;

import javax.ejb.Local;

/**
 *
 * @author alessandrotorcetta
 */
@Local
public interface offerBeanLocal {
    public void offerPrice(String offerMsg);


    public String findTransaction(int itemId);
    
    //public void offer(java.lang.String offerMsg);
    
}

