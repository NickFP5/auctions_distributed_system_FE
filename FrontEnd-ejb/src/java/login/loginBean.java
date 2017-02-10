/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;


import java.io.StringReader;
import java.util.Iterator;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import netConf.NetworkConfigurator;
import netConf.NetworkNode;
import resources.User;
import user.UserWebService_Service;



/**
 *
 * @author Nick F
 */
@Stateless
public class loginBean implements loginBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/userWebService.wsdl")
    private UserWebService_Service service;



    
    
    
    @Override
    public int check(User u){
        // invocazione al web service delle repliche per verificare se l'utente è presente o meno
        
        System.out.println("Dentro check loginBean frontend");
        String result = select(u.getEmail(), u.getPassword());
        
        JsonObject jo = Json.createReader(new StringReader(result)).readObject();
        String email = (String) jo.getString("email");
        int user_id = jo.getInt("id");
        System.out.println("Valore email: " + email);
        
        if(email != null) return user_id;
        
        else return -1;
        //return false;
    }

    private String select(java.lang.String email, java.lang.String password) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        user.UserWebService port = service.getUserWebServicePort();
        //return port.select(email, password);
        String result = null;
        
        BindingProvider bindingProvider; //classe che gestisce il cambio di indirizzo quando il webservice client deve riferirsi a webservice che stanno su macchine diverse
        bindingProvider = (BindingProvider) port;
        //String s = "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+NetworkConfigurator.getInstance(false).getMyself().getPort()+"/ReplicaManager-war/userWebService";
;
        //System.out.println("FRONTEND WebService --> " + s);
        NetworkNode node;
        int porta = 0;
        for(Iterator i  = NetworkConfigurator.getInstance(false).getReplicas().listIterator(); i.hasNext();){
            System.out.println("Dentro for select bean FE");
            node = (NetworkNode) i.next();
            if(NetworkConfigurator.getInstance(false).getMyself().getIp().equals(node.getIp())){
                System.out.println("Trovata replica che ha il mio stesso ip, mi prendo la sua porta");
                porta = node.getPort();
                break;
            }
        }
        
        String s = "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+porta+"/ReplicaManager-war/userWebService";
        System.out.println("FRONTEND WebService --> " + s);
        
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://"+NetworkConfigurator.getInstance(false).getMyself().getIp()+":"+porta+"/ReplicaManager-war/userWebService"
            );
        
        
        
        try{
                result = port.select(email, password);
            }catch(Exception ex){
                System.err.println("Errore di rete");
            }
        
        return result;
        
    }

 
  
    
}
