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
import javax.xml.ws.BindingProvider;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;

public class totalOrderMulticastSender {

    private static totalOrderMulticastSender _instance = new totalOrderMulticastSender();
    private Map<Integer, List<TotalOrderMulticastMessage>> holdbackQueueTable;
    private Map<Integer, Integer> groupLastSequence;
    //private Map<Integer, Map<Integer, List<Integer>>> groupProposalSequence;
    private Map<Integer, Map<Integer, Map<Integer,Integer>>> groupProposalSequence;
    private Map<Integer, Integer> groupMessageCounter;
    private Map<Integer, Map<Integer, TotalOrderMulticastMessage>> bufferMessageTable;
    //private BasicMulticast basicMulticast;
    private Object _mutex;
    private List<String> deliveryQueue;
    private int  myport, myid, mygroup, mygroupsize;
    private String myname, myip;
    
    private HashSet<Integer> alive;
    
    

    private totalOrderMulticastSender(){
        
       // try {
            
             // creazione file
             /*
            File f = new File("prova.txt");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(totalOrderMulticastSender.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("File absolute path-->" + f.getAbsolutePath() + "file path --> " + f.getPath());
            
            File config = new File("conf/config.txt");
            
            
            String myconfig;
            String rmconfig;
            
            Scanner s = new Scanner(config);
            
            myconfig  = s.nextLine();
            //rmconfig = s.nextLine();         
            */
             
            /* 
            String myconfig = "1 127.0.0.1 8080 FR1 1";
             
            String[] aux = myconfig.split(" ");
            myid = Integer.parseInt(aux[0]);
            myip = aux[1];
            myport = Integer.parseInt(aux[2]);
            myname = aux[3];
            mygroup = Integer.parseInt(aux[4]);
            mygroupsize = 2;
            */
            
            NetworkConfigurator nc = NetworkConfigurator.getInstance(false);
            NetworkNode nn = nc.getMyself();
            myid = nn.getId();
            myip = nn.getIp();
            myport = nn.getPort();
            myname = nn.getName();
            mygroup = 1;
            mygroupsize = nc.getReplicas().size()+1;
            
            
            
            System.out.println("MIO ID-->" + myid + "MIO IP--->" + myip + "MIO NOME--->" + myname);
            
          //  } catch (FileNotFoundException ex) {
          //  Logger.getLogger(totalOrderMulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        //}
            
            holdbackQueueTable = new Hashtable<Integer, List<TotalOrderMulticastMessage>>();
            groupLastSequence = new Hashtable<Integer, Integer>();
            //basicMulticast = BasicMulticast.getInstance();
            bufferMessageTable = new Hashtable<Integer, Map<Integer, TotalOrderMulticastMessage>>();
            //groupProposalSequence = new Hashtable<Integer, Map<Integer, List<Integer>>>();
            groupProposalSequence = new Hashtable<Integer, Map<Integer, Map<Integer, Integer>>>();
            groupMessageCounter = new Hashtable<Integer, Integer>();
            _mutex = new Object();
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

    public static totalOrderMulticastSender getInstance() {
        return _instance;
    }

    public void send(int groupId, String groupMessage) {
        //synchronized (_mutex) {
        synchronized(this){
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
            basicMulticast(tomm.toString());
        } //fine synchgronized
    }

    public void basicMulticast(String msg) {
        System.out.println("Invio il messaggio " + msg + " , sono il SENDER BEAN");
        //per ognuno dei replicaManager non sospetto
        
        offer(msg);
    }

    public void delivery(IMessage message) {
        System.out.println("TotalOrderSender inside delivery (id " + myid + ") received msg: " + message.toString());
        //synchronized (_mutex) {
        synchronized(this){
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
            
            

            //System.out.println("receive message: "+ tomm);
            if (messageType == TotalOrderMessageType.INITIAL) {
                System.out.println("TotalOrderSender (id" + myid + ") received INITIAL msg. Something went wrong.");
                TotalOrderMulticastMessage reply;
                sequence += 1;
                groupLastSequence.put(groupId, sequence);

                reply = new TotalOrderMulticastMessage();
                reply.setSource(selfId);
                reply.setGroupId(groupId);
                reply.setMessageType(TotalOrderMessageType.PROPOSAL);
                reply.setSequence(sequence);
                reply.setMessageId(tomm.getMessageId());
                //System.out.println("reply message" + reply);
                //basicMulticast.reply(groupId, tomm.getSource(), reply);

                tomm.setSequence(sequence);
                priorityQueue.add(tomm);
                Collections.sort(priorityQueue);
            } else if (messageType == TotalOrderMessageType.PROPOSAL) {
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
                    
                    basicMulticast(finalMessage.toString());
                    
                    
                    

                }

            } else if (messageType == TotalOrderMessageType.FINAL) {
                int source = tomm.getSource();
                int mid = tomm.getMessageId();
                for (TotalOrderMulticastMessage entry : priorityQueue) {
                    int entryMID = entry.getMessageId();
                    int entrySource = entry.getSource();
                    if (entryMID == mid && entrySource == source) {
                        entry.setMessageType(TotalOrderMessageType.FINAL);
                        entry.setSequence(tomm.getSequence());
                    }
                }
                if (sequence < tomm.getSequence()) {
                    sequence = tomm.getSequence();
                    groupLastSequence.put(groupId, sequence);
                }

                Collections.sort(priorityQueue);
                /*
				System.out.println("======================");
				Iterator<TotalOrderMulticastMessage> iterator = priorityQueue.iterator();
				while(iterator.hasNext()){
					System.out.println(iterator.next());
				}
				
				System.out.println("======================");
                 */

                while (priorityQueue.isEmpty() == false) {
                    TotalOrderMulticastMessage entry = priorityQueue.get(0);
                    //System.out.println(entry);
                    if (entry == null || entry.isDeliverable() == false) {
                        break;
                    } else if (entry.isDeliverable()) {
                        System.out.println("deliver total order message: " + entry.getContent());
                        priorityQueue.remove(0);//();
                    }
                }
            }
        } //fine synchronized
    }
    
    
    public void addToAlive(int p){
        synchronized(this){
            alive.add(p);
        }
    }
    
    public void removeToAlive(int p){
        synchronized(this){
            System.out.println("Rimuovo un RM sospetto dalla lista degli alive");
            alive.remove(p);
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
                            bufferMessageTable.get(1).remove(finalMessage);
                            groupLastSequence.put(1, finalSequence);
                            // IL SENDER PRENDE IL PROPOSAL MSG, elabora il final e lo tramsette a tutti i membri del gruppo in formato JSON


                            cachedSequenceTable.remove(entry.getKey());

                            System.out.println("Invio a tutti tranne al RM attualmente sospetto");

                            basicMulticast(finalMessage.toString());
                    }

                }
            }  
        }    
    }
    
    
    private boolean isReadyToSendFinal(Map<Integer,Integer> cachedSequence){
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
        
        return result;
    }
    
    

    private static void offer(java.lang.String offerMsg) {
        offer.OfferWebService_Service service = new offer.OfferWebService_Service();
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
            port.offer(offerMsg);
        }
        
    }
    
    
}
