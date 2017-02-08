/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offer;

import java.util.Iterator;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
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
        
        String tomm = totalOrderMulticastSender.getInstance().send(1, offerMsg);
       
        offer(tomm);
        
        // return "ciao";
       
        
    }
    
    
    
    
    
    /*
    @Override
    public void offer(java.lang.String offerMsg) {
        
        offer.OfferWebService_Service service;
        offer.OfferWebService port;
        //port.offer(offerMsg);
        
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        
        NetworkNode n;
        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            n = (NetworkNode) it.next();
            System.out.println("PORTA a cui invio -->" + n.getPort() + "  IP ---> " + n.getIp() + " nome --> " + n.getName());
        }
        

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            service = new offer.OfferWebService_Service();
            port = service.getOfferWebServicePort();
            bindingProvider = (BindingProvider) port;
            n = (NetworkNode) it.next();
            System.out.println("SIZE DEI REPLICA A CUI INVIARE --> " + NetworkConfigurator.getInstance(false).getReplicas().size());
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/offerWebService"
            );
            System.out.println("Sono il SENDER, STO INVIANDO A ---> " + n.getIp() + " il suo nome è ---> " + n.getName());
            port.offer(offerMsg);
            
        }
        */
        
        /*
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://87.17.110.210:8080/ReplicaManager-war/offerWebService"
            );
        System.out.println("Sono il SENDER, STO INVIANDO A ---> 87.17.110.210");
        port.offer(offerMsg);
    }
        */

    private void offer(java.lang.String offerMsg) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        offer.OfferWebService port = service.getOfferWebServicePort();
        //port.offer(offerMsg);
        
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/offerWebService"
            );
            System.out.println("Sono il SENDER, STO INVIANDO A ---> " + n.getIp() + " il suo nome è ---> " + n.getName());
            port.offer(offerMsg);
        }
        
        
        
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
