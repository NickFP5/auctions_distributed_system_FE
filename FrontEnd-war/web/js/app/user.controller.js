/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function(angular) {

    'use strict';

    angular
        .module('auctionsApp')
        .controller('userController', userController);


    function userController($q,$window) {
        var vm = this;
        var loginUri = "ws://" + window.location.host +"/FrontEnd-war/login";
        var registerUri = "ws://" + window.location.host +"/FrontEnd-war/register";
        var output;
        var websocket; // = new WebSocket(wsUri);
        
        vm.log_in =log_in;
        vm.register = register;
        
        
       
       function log_in(){
           //console.log(JSON.stringify(newItem));
           console.log("Dentro log_in prima di web sock send");
           vm.logininfo = {
               email: vm.email,
                password: vm.password
           };
           console.log(JSON.stringify(vm.logininfo));
            connect_log(loginUri).then(function(){
                console.log("Invio--> " + vm.logininfo);
                websocket.send(JSON.stringify(vm.logininfo));
            }); 
            
            
       }
       
       function register(){
           //console.log(JSON.stringify(newItem));
           vm.reginfo = {
               name: vm.name,
               email: vm.email,
                password: vm.password
           };
           console.log(JSON.stringify(vm.reginfo));
            connect_reg(registerUri).then(function(){
                websocket.send(JSON.stringify(vm.reginfo));
            }); 
            $window.location.href = '/FrontEnd-war/login.html';
       }
       
       
       
       
       
        
        function connect_log(uri){
            console.log(" Dentro connect_log");
            var d = $q.defer();
            websocket = new WebSocket(uri);
            websocket.onmessage = function(message) {
                console.log(" Dentro connect log #### Message received: " + message.data); 
                //vm.logininfo = JSON.parse(message.data);,
                if(parseInt(message.data) > 0) {
                    sessionStorage.setItem('email', message.data.email);
                    sessionStorage.setItem('user_id', parseInt(message.data));
                }
                websocket.close();
                $window.location.href = '/FrontEnd-war/viewAuctions.html';
            };
            websocket.onopen = function(){  
                console.log("Socket has been opened!");
                d.resolve();
            };
            return d.promise;
        }
        
        
        function connect_reg(uri){
            var d = $q.defer();
            websocket = new WebSocket(uri);
            websocket.onmessage = function(message) {
                console.log("Message received: " + message.data); 
                //vm.logininfo = JSON.parse(message.data);,
                
                websocket.close();
                
            };
            websocket.onopen = function(){  
                console.log("Socket has been opened!");
                d.resolve();
            };
            return d.promise;
        }        
        
        
        
        
        
    }

})(window.angular);

