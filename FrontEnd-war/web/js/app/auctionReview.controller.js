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
        var OfferUri = "ws://localhost:8080/FrontEnd-war/offer";
        var output;
        var websocket; // = new WebSocket(wsUri);
        
        vm.offerPrice = offerPrice;
        
        vm.item = {
            id: sessionStorage.getItem("item_id"),
            title: sessionStorage.getItem("item_title"),
            price: sessionStorage.getItem("item_price"),
            seller_id: sessionStorage.getItem("item_seller_id"),
            millis: sessionStorage.getItem("item_expiring_date")
        };
        
        vm.cd = countdown(null, vm.item.millis);
        vm.countdown = vm.cd.toString();
        var uriOffer = OfferUri +"/"+ vm.item.id.toString();
        console.log(uriOffer);
        connect(uriOffer);
        
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
                vm.cd = countdown(null, vm.item.millis);
                vm.countdown = vm.cd.toString();
            }, 1000);

            return d.promise;
        }
        
        vm.executeAsync();
        
        function offerPrice() {
            var offerinfo = {
                item_id:  vm.item.id,
                requestedPrice: vm.offer,
                user_id: sessionStorage.getItem('user_id')
            };
            console.log(JSON.stringify(offerinfo));
            //var uriOffer = OfferUri +"/"+ vm.item.id.toString();
            //console.log(uriOffer);
            //connect(uriOffer).then(function(){
            websocket.send(JSON.stringify(offerinfo));
            //}); 
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

