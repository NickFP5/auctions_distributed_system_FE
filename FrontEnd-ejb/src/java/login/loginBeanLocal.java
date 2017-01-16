/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import javax.ejb.Local;
import resources.User;

/**
 *
 * @author Nick F
 */
@Local
public interface loginBeanLocal {
    public boolean check(User u);
}
