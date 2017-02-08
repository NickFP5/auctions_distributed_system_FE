/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package totalOrderReplicazione;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import model.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.BindingProvider;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import offer.offerBeanLocal;

public class totalOrderMulticastSender {

    

    private static totalOrderMulticastSender _instance = new totalOrderMulticastSender();
    private Map<Integer, List<TotalOrderMulticastMessage>> holdbackQueueTable;
    private Map<Integer, Integer> groupLastSequence;
    //private Map<Integer, Map<Integer, List<Integer>>> groupProposalSequence;
    private Map<Integer, Map<Integer, Map<Integer,Integer>>> groupProposalSequence;
    private Map<Integer, Integer> groupMessageCounter;
    private Map<Integer, Map<Integer, TotalOrderMulticastMessage>> bufferMessageTable;
    //private BasicMulticast basicMulticast;
    private  Object _mutex;
    private List<String> deliveryQueue;
    private int  myport, myid, mygroup, mygroupsize;
    private String myname, myip;
    
    private HashSet<Integer> alive;
    
    
    

    private totalOrderMulticastSender(){
        
      
            _mutex = new Object();
            NetworkConfigurator nc = NetworkConfigurator.getInstance(false);
            NetworkNode nn = nc.getMyself();
            myid = nn.getId();
            myip = nn.getIp();
            myport = nn.getPort();
            myname = nn.getName();
            mygroup = 1;
            mygroupsize = nc.getReplicas().size()+1;
            
            
            
            System.out.println("MIO ID-->" + myid + "MIO IP--->" + myip + "MIO NOME--->" + myname);
            
        
            holdbackQueueTable = new Hashtable<Integer, List<TotalOrderMulticastMessage>>();
            groupLastSequence = new Hashtable<Integer, Integer>();
            //basicMulticast = BasicMulticast.getInstance();
            bufferMessageTable = new Hashtable<Integer, Map<Integer, TotalOrderMulticastMessage>>();
            bufferMessageTable.put(1, new Hashtable<Integer, TotalOrderMulticastMessage>());
            //groupProposalSequence = new Hashtable<Integer, Map<Integer, List<Integer>>>();
            groupProposalSequence = new Hashtable<Integer, Map<Integer, Map<Integer, Integer>>>();
            groupMessageCounter = new Hashtable<Integer, Integer>();
            //_mutex = new Object();
            deliveryQueue = new LinkedList<String>();
            
            // DA MODIFICARE CON IL DEPLOY
            alive = new HashSet<Integer>();
            NetworkNode nodo;
            List <NetworkNode> replicas = nc.getReplicas();
            for(int i = 0; i< replicas.size() ; i++){
                 nodo = (NetworkNode) replicas.toArray()[i];
                 alive.add(nodo.getId());
            }
        
    }

    public static synchronized totalOrderMulticastSender getInstance() {
        return _instance;
    }

