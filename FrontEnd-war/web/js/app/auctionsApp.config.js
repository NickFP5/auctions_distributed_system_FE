/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('auctionsApp')
        .config(function ($mdThemingProvider) {

            var myBlueMap = $mdThemingProvider.extendPalette('blue', {
                '500': '#127ca4'//, //  darkgreen: #006400 forestgreen: #228b22 limegreen: #adff2f
                        //'contrastDefaultColor': '#ffd700'
            });

            // Register the new color palette map with the name <code>neonRed</code>
            $mdThemingProvider.definePalette('myBlue', myBlueMap);


            $mdThemingProvider.theme('default')
                    .primaryPalette('myBlue') //pink
        });