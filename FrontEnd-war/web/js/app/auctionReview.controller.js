/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function(angular) {

    'use strict';

    angular
        .module('auctionsApp')
        .controller('auctionReviewController', auctionReviewController);


    function auctionReviewController($q, $timeout) {
        var vm = this;
        var createUri = "ws://localhost:8080/FrontEnd-war/createItem";
        var output;
        var websocket; // = new WebSocket(wsUri);
        
        vm.deleteItem = deleteItem;
        
        vm.item = {
            id: 157,
            title: "Item 157",
            price: 157.7,
            seller_id: 1,
            millis: Date.now()
        };
        
        vm.cd = countdown(null, new Date(2017, 2, 1, 13, 59, 33));
        vm.countdown = vm.cd.toString();
        
        vm.executeAsync = function() {
            asyncAction().then(function(response) {
                vm.message = response;
                vm.executeAsync();
                console.log("New executeAsync");
            });
        }

        var asyncAction = function() {
            var d = $q.defer();

            $timeout(function() {
                d.resolve("Executed");
                vm.cd = countdown(null, new Date(2017, 2, 1, 13, 59, 33));
                vm.countdown = vm.cd.toString();
            }, 1000);

            return d.promise;
        }
        
        vm.executeAsync();
        
        function deleteItem() {
            var deleteinfo = {
                id: vm.itemid
            };
            console.log(JSON.stringify(deleteinfo));
            connect(createUri).then(function(){
                websocket.send(JSON.stringify(deleteinfo));
            }); 
        }
        
        
        function connect(uri){
            var d = $q.defer();
            websocket = new WebSocket(uri);
            websocket.onmessage = function(message) {
                console.log("Message received: " + message); 
            };
            websocket.onopen = function(){  
                console.log("Socket has been opened!");
                d.resolve();
            };
            return d.promise;
        }
        
    }

})(window.angular);

