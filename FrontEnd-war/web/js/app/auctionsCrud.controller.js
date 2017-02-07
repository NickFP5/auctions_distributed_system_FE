/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function(angular) {

    'use strict';

    angular
        .module('auctionsApp')
        .controller('auctionsCrudController', auctionsCrudController);


    function auctionsCrudController($q) {
        var vm = this;
        var createUri = "ws://" + window.location.host +"/FrontEnd-war/createItem";
        var editTitleUri = "ws://" + window.location.host +"/FrontEnd-war/modifyItemTitle";
        var editPriceUri = "ws://" + window.location.host +"/FrontEnd-war/modifyItemPrice";
        var editExpDateUri = "ws://" + window.location.host +"/FrontEnd-war/modifyItemExpiringDate";
        var deleteUri = "ws://" + window.location.host +"/FrontEnd-war/removeItem";
        var output;
        var websocket; // = new WebSocket(wsUri);
        vm.itemid = null;
        vm.createItem = createItem;
        vm.editItem = editItem;
        vm.deleteItem = deleteItem;
        
        vm.editby = [];
        vm.editby.push({title: 'title', value: 1});
        vm.editby.push({title: 'price', value: 2});
        vm.editby.push({title: 'expirationDate', value: 3});
        vm.selectedEditCrit = 1;
        
       /* websocket.onopen = function(){  
            console.log("Socket has been opened!");  
        };*/

        

        //Creates a new item with the given parameters
        function createItem() {
            
            var millis = vm.expdate;
            millis.setHours(vm.exptime.getHours());
            millis.setMinutes(vm.exptime.getMinutes());
            millis.setSeconds(vm.exptime.getSeconds());
            
            var newItem = {
                title: vm.title,
                price: vm.price,
                millis: millis.getTime(),
                seller_id: vm.seller_id
            };
            console.log(JSON.stringify(newItem));
            connect(createUri).then(function(){
                websocket.send(JSON.stringify(newItem));
            }); 
        }
        
        function deleteItem() {
            var deleteinfo = {
                id: vm.itemid
            };
            console.log(JSON.stringify(deleteinfo));
            connect(deleteUri).then(function(){
                websocket.send(JSON.stringify(deleteinfo));
            }); 
        }
        
        function editItem() {
            switch(vm.selectedEditCrit){
                case 1:
                    var editvalues_title = {
                        id: vm.itemid,
                        title: vm.title
                    };
                    console.log(JSON.stringify(editvalues_title));
                    connect(editTitleUri).then(function(){
                        websocket.send(JSON.stringify(editvalues_title));
                    });
                    break;
                case 2:
                    var editvalues_price = {
                        id: vm.itemid,
                        price: vm.price
                    };
                    console.log(JSON.stringify(editvalues_price));
                    connect(editPriceUri).then(function(){
                        websocket.send(JSON.stringify(editvalues_price));
                    });
                    break;
                case 3:
                    
                    var millis = vm.expdate;
                    millis.setHours(vm.exptime.getHours());
                    millis.setMinutes(vm.exptime.getMinutes());
                    millis.setSeconds(vm.exptime.getSeconds());
                    
                    var editvalues_expdate = {
                        id: vm.itemid,
                        millis: millis.getTime()
                    };
                    console.log(JSON.stringify(editvalues_expdate));
                    connect(editExpDateUri).then(function(){
                        websocket.send(JSON.stringify(editvalues_expdate));
                    });
                    break;
            }
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
