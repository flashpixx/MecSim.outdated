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


/**
 * base modul to represent base algorithms
 * and structure to encapsulate structures
 **/
var MecSim = (function (px_modul) {


    // --- websocket structure ---------------------------------------------------------------------------------------------------------------------------------
    /**
     * websocket structure to encapsulate access to the internal websocket structure  with function binding
     *
     * @param pc_wspath path of the websocket (can set relative to the window location)
     * @param po_options object with onmessage, onerror, onopen and onclose functions to bind functions directly
     * @returns websocket
     **/
    px_modul.websocket = function( pc_wspath, po_options )
    {
        var lo_options   = po_options || {};
        var lo_socket;

        if ((pc_wspath.startsWith("ws://")) || (pc_wspath.startsWith("wss://")))
            lo_socket = new WebSocket(pc_wspath);
        else
            lo_socket = new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (pc_wspath.startsWith("/") ? pc_wspath : location.pathname + pc_wspath ) );

        if (lo_socket !== undefined)
        {
            lo_socket.onopen    = function() { if (lo_options.onopen) lo_options.onopen(lo_socket); };
            lo_socket.onclose   = lo_options.onclose   || null;
            lo_socket.onmessage = lo_options.onmessage || null;
            lo_socket.onerror   = lo_options.onerror   || null;
        }

        return lo_socket;
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // ---- ajax request ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * redefined jQuery ajax request, with equal option fields
     * @see http://api.jquery.com/jquery.ajax/

     * @param po_option Ajax request object or URL
     * @return jQuery Ajax object
    **/
    px_modul.ajax = function( px_options )
    {
        // in strict mode a deep-copy is needed / string defines the URL
        var lo_options    = classof(px_options, "string") ? { "url" : px_options } : jQuery.extend( true, {}, px_options );
        lo_options.method = lo_options.method || "post";

        return jQuery.ajax( lo_options );
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- logger structure ------------------------------------------------------------------------------------------------------------------------------------
    /**
     * logger structure to encapsulate functionality of Java logger class
     **/
    px_modul.logger = function() {return{

        /**
         * function to read the logger configuration
         * @param px_success function which is called on read success
         **/
        get   : function( px_success ) { jQuery.ajax({ url : "/clogger/configuration", type: "post",  success : px_success }); },

        /**
         * error logging
         * @param px_message message
         **/
        error : function( px_message ) { console.error(px_message); jQuery.ajax({ url : "/clogger/error", type: "post",  data : px_message  }); },

        /**
         * debug logging
         * @param px_message message
         **/
        debug : function( px_message ) { console.warn(px_message); jQuery.ajax({ url : "/clogger/debug", type: "post",  data : px_message  }); },

        /**
         * warning logging
         * @param px_message message
         **/
        warn  : function( px_message ) { console.warn(px_message); jQuery.ajax({ url : "/clogger/warn", type: "post",  data : px_message  }); },

        /**
         * information logging
         * @param px_message message
         **/
        info  : function( px_message ) { console.info(px_message); jQuery.ajax({ url : "/clogger/info", type: "post",  data : px_message  }); },

        /**
         * output logging
         * @param px_message message
         **/
        out   : function( px_message ) { console.log(px_message); jQuery.ajax({ url : "/clogger/out", type: "post",  data : px_message  }); }

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // ---- configuration structure ----------------------------------------------------------------------------------------------------------------------------
    /**
     * configuration access to encapsulate access
    **/

    px_modul.configuration = function() { return {

        /** get the configuration
         * @param px_success function which is called on successful reading
         * @return returns the jQuery Ajax object
         **/
        get : function( px_success ) { return jQuery.ajax({ url : "/cconfiguration/get", type: "post",  success : px_success }); },

        /**
         * sets the configuration
         * @param po_data full configuration object
         * @return returns the jQuery Ajax object
         **/
        set : function( po_data ) { return jQuery.ajax({ url : "/cconfiguration/set", type: "post",  data : po_data }); },

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------



    // --- language access -------------------------------------------------------------------------------------------------------------------------------------
    /**
     * language access for setting text elements
     * @param po_options Json configuration object with the structure
     * { url: -map<string,string> with the translating strings-,
     *   each: -optional function that is called on each pair-,
     *   finish: -option function that is called after all pairs are finished-,
     *   target: -optional pane element to set translation automatically-,
     *   idgenerator : -optional generator function within the target for generating ID names-
     * }
     * @return ajax request object
    **/
    px_modul.language = function(po_options)
    {
        return jQuery.ajax({
            url : po_options.url,
            type: "post",

            success : function( po_data )
            {
                if (po_options.each)
                    jQuery.each(po_data, po_options.each);

                else if (po_options.target instanceof Pane)
                {
                    var lx_generator = po_options.idgenerator ? po_options.idgenerator : po_options.target.generateSubID;

                    jQuery.each(po_data, function(pc_id, pc_translation) {
                       var la = pc_id.split("_");

                        if ((la.length == 1) && (la[0] === "name"))
                        {
                            po_options.target.setName( pc_translation );
                            return;
                        }

                        la[1] = la.slice(1).join("_");
                        if (la[0] === "id")
                            jQuery( lx_generator.call(po_options.target, la[1], "#") ).text( pc_translation );
                        if (la[0] === "label")
                            jQuery( 'label[for="' + lx_generator.call(po_options.target, la[1]) + '"]' ).text( pc_translation );
                        if (la[0] === "title")
                            jQuery( lx_generator.call(po_options.target, la[1], "#") ).prop( "title", pc_translation );
                    });
                }

                if (po_options.finish)
                    po_options.finish();
            }
        });
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- UI references ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * references to static UI components
     **/
    px_modul.ui = function() {return {

        /** reference to the screen area **/
        screen      : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_screen"; },
        /** reference to the menu **/
        menu        : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_menu"; },
        /** reference to the accordion **/
        accordion   : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_accordion"; },
        /** reference to the menu section of the screen **/
        screenmenu  : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_screen_right"; },
        /** reference to the content area **/
        content     : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_content"; },
        /** reference to a static (hidden) content area **/
        static      : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_static"; },
        /** reference to log area **/
        log         : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_log"; }
    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- initializing of the UI content ----------------------------------------------------------------------------------------------------------------------
    var ml_once = false;
    px_modul.uiinitialize = function( pa )
    {
        if (ml_once)
            throw "MecSim initializing can run only once";
        ml_once = true;

        // create basic layout within the body element (three frame structure)
        jQuery( '<div id = "' + px_modul.ui().screen()     + '" >' +
                '<div id = "' + px_modul.ui().menu()       + '" >' +
                '<div id = "' + px_modul.ui().accordion()  + '" />' +
                '</div>' +
                '<div>' +
                '<div id = "' + px_modul.ui().screenmenu() + '" >' +
                '<div>' +
                '<div id = "' + px_modul.ui().static()    + '" ></div >' +
                '<div id = "' + px_modul.ui().content()    + '" ></div >' +
                '</div>' +
                '<div id = "' + px_modul.ui().log()        + '" ></div >' +
                '</div >' +
                '</div >' +
                '</div>').appendTo("body");


        // add main layout elements to the HTML body
        if (Array.isArray(pa))
        {
            // create a list with used ID to avoid duplicates - a set is a better choice,
            // but Java WebKit component does not support the ECMAScript6 specification
            var lo_ids = [];

            pa.forEach( function(px_item) {
                if (px_item instanceof Pane)
                {
                    if (jQuery.inArray(px_item.getID(), lo_ids) > -1)
                        throw "Pane [" + px_item.getID() + "] exists on the global screen";
                    lo_ids.push(px_item.getID());

                    // global elements
                    if (px_item.getGlobalContent())
                        jQuery( px_item.getGlobalContent() ).appendTo("body");
                    if (px_item.getGlobalCSS())
                        jQuery("head").append( '<style type = "text/css">' + px_item.getGlobalCSS() + '</style>' );

                    // accordion elements
                    if (!px_item.isHidden())
                    {
                        var lc = px_item.getName();
                        if (!lc)
                            lc = "";

                        jQuery( '<h3 id = "' + px_item.getID() + '">' + lc + '</h3>' ).appendTo( px_modul.ui().accordion("#") );
                        jQuery( px_item.getContentWithContainer() ).appendTo( px_modul.ui().accordion("#") );
                    }

                    px_item.afterDOMAdded();
                }
            });
        }

        // main CSS
        jQuery("head").append(
            '<style type = "text/css">' +

            // sets the menu layout
            px_modul.ui().menu("#")    + ' { z-index: 0; background-image: url(theme/images/tuc_small.gif); background-position: 50% 97%; background-repeat: no-repeat; }' +
            // sets the content layout (with logo)
            px_modul.ui().content("#") + '::before { z-index: 0; content: ""; position: fixed; top: 30%; left: 50%; opacity: 0.35; height: 145px; width: 515px; background-image: url(theme/images/tuc.svg); background-repeat: no-repeat; }' +
            px_modul.ui().content("#") + ' { z-index: 0; overflow: auto; }' +
            /** sets the static content layout **/
            px_modul.ui().static("#") + ' { z-index: 1; position: absolute; }' +
            // sets the log layout
            px_modul.ui().log("#") + ' { z-index: 0; overflow: auto; }' +


            // --- set default CSS definition ---

            // defines HTML / body with 100% height and width for the splitter content (font values are copied from jQuery-UI)
            'html, body { height: 100%; width: 100%; margin: 0; padding: 0; overflow: hidden;    font-family: Helvetica,Arial,sans-serif; font-size:1em; color:#222222 }' +
            // overwrite the default a tag (font values are copied from jQuery-UI)
            'a { text-decoration: none;    color: #222222; }' +
            // select needs a width, because jQuery sets it to size = 0
            'select { width: 125px; }' +
            // resizing iFrame to the full parent element size
            'iframe{ width: 100%; height: 100%; position: absolute; }' +
            // jQuery button is set to a minimale size
            '.ui-button-text-only { min-height: 1.8em; min-width: 35px; padding-top: 0.25em; padding-bottom: 0.25em; padding-left: 1em; padding-right: 1em; }' +
            // jQuery sortable without bullet
            '.ui-sortable { list-style-type: none; }' +
            'li.ui-state-default { min-height: 0.5em; padding: 0.5em; white-space: nowrap; text-overflow: ellipsis; overflow: hidden; }' +

            '</style>'
        );

        // initialize the content pane with the three layer structures - must be called at the end, because of correct layout structure
        jQuery( px_modul.ui().screen("#") ).jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        jQuery( px_modul.ui().screenmenu("#") ).jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });
        jQuery( px_modul.ui().accordion("#") ).accordion({ active: false, collapsible: true, heightStyle: "content" });

        // run startup checks and show result
        px_modul.ajax({
            url : "/cconfiguration/startupchecks"
        }).done(function(po) {
            if (po.messages.length == 0)
                return;

            //console.log(po.messages);
        });

    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return px_modul;

}(MecSim || {}));
