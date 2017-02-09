/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heartBeatReceiver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import offer.OfferWebService_Service;
import totalOrderReplicazione.totalOrderMulticastSender;

/**
 *
 * @author alessandrotorcetta
 */
@WebService(serviceName = "heartBeatReceiverWebService")
public class heartBeatReceiverWebService {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/ReplicaManager-war/offerWebService.wsdl")
    private OfferWebService_Service service;

    @Inject
    private HashSet<Integer> alive;
    @Inject
    private HashSet<Integer> suspected;
    @Inject
    private long delay;
    @Inject
    private HashSet<Integer> total_process;
    
    @Inject
    private totalOrderMulticastSender toms;
    
    @Inject
    private NetworkConfigurator nc;
    
    
    
    @PostConstruct
    public void init(){
        alive = new HashSet<Integer>();
        suspected = new HashSet<Integer>();
        delay = 30000;
        total_process = new HashSet<Integer>();
        
        nc = NetworkConfigurator.getInstance(false);
        NetworkNode nodo;
        List <NetworkNode> replicas = nc.getReplicas();
        for(int i = 0; i< replicas.size() ; i++){
                 nodo = (NetworkNode) replicas.toArray()[i];
                 total_process.add(nodo.getId());
        }
        
        
        //total_process.add(2);
        toms = totalOrderMulticastSender.getInstance();
        checkSuspect();
        
        
        
    }
    //-------> SONO ARRIVATO QUI
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "heartBeatReceive")
    public void heartBeatReceive(@WebParam(name = "heartBeat") String heartBeat) {
        // QUA SI PRENDE L'ID DEL RM
        String[] tmp = heartBeat.split(" ");
        int replica_id = Integer.parseInt(tmp[0]);
        System.out.println("Aggiungo " + replica_id + " alla lista dei processi alive");
        alive.add(replica_id);
        List<String> notifyMsgList;
        
        if (suspected.contains(replica_id)){
            System.out.println(""+ replica_id + " è in realtà vivo, aumento il delay per il prossimo round");
            suspected.remove(replica_id);
            toms.addToAlive(replica_id);
            delay = delay + 1000; 
        }
        
        System.out.println(heartBeat);
    } 
    
    private void checkSuspect(){
        //System.out.println("Inside checkSuspect --");
        int q  = 0;
        List<String> finalMsgList;
        String finalMsg;
        while(true){
            for(int i = 0; i < total_process.size(); i++){
               // System.out.println("Inside  for checkSuspect -->");
                q = (int) total_process.toArray()[i];
                System.out.println("Inside  for checkSuspect Vedo se -->" + q + " è alive");
                if (!alive.contains(q) && !suspected.contains(q)){
                    System.out.println("Aggiungo " + q + " alla lista dei sospettati");
                    suspected.add(q);
                    finalMsgList = toms.removeToAlive(q);
                    
                    //ogni elemento della lista lo invio
                    
                    for(Iterator j = finalMsgList.listIterator(); j.hasNext();){
                        finalMsg = (String) j.next();
                        offer(finalMsg);
                    }
                    
                    
                    
                }
            }
            alive = new HashSet<Integer>();     // resetto i processi alive per il prossimo round
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(heartBeatReceiverWebService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            //port.offer(offerMsg);
            
            try{
                port.offer(offerMsg);
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
            
        }
        
        
    }
}
