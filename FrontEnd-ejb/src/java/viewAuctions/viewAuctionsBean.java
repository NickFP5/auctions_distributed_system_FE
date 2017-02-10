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
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;



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
        //return port.selectLive();
        
        String result = null;
        
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;
        //String s = "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+NetworkConfigurator.getInstance(false).getMyself().getPort()+"/ReplicaManager-war/userWebService";
;
        //System.out.println("FRONTEND WebService --> " + s);
        NetworkNode node;
        int porta = 0;
        for(Iterator i  = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); i.hasNext();){
            System.out.println("Dentro for select bean FE");
            node = (NetworkNode) i.next();
            if(NetworkConfigurator.getInstance(false).getMyself().getIp().equals(node.getIp())){
                System.out.println("Trovata replica che ha il mio stesso ip, mi prendo la sua porta");
                porta = node.getPort();
                break;
            }
        }
        
        String s = "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+porta+"/ReplicaManager-war/itemWebService";
        System.out.println("FRONTEND WebService --> " + s);
        
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+porta+"/ReplicaManager-war/itemWebService"
            );
        
        
        
        
        
        try{
                result = port.selectLive();
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
        
        return result;
        
    }
}
