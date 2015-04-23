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


/**
 * global singleton prototype of MecSim calls
 * @return instance
 */
function MecSim()
{

    /** instantiation **/
    if ( arguments.callee._singletonInstance )
        return arguments.callee._singletonInstance;
    arguments.callee._singletonInstance = this;

    /** private UI components **/
    var m_ui = {
        accordion   : $("#mecsim_global_accordion").accordion({ active: false, collapsible: true }),
        menu        : $("#mecsim_global_menu"),
        content     : $("#mecsim_global_content")
    };


    /**
     * creates a websocket instance
     * @param pc_wspath path of the web socket (relative or full URI)
     * @returns websocket instance
     */
    this.getWebSocket = function( pc_wspath )
    {
        if ((pc_wspath.startsWith("ws://")) || (pc_wspath.startsWith("wss://")))
            return new WebSocket(pc_wspath);

        return new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (pc_wspath.startsWith("/") ? pc_wspath : location.pathname + pc_wspath ) );
    };


    /**
     * reads the configuration JSON data
     * @param px_success success function call
     */
    this.getConfiguration = function( px_success )
    {
        $.ajax({
            url : "/cconfiguration/get",
            success : px_success
        });
    };

};

