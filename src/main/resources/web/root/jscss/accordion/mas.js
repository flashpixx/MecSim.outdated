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

"use strict";

// --- MAS PANEL -------------------------------------------------------------------------------------------------------

var MASPanel = ( function (px_module) {

    px_module.bind_ui_actions = function() {

        MASPanel.ui().mecsim_multiagent_panel().on("click", function(data){
            MecSim.ui().content().empty();
        });

        MASPanel.ui().mecsim_jason_mindinspector_button().button().on("click", function(){
            MecSim.ui().content().load("template/mindinspector.htm");
        });

    }

    // --- UI references -----------------------------------------------------------------------------------------------

    /**
     * references to static UI components of the MAS panel
     **/
    px_module.ui = function() {return {

        /** reference to mecsim jason mindinspector button **/
        mecsim_jason_mindinspector_button : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_mas_jasonmindinspector" : $("#mecsim_mas_jasonmindinspector"); },
        /** reference to mas panel **/
        mecsim_multiagent_panel : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_multiagent_panel" : $("#mecsim_multiagent_panel"); }

    };}
    // -----------------------------------------------------------------------------------------------------------------

    return px_module;

}(MASPanel || {}));