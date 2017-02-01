/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function(angular) {

    'use strict';

    angular
        .module('auctionsApp')
        .controller('viewAuctionsController', viewAuctionsController);


    function viewAuctionsController($q) {
        var vm = this;
        var viewUri = "ws://localhost:8080/FrontEnd-war/viewAuctions";
        var output;
        var websocket;
        vm.itemid = null;
        
        vm.items = [];
        vm.itemsraw = null;
        
        connect(viewUri).then(function(){
            console.log("promise resolved")
            vm.items = JSON.parse(vm.itemsraw);
        });
        
        function connect(uri){
            console.log("Connecting to: " + uri);
            var d = $q.defer();
            websocket = new WebSocket(uri);
            
            websocket.onmessage = function(message) {
                console.log("Message received: " + message.data); 
                vm.itemsraw = message.data;
                d.resolve();
            };
            
            
            websocket.onopen = function(){  
                console.log("Socket has been opened!");
                websocket.send("dummy data");
                
            };
            
            
            
            return d.promise;
        }

        
    }

})(window.angular);
