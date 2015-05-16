/**
 * base class modul to represent base algorithms
 **/
var MecSimNew = (function (px_modul) {


    /**
     * websocket structure to encapsulate
     * access to the internal websocket acces
     * with function binding

     * @param pc_wspath path of the websocket (can set relative to the location)
     * @param po_options object with onmessage, onerror, onopen and onclose functions
     **/
    px_modul.WebSocket = function( pc_wspath, po_options )
    {
        var lo_options   = po_options || {};
        var lo_socket;

        if ((pc_wspath.startsWith("ws://")) || (pc_wspath.startsWith("wss://")))
            lo_socket = new WebSocket(pc_wspath);
        else
            lo_socket = new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (pc_wspath.startsWith("/") ? pc_wspath : location.pathname + pc_wspath ) );

        lo_socket.onopen    = lo_options.onopen || null;
        lo_socket.onclose   = lo_options.onclose || null;
        lo_socket.onmessage = lo_options.onmessage || null;
        lo_socket.onerror   = lo_options.onerror || null;
    }


    /**
     * logger structure to encapsulate
     * Java logger access
     **/
     px_modul.Logger = {

        "get"   : function( px_success ) { $.ajax({ url : "/clogger/configuration", type: "POST",  success : px_success }); },

        "error" : function( pc_message ) { $.ajax({ url : "/clogger/error",         type: "POST",  data :pc_message  }); },
        "debug" : function( pc_message ) { $.ajax({ url : "/clogger/debug",         type: "POST",  data :pc_message  }); },
        "warn"  : function( pc_message ) { $.ajax({ url : "/clogger/warn",          type: "POST",  data :pc_message  }); },
        "info"  : function( pc_message ) { $.ajax({ url : "/clogger/info",          type: "POST",  data :pc_message  }); },
        "out"   : function( pc_message ) { $.ajax({ url : "/clogger/out",           type: "POST",  data :pc_message  }); }
     }



    return px_modul;

}(MecSimNew || {}));
