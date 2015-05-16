/**
 * base class modul to represent websocket communication
 **/

var MecSimNew = (function (px_modul) {

    /**
     * creates a websocket
     * @param pc_wspath path of the websocket (can set relative to the location)
     * @param po_options object with onmessage, onerror, onopen and onclose functions
     **/
    px_modul.connectWebSocket = function( pc_wspath, po_options )
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




    return px_modul;

}(MecSimNew || {}));
