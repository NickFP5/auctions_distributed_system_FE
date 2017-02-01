/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudItem;

import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author alessandrotorcetta
 */
@Local
public interface crudItemBeanLocal {

    public void modifyTitle(int id, String title);

    public void modifyPrice(int id, float price);

    public void modifyExpiringDate(int id, long d);

    public void create(String title, float price, int seller_id, long expiring_date);

    public void remove(int id);
    
}
