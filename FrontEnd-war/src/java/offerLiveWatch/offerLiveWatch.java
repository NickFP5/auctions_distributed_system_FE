/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offerLiveWatch;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.Session;
import offer.offerBeanLocal;
import resources.Transaction;

/**
 *
 * @author alessandrotorcetta
 */
public class offerLiveWatch implements Runnable {

    private offerBeanLocal offerBean;
    private Session session;
    private int item_id;
    private Transaction t;

    public offerLiveWatch(Session session, int item_id) {
        this.offerBean = lookupofferBeanLocal();
        this.session = session;
        this.item_id = item_id;

    }

    @Override
    public void run() {
        String transaction;
        JsonObject jo;
        int id;
        int user_id;
        long timestamp;
        int item_id_t;
        float amount;
        boolean succ;
        Date date;
        Date date2;
        JsonObjectBuilder job2;
        JsonObject jo2;
        boolean first = false;
        while (true) {
            if(!this.session.isOpen()) break;
            System.out.println("Dentro while");
            synchronized(this) {
            transaction = this.offerBean.findTransaction(item_id);
            }
            jo = Json.createReader(new StringReader(transaction)).readObject();
            if(jo.getBoolean("exist")!= true){
                System.out.println("Non esiste la transazione");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(offerLiveWatch.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }
            
            id = jo.getInt("id");
            user_id = jo.getInt("user_id");
            timestamp = (long) jo.getJsonNumber("timestamp").longValue();
            item_id_t = jo.getInt("item_id");
            amount = (float) jo.getJsonNumber("amount").doubleValue();
            succ = jo.getBoolean("successful");

            if (t == null) {
                date = new Date();
                date.setTime(timestamp);
                t = new Transaction();
                t.setId(id);
                t.setAmount(amount);
                t.setTimestamp(date);
                t.setSuccessful(succ);

            } else {
                if (t.getAmount() < amount || first == false) {
                    first = true;
                    date2 = new Date();
                    date2.setTime(timestamp);
                    t.setAmount(amount);
                    t.setTimestamp(date2);
                    t.setSuccessful(succ);
                    job2 = (JsonObjectBuilder) Json.createObjectBuilder();   
                    job2.add("id", t.getId());
                    job2.add("user_id", user_id);
                    job2.add("timestamp", t.getTimestamp().getTime());
                    job2.add("item_id", item_id_t);
                    job2.add("amount", t.getAmount());
                    job2.add("successful", t.getSuccessful());
                    job2.add("exist", true);
                    jo2 = job2.build();
                    try {
                        this.session.getBasicRemote().sendText(jo2.toString());
                    } catch (IOException ex) {
                        Logger.getLogger(offerLiveWatch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(offerLiveWatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private offerBeanLocal lookupofferBeanLocal() {
        try {
            Context c = new InitialContext();
            return (offerBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/offerBean!offer.offerBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