    public String send(int groupId, String groupMessage) {
       // synchronized (_mutex) {
        //synchronized(this){
            System.out.println("Dentro send di totalSender");
            TotalOrderMulticastMessage tomm;
            
            
            int selfId = myid;
   //----->         int selfId = Profile.getInstance().getId();
            int messageId;
            Map<Integer, TotalOrderMulticastMessage> cachedMessage;

            tomm = new TotalOrderMulticastMessage();
            tomm.setContent(groupMessage);
            tomm.setSource(selfId);
            tomm.setGroupId(groupId);
            tomm.setMessageType(TotalOrderMessageType.INITIAL);
            tomm.setSequence(-1);
            if (groupMessageCounter.containsKey(groupId) == false) {
                groupMessageCounter.put(groupId, 0);
            }

            if (bufferMessageTable.containsKey(groupId) == false) {
                bufferMessageTable.put(groupId, new Hashtable<Integer, TotalOrderMulticastMessage>());
            }
            messageId = groupMessageCounter.get(groupId);
            cachedMessage = bufferMessageTable.get(groupId);
            tomm.setMessageId(messageId);
            cachedMessage.put(messageId, tomm);

            groupMessageCounter.put(groupId, messageId + 1);
            //basicMulticast.send(groupId, tomm);
            
      //::::---->            notifyAll();
            
            
           //System.out.println("STO INVIANDO IL NOTIFY");
           // _mutex.notifyAll();
           System.out.println("Invio il messaggio " + tomm.toString() + " , sono il SENDER BEAN");
           return tomm.toString();
            
          
          
          
           // ----->delivery

            // delivery(message)
            
            
            
     //   } //fine synchgronized
         
    }
/*
    public void basicMulticast(String msg) {
        System.out.println("Invio il messaggio " + msg + " , sono il SENDER BEAN");
        //per ognuno dei replicaManager non sospetto
        
      //::::---->        notifyAll();
        
        //offerBean.offer(msg);
    }
*/
    public String delivery(IMessage message) {
        System.out.println("TotalOrderSender  PRIMA DEL SYNCHRONIZED inside delivery (id " + myid + ") received msg: " + message.toString());
        
        
     //   synchronized (_mutex) {
        
        /*try {
            System.out.println("Mi METTO IN ATTESA DEL NOTIFY");
            _mutex.wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(totalOrderMulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
        //synchronized(this){
            TotalOrderMulticastMessage tomm = (TotalOrderMulticastMessage) message;
            int groupId;
            int sequence;
            int selfId;
            List<TotalOrderMulticastMessage> priorityQueue;
            TotalOrderMessageType messageType;

            System.out.println("TotalOrderSender inside delivery inside syncrhonized");
            
            groupId = tomm.getGroupId();
     // -------->      selfId = Profile.getInstance().getId();
            
            selfId = myid;
            
            if (holdbackQueueTable.containsKey(groupId) == false) {
                holdbackQueueTable.put(groupId, new LinkedList<TotalOrderMulticastMessage>());
            }
            if (groupLastSequence.containsKey(groupId) == false) {
                groupLastSequence.put(groupId, 0);
            }

            priorityQueue = holdbackQueueTable.get(groupId);
            messageType = tomm.getMessageType();
            sequence = groupLastSequence.get(groupId);
            
            

           if (messageType == TotalOrderMessageType.PROPOSAL) {
                System.out.println("TotalOrderSender (id" + myid + ") received PROPOSAL msg. ");
                Map<Integer,Integer> cachedSequence;
                Map<Integer, Map<Integer,Integer>> cachedSequenceTable;
                int messageId = tomm.getMessageId();
                int proposeSequence = tomm.getSequence();
                if (groupProposalSequence.containsKey(groupId) == false) {
                    groupProposalSequence.put(groupId, new Hashtable<Integer, Map<Integer,Integer>>());
                }
                cachedSequenceTable = groupProposalSequence.get(groupId);
                if (cachedSequenceTable.containsKey(messageId) == false) {
                    cachedSequenceTable.put(messageId, new HashMap<Integer,Integer>());
                }

                cachedSequence = cachedSequenceTable.get(messageId);
                cachedSequence.put(tomm.getSource(),proposeSequence);
                //System.out.println("receive proposed message: " + tomm);
     //   ------>        if (cachedSequence.size() == MemberIndexer.getInstance().getGroupSize(groupId)) {
            boolean result = isReadyToSendFinal(cachedSequence);
     
                
            //if (cachedSequence.size() == mygroupsize - 1 ) {
            if(result){
                    System.out.println("TotalOrderSender (id" + myid + ") collected all PROPOSAL msg. Sending FINAL msg to group.");
                    int finalSequence = 0;
                  //  finalSequence = sequence > Collections.max(cachedSequence) ? sequence : Collections.max(cachedSequence);
                    int p;
                    for(Map.Entry<Integer,Integer> entry : cachedSequence.entrySet()){
                        p = entry.getValue();
                        if(finalSequence < p) finalSequence = p;
                    }
                    TotalOrderMulticastMessage finalMessage = bufferMessageTable.get(groupId).get(messageId);
                    finalMessage.setSequence(finalSequence);
                    finalMessage.setMessageType(TotalOrderMessageType.FINAL);
                    //basicMulticast.send(groupId, finalMessage);
                    bufferMessageTable.get(groupId).remove(finalMessage);
                    groupLastSequence.put(groupId, finalSequence);
                    // IL SENDER PRENDE IL PROPOSAL MSG, elabora il final e lo tramsette a tutti i membri del gruppo in formato JSON
                    
                    
                    cachedSequenceTable.remove(messageId);
                    
           //::::---->               notifyAll();
                    
            //----->        basicMulticast(finalMessage.toString());
                    return finalMessage.toString();
                    
                    

                }
   
            } 
      //  } //fine synchronized
      
        return null;
      
    }
    
    
    public void addToAlive(int p){
        //synchronized (_mutex) {
            alive.add(p);
            //::::---->      notifyAll();
        //}
    }
    
    public List<String> removeToAlive(int p){
       //  synchronized (_mutex) {
            System.out.println("Rimuovo un RM sospetto dalla lista degli alive");
            alive.remove(p);
            List <String> finalMsgList = new LinkedList<String>();
            boolean result;
            Map<Integer,Integer> cachedSequence;
            Map<Integer, Map<Integer, Integer>> cachedSequenceTable = groupProposalSequence.get(1);
            if(cachedSequenceTable != null){
                for(Map.Entry<Integer, Map<Integer,Integer>> entry : cachedSequenceTable.entrySet()){
                    cachedSequence = entry.getValue();
                    result = isReadyToSendFinal(cachedSequence);
                    if(result){
                        int finalSequence = 0;
                          //  finalSequence = sequence > Collections.max(cachedSequence) ? sequence : Collections.max(cachedSequence);
                            int pa;
                            for(Map.Entry<Integer,Integer> entry2 : cachedSequence.entrySet()){
                                pa = entry2.getValue();
                                if(finalSequence < pa) finalSequence = pa;
                            }
                            TotalOrderMulticastMessage finalMessage = bufferMessageTable.get(1).get(entry.getKey());
                            finalMessage.setSequence(finalSequence);
                            finalMessage.setMessageType(TotalOrderMessageType.FINAL);
                            //basicMulticast.send(groupId, finalMessage);
                            
                            finalMsgList.add(finalMessage.toString());
                            
                            bufferMessageTable.get(1).remove(finalMessage);
                            groupLastSequence.put(1, finalSequence);
                            // IL SENDER PRENDE IL PROPOSAL MSG, elabora il final e lo tramsette a tutti i membri del gruppo in formato JSON


                            cachedSequenceTable.remove(entry.getKey());

                            System.out.println("Invio a tutti tranne al RM attualmente sospetto");
                            
                            
  

                        //   ------> basicMulticast(finalMessage.toString());
                    }

                }
            }  
            return finalMsgList;
            
      //  }  //dentro send    
    }
    
    
    private boolean isReadyToSendFinal(Map<Integer,Integer> cachedSequence){
        System.out.println("Sono nell'isReadyToSend");
        boolean result = false;
        
        int pa;
        //int p;
        
        for(int i = 0; i< alive.size(); i++){
          pa = (int) alive.toArray()[i];
          if (!cachedSequence.containsKey(pa)){
              result = false;
              return result;
          }
        }
        result = true;
        
        System.out.println("Sto per restituire ---> " + result + " nell' ISREADYTOFINAL");
        
        return result;
    }

    
}
