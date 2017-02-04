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

public class totalOrderMulticastSender {

    private static totalOrderMulticastSender _instance = new totalOrderMulticastSender();
    private Map<Integer, List<TotalOrderMulticastMessage>> holdbackQueueTable;
    private Map<Integer, Integer> groupLastSequence;
    private Map<Integer, Map<Integer, List<Integer>>> groupProposalSequence;
    private Map<Integer, Integer> groupMessageCounter;
    private Map<Integer, Map<Integer, TotalOrderMulticastMessage>> bufferMessageTable;
    //private BasicMulticast basicMulticast;
    private Object _mutex;
    private List<String> deliveryQueue;
    private int  myport, myid, mygroup, mygroupsize;
    private String myname, myip;
    

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
            String myconfig = "1 127.0.0.1 8080 FR1 1";
             
            String[] aux = myconfig.split(" ");
            myid = Integer.parseInt(aux[0]);
            myip = aux[1];
            myport = Integer.parseInt(aux[2]);
            myname = aux[3];
            mygroup = Integer.parseInt(aux[4]);
            mygroupsize = 2;
            
            System.out.println("MIO ID-->" + myid + "MIO IP--->" + myip + "MIO NOME--->" + myname);
            
          //  } catch (FileNotFoundException ex) {
          //  Logger.getLogger(totalOrderMulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        //}
            
            holdbackQueueTable = new Hashtable<Integer, List<TotalOrderMulticastMessage>>();
            groupLastSequence = new Hashtable<Integer, Integer>();
            //basicMulticast = BasicMulticast.getInstance();
            bufferMessageTable = new Hashtable<Integer, Map<Integer, TotalOrderMulticastMessage>>();
            groupProposalSequence = new Hashtable<Integer, Map<Integer, List<Integer>>>();
            groupMessageCounter = new Hashtable<Integer, Integer>();
            _mutex = new Object();
            deliveryQueue = new LinkedList<String>();
        
    }

    public static totalOrderMulticastSender getInstance() {
        return _instance;
    }

    public void send(int groupId, String groupMessage) {
        synchronized (_mutex) {
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
        }
    }

    public void basicMulticast(String msg) {
        System.out.println("Invio il messaggio, sono il SENDER BEAN");
        //per ognuno dei replicaManager
        offer(msg);
    }

    public void delivery(IMessage message) {
        synchronized (_mutex) {
            TotalOrderMulticastMessage tomm = (TotalOrderMulticastMessage) message;
            int groupId;
            int sequence;
            int selfId;
            List<TotalOrderMulticastMessage> priorityQueue;
            TotalOrderMessageType messageType;

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
                List<Integer> cachedSequence;
                Map<Integer, List<Integer>> cachedSequenceTable;
                int messageId = tomm.getMessageId();
                int proposeSequence = tomm.getSequence();
                if (groupProposalSequence.containsKey(groupId) == false) {
                    groupProposalSequence.put(groupId, new Hashtable<Integer, List<Integer>>());
                }
                cachedSequenceTable = groupProposalSequence.get(groupId);
                if (cachedSequenceTable.containsKey(messageId) == false) {
                    cachedSequenceTable.put(messageId, new LinkedList<Integer>());
                }

                cachedSequence = cachedSequenceTable.get(messageId);
                cachedSequence.add(proposeSequence);
                //System.out.println("receive proposed message: " + tomm);
     //   ------>        if (cachedSequence.size() == MemberIndexer.getInstance().getGroupSize(groupId)) {
            if (cachedSequence.size() == mygroupsize) {
                    int finalSequence = 0;
                    finalSequence = sequence > Collections.max(cachedSequence) ? sequence : Collections.max(cachedSequence);
                    TotalOrderMulticastMessage finalMessage = bufferMessageTable.get(groupId).get(messageId);
                    finalMessage.setSequence(finalSequence);
                    finalMessage.setMessageType(TotalOrderMessageType.FINAL);
                    //basicMulticast.send(groupId, finalMessage);
                    bufferMessageTable.get(groupId).remove(finalMessage);
                    groupLastSequence.put(groupId, finalSequence);
                    // IL SENDER PRENDE IL PROPOSAL MSG, elabora il final e lo tramsette a tutti i membri del gruppo in formato JSON
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
        }
    }

    private static String offer(java.lang.String offerMsg) {
        offer.OfferWebService_Service service = new offer.OfferWebService_Service();
        offer.OfferWebService port = service.getOfferWebServicePort();
        return port.offer(offerMsg);
    }

}
