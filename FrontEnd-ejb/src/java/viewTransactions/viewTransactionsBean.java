/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewTransactions;

import java.util.Iterator;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import transaction.TransactionWebService_Service;

/**
 *
 * @author alessandrotorcetta
 */
@Stateless
public class viewTransactionsBean implements viewTransactionsBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/transactionWebService.wsdl")
    private TransactionWebService_Service service;

    @Override
    public String viewTransactions() {
        System.out.println("Dentro selectTransactions di viewTransactionbBean");
        return selectTransactions();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private String selectTransactions() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        transaction.TransactionWebService port = service.getTransactionWebServicePort();
        //return port.selectTransactions();

        String result = null;

        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;
        //String s = "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+NetworkConfigurator.getInstance(false).getMyself().getPort()+"/ReplicaManager-war/userWebService";
        //System.out.println("FRONTEND WebService --> " + s);
        NetworkNode node;
        int porta = 0;
        for (Iterator i = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); i.hasNext();) {
            System.out.println("Dentro for select bean FE");
            node = (NetworkNode) i.next();
            if (NetworkConfigurator.getInstance(false).getMyself().getIp().equals(node.getIp())) {
                System.out.println("Trovata replica che ha il mio stesso ip, mi prendo la sua porta");
                porta = node.getPort();
                break;
            }
        }

        String s = "http://" + NetworkConfigurator.getInstance(false).getMyself().getIp() + ":" + porta + "/ReplicaManager-war/transactionWebService";
        System.out.println("FRONTEND WebService --> " + s);

        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://" + NetworkConfigurator.getInstance(false).getMyself().getIp() + ":" + porta + "/ReplicaManager-war/transactionWebService"
        );

        try {
            result = port.selectTransactions();
        } catch (Exception ex) {
            System.err.println("Errore di rete");
        }

        return result;

    }

}
