/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function(angular) {

    'use strict';

    angular
        .module('auctionsApp')
        .controller('paymentController', paymentController);


    function paymentController($q,$window) {
        var vm = this;
        var PaymentUri = "ws://" + window.location.host +"/FrontEnd-war/payment";
        var output;
        var websocket; // = new WebSocket(wsUri);
        
        vm.pay =pay;
        
        
       
       function pay(){
           //console.log(JSON.stringify(newItem));
           vm.payment = {
               user_id: vm.userid,
                item_id: vm.itemid,
           };
           console.log(JSON.stringify(vm.payment))
            connect(PaymentUri).then(function(){
                websocket.send(JSON.stringify(vm.payment));
            }); 
            $window.location.href = '/FrontEnd-war/viewAuctions.html';
       }
       
        
        function connect(uri){
            var d = $q.defer();
            websocket = new WebSocket(uri);
            websocket.onmessage = function(message) {
                console.log("Message received: " + message.data); 
                vm.transaction = JSON.parse(message.data);
                
            };
            websocket.onopen = function(){  
                console.log("Socket has been opened!");
                d.resolve();
            };
            return d.promise;
        }
        
    }

})(window.angular);

