/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewTransactions;

import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
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
    public String viewTransactions(){
        System.out.println("Dentro selectTransactions di viewTransactionbBean");
       // return "ciao";
        return selectTransactions();
    }
    


    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    private String selectTransactions() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        transaction.TransactionWebService port = service.getTransactionWebServicePort();
        return port.selectTransactions();
    }

    
    
}
