/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

//"use strict";

// --- MAS PANEL --------------------------------------------------------------------------------------------------

var mecsim_mas,
    MASPanel = {

        settings: {
            jasonmindinspector: $("#mecsim_mas_jasonmindinspector").button()
        },

        init: function() {
            mecsim_mas = this.settings;
            this.bind_ui_actions();
        },

        bind_ui_actions: function() {

            $("#ui-id-8").on("click", function(data){
                MecSim.ui().content().empty();
            });

            MASPanel.settings.jasonmindinspector.on("click", function(){
                MecSim.ui().content().load("template/mindinspector.htm");
            });

        }

    };
