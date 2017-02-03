/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;


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
        return port.select(email, password);
    }

 
  
    
}
