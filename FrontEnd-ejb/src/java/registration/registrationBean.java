/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;


import java.io.StringReader;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.ws.WebServiceRef;
import resources.User;
import user.UserWebService_Service;

/**
 *
 * @author Nick F
 */
@Stateless
public class registrationBean implements registrationBeanLocal {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/ReplicaManager-war/userWebService.wsdl")
    private UserWebService_Service service;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    
    
    
    //modificare il void 
    
    
    @Override
    public void register(User u){
        
        System.out.println("Dentro register registerBean frontend");
        String result = insert(u.getEmail(), u.getName(),  u.getPassword());
        System.out.println("DOPO INSER REGBEAN ");
        

        /*
        
        JsonObject jo = Json.createReader(new StringReader(result)).readObject();
        String email = (String) jo.getString("email");
         
        System.out.println("Valore email: " + email);
     
        if(email != null) return false;
        
        else return true;
        //return false;
        */

     }

    private String insert(java.lang.String email, java.lang.String name, java.lang.String password) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        user.UserWebService port = service.getUserWebServicePort();
        return port.insert(email, name, password);
    }
}
