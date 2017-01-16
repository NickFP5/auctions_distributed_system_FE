/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import javax.ejb.Local;
import resources.User;

/**
 *
 * @author Nick F
 */
@Local
public interface registrationBeanLocal {
    public void register(User u);
}
