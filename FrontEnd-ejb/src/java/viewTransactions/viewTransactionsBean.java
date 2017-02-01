/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewTransactions;

import items.ItemWebService_Service;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class viewTransactionsBean implements viewTransactionsBeanLocal {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/itemWebService.wsdl")
    private ItemWebService_Service service;
    
    
    public String viewLiveAuctions(){
        return selectLive();
    }

    private String selectLive() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        return port.selectLive();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
