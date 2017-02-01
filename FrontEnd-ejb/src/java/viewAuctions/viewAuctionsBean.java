/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewAuctions;

import items.ItemWebService_Service;
import javax.ejb.Stateless;
import resources.Item;
import java.util.*;
import javax.xml.ws.WebServiceRef;



/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class viewAuctionsBean implements viewAuctionsBeanLocal {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/itemWebService.wsdl")
    private ItemWebService_Service service;

    @Override
    public String viewAuctions(){
        return selectLive();
    }

    private String selectLive() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        return port.selectLive();
    }
}
