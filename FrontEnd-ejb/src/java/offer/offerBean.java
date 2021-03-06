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
 * 
 * offerBean: 
 *      bean che coopera con il gestore di ISIS totalOrderMulticastSender per effettuare l'invio di messaggi REVISE_TS
 *      in corrispondenza di offerte di rilanci da parte degli utenti per un particolare oggetto in asta.
 */
@Stateless
public class offerBean implements offerBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/offerWebService.wsdl")
    private OfferWebService_Service service;

    @Override
    public void offerPrice(String offerMsg) {

        String tomm = totalOrderMulticastSender.getInstance().send(1, offerMsg);

        offer(tomm);

    }

    private void offer(java.lang.String offerMsg) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        offer.OfferWebService port = service.getOfferWebServicePort();
        //port.offer(offerMsg);

        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;

        for (Iterator it = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); it.hasNext();) {
            NetworkNode n = (NetworkNode) it.next();

            bindingProvider.getRequestContext().put(
                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    "http://" + n.getIp() + ":" + n.getPort() + "/ReplicaManager-war/offerWebService"
            );
            System.out.println("Sono il SENDER, STO INVIANDO " + offerMsg + " A ---> " + n.getIp() + " il suo nome è ---> " + n.getName());
            //port.offer(offerMsg);

            try {
                port.offer(offerMsg);
            } catch (Exception ex) {
                System.err.println("Errore di rete");
            }

        }

    }

    @Override
    public String findTransaction(int itemId) {

        return getTransaction(itemId);
    }

    private String getTransaction(int itemId) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        offer.OfferWebService port = service.getOfferWebServicePort();

        //return port.getTransaction(itemId);
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

        String s = "http://" + NetworkConfigurator.getInstance(false).getMyself().getIp() + ":" + porta + "/ReplicaManager-war/offerWebService";
        System.out.println("FRONTEND WebService --> " + s);

        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://" + NetworkConfigurator.getInstance(false).getMyself().getIp() + ":" + porta + "/ReplicaManager-war/offerWebService"
        );

        String t = null;

        try {
            t = port.getTransaction(itemId);
        } catch (Exception ex) {
            System.err.println("Errore di rete");
        }
        return t;
    }

}
