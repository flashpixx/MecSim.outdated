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
 * base modul to represent base algorithms
 * and structure to encapsulate
 **/
var MecSim = (function (px_modul) {



    // --- websocket structure ---------------------------------------------------------------------------------------------------------------------------------
    /**
     * websocket structure to encapsulate access to the internal websocket structure  with function binding
     *
     * @param pc_wspath path of the websocket (can set relative to the window location)
     * @param po_options object with onmessage, onerror, onopen and onclose functions to bind functions direct
     **/
    px_modul.WebSocket = function( pc_wspath, po_options )
    {
        var lo_options   = po_options || {};
        var lo_socket;

        if ((pc_wspath.startsWith("ws://")) || (pc_wspath.startsWith("wss://")))
            lo_socket = new WebSocket(pc_wspath);
        else
            lo_socket = new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (pc_wspath.startsWith("/") ? pc_wspath : location.pathname + pc_wspath ) );

        if (lo_socket !== undefined)
        {
            lo_socket.onopen    = lo_options.onopen || null;
            lo_socket.onclose   = lo_options.onclose || null;
            lo_socket.onmessage = lo_options.onmessage || null;
            lo_socket.onerror   = lo_options.onerror || null;
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- logger structure ------------------------------------------------------------------------------------------------------------------------------------
    /**
     * logger structure to encapsulate functionality of Java logger class
     **/
    px_modul.Logger = function() {return{

        /**
         * function to read the logger configuration
         * @param px_success function which is called on read success
         **/
        "get"   : function( px_success ) { $.ajax({ url : "/clogger/configuration", type: "POST",  success : px_success }); },

        /**
         * error logging
         * @param px_message message
         **/
        "error" : function( px_message ) { $.ajax({ url : "/clogger/error",         type: "POST",  data : px_message  }); },

        /**
         * debug logging
         * @param px_message message
         **/
        "debug" : function( px_message ) { $.ajax({ url : "/clogger/debug",         type: "POST",  data : px_message  }); },

        /**
         * warning logging
         * @param px_message message
         **/
        "warn"  : function( px_message ) { $.ajax({ url : "/clogger/warn",          type: "POST",  data : px_message  }); },

        /**
         * information logging
         * @param px_message message
         **/
        "info"  : function( px_message ) { $.ajax({ url : "/clogger/info",          type: "POST",  data : px_message  }); },

        /**
         * output logging
         * @param px_message message
         **/
        "out"   : function( px_message ) { $.ajax({ url : "/clogger/out",           type: "POST",  data : px_message  }); }

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // ---- configuration structure ----------------------------------------------------------------------------------------------------------------------------
    /**
     * configuration access to encapsulate access
    **/

    px_modul.Configuration = function() { return {

        /** get the configuration
         * @param px_success function which is called on successful reading
         **/
        "get" : function( px_success ) { $.ajax({ url : "/cconfiguration/get", type: "POST",  success : px_success }); },

        /**
         * sets the configuration
         * @param po_data full configuration object
         **/
        "set" : function( po_data ) { $.ajax({ url : "/cconfiguration/set", type: "POST",  data : po_data }); },

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- UI references ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * references to static UI components
     **/
    px_modul.UI = function() {return {

        /** reference to the accordion **/
        "accordion"   : $("#mecsim_global_accordion"),
        /** reference to the menu **/
        "menu"        : $("#mecsim_global_menu"),
        /** reference to the content area **/
        "content"     : $("#mecsim_global_content"),
        /** reference to the object inspector **/
        "inspector"   : $( "#mecsim_object_inspector" ),
        /** reference to log area **/
        "log"         : $("#mecsim_global_log"),
        /** reference to the screen area **/
        "screen"      : $("#mecsim_global_screen"),
        /** reference to the menu section of the screen **/
        "menu"        : $("#mecsim_global_screen_right")

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    return px_modul;

}(MecSim || {}));
