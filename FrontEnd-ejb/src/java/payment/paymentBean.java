/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payment;

import java.util.Iterator;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;

/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class paymentBean implements paymentBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/payWebService.wsdl")
    private PayWebService_Service service;
    
    @Override
    public void paga(int user_id, int item_id){
        System.out.println("Dentro paga del Bean");
        doPayment(user_id,item_id);
    }

    private void doPayment(int user_id, int item_id) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        payment.PayWebService port = service.getPayWebServicePort();
        //port.doPay(item_id, item_id);
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

       String s;
        
        for(Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();){
            NetworkNode n = (NetworkNode) it.next();
            
            s = "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/payWebService";
            
            bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+n.getIp()+":"+n.getPort()+"/ReplicaManager-war/payWebService"
            );
            
            System.out.println("Dentro doPay, sto cercando di comunicare con --> " + s);
            //result = port.insert(email, name, password);
            
            try{
                port.doPayWebServ(user_id, item_id);
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
            
        }
        
        
    }


    

}
