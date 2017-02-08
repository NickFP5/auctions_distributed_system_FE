/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservTotalOrder;

import java.io.StringReader;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonObject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import model.TotalOrderMessageType;
import model.TotalOrderMulticastMessage;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import offer.OfferWebService_Service;
import totalOrderReplicazione.totalOrderMulticastSender;

/**
 *
 * @author alessandrotorcetta
 */
@WebService(serviceName = "prTsWebService")
public class prTsWebService {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/ReplicaManager-war/offerWebService.wsdl")
    private OfferWebService_Service service;

    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "proposed")
    public void proposed(@WebParam(name = "proposedTs") String proposedTs) {
        System.out.println("ProposedTS WebService received--> " + proposedTs);
        JsonObject jo = Json.createReader(new StringReader(proposedTs)).readObject();
        TotalOrderMulticastMessage msg = new TotalOrderMulticastMessage();
        msg.setMessageId(jo.getInt("messageId"));
        //msg.setTotalOrderSequence(jo.getInt("totalOrderSequence"));
	msg.setMessageType(TotalOrderMessageType.PROPOSAL);
	msg.setGroupId(jo.getInt("groupId"));
	msg.setSource(jo.getInt("source"));
        msg.setSequence(jo.getInt("sequence"));
        //msg.setContent(jo.getString("content"));

        ///------->
        
        String finalMsg = totalOrderMulticastSender.getInstance().delivery(msg);
        
        if(finalMsg != null) offer(finalMsg);
        
    }

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
    
    
    
    
}
