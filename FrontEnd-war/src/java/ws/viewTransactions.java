/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import viewTransactions.viewTransactionsBeanLocal;

/**
 *
 * @author alessandrotorcetta
 */
@ServerEndpoint(value="/viewTransactions", configurator = GetHttpSessionConfigurator.class)
public class viewTransactions {

    viewTransactionsBeanLocal viewTransactionsBean = lookupviewTransactionsBeanLocal();

    
     
    @OnMessage
    public String onMessage(String message, Session session, EndpointConfig config) {
        System.out.println("Dentro onMessage viewTransaction");
        return viewTransactionsBean.viewTransactions();
    }

    @OnClose
    public void onClose(Session session) {
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Dentro onOpen viewTransaction--->");
        //System.out.println(viewTransactionsBean.viewTransactions());
        
        
        
        try {
            System.out.println("Dentro onOpen viewTransaction--->prima di invoc");
            session.getBasicRemote().sendText(viewTransactionsBean.viewTransactions());
            System.out.println("Dentro onOpen viewTransaction ---> dopo prima invoc");
        } catch (IOException ex) {
            System.out.println("Dentro catch di open msg");
            Logger.getLogger(viewTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("SONO IN ERRORE");
    }

    private viewTransactionsBeanLocal lookupviewTransactionsBeanLocal() {
        try {
            Context c = new InitialContext();
            return (viewTransactionsBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/viewTransactionsBean!viewTransactions.viewTransactionsBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
