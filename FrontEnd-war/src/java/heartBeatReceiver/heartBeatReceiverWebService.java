/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heartBeatReceiver;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import totalOrderReplicazione.totalOrderMulticastSender;

/**
 *
 * @author alessandrotorcetta
 */
@WebService(serviceName = "heartBeatReceiverWebService")
public class heartBeatReceiverWebService {

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
        delay = 10000;
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
        while(true){
            for(int i = 0; i < total_process.size(); i++){
               // System.out.println("Inside  for checkSuspect -->");
                q = (int) total_process.toArray()[i];
                System.out.println("Inside  for checkSuspect Vedo se -->" + q + " è alive");
                if (!alive.contains(q) && !suspected.contains(q)){
                    System.out.println("Aggiungo " + q + " alla lista dei sospettati");
                    suspected.add(q);
                    toms.removeToAlive(q);
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
}
