/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudItem;

import netConf.NetworkConfigurator;
import items.ItemWebService_Service;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkNode;
import resources.Item;

/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class crudItemBean implements crudItemBeanLocal {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/itemWebService.wsdl")
    private ItemWebService_Service service;
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public void modifyTitle(int id, String title) {
        updateTitle(id, title);
    }
    
    @Override
    public void modifyPrice(int id, float price) {
        updatePrice(id, price);
    }
    
    @Override
    public void modifyExpiringDate(int id, long d) {
        updateExpiringDate(id, d);
    }
    
    @Override
    public void create(String title, float price, int seller_id, long expiring_date) {
        insert( title, price, seller_id, expiring_date);
    }
    
    @Override
    public void remove(int id) {
        delete(id);
    }
    

    private void updateTitle(int id, java.lang.String title) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        //port.updateTitle(id, title);
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/itemWebService"
            );
            port.updateTitle(id, title);
        }
        
    }

    private void updatePrice(int id, float price) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        //port.updatePrice(id, price);
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/itemWebService"
            );
            port.updatePrice(id, price);
        }
        
    }

    private void updateExpiringDate(int id, long exp) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        port.updateExpiringDate(id, exp);
    }

    private void insert(java.lang.String title, float price, int sellerId, long expiringDate) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/itemWebService"
            );
            //port.insert(title, price, sellerId, expiringDate);
            
            try{
                port.insert(title, price, sellerId, expiringDate);
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
            
            
        }
        
    }

    private void delete(int id) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        items.ItemWebService port = service.getItemWebServicePort();
        
        //port.delete(id);
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/itemWebService"
            );
            //port.delete(id);
            
            try{
                port.delete(id);
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
            
        }
        
        
    }
    
}