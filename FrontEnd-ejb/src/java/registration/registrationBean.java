/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;


import javax.ejb.Stateless;
import resources.User;

/**
 *
 * @author Nick F
 */
@Stateless
public class registrationBean implements registrationBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void register(User u){
        
        // invocazione...
    }
}
