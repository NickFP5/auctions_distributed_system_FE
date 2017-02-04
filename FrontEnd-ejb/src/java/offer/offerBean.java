/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offer;

import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import totalOrderReplicazione.totalOrderMulticastSender;

/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class offerBean implements offerBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/offerWebService.wsdl")
    private OfferWebService_Service service;
    
    
    
    @Override
    //public String offerPrice(int itemId, float requestedPrice, int userId){
    public void offerPrice(String offerMsg){    
        totalOrderMulticastSender.getInstance().send(1, offerMsg);
       // return "ciao";
        
    }
    
    
    @Override
    public String findTransaction(int itemId){
        
       // return "ciao";
        return getTransaction(itemId);
    }
    
    
   


    private String getTransaction(int itemId) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        offer.OfferWebService port = service.getOfferWebServicePort();
        return port.getTransaction(itemId);
    }

    
    
}
