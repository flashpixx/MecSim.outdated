// --- global prototype functions ------------------------------------------------------------------------------------------------------------------------------
String.prototype.startsWith = function(prefix) {
    return this.indexOf(prefix) === 0;
}

String.prototype.endsWith = function(suffix) {
    return this.match(suffix+"$") == suffix;
};


// --- MecSim --------------------------------------------------------------------------------------------------------------------------------------------------

var MecSim = (function () {

    var s_instance;

    function _ctor() {return {

        getWebSocket : function( p_wspath ) {
            if ((p_wspath.startsWith("ws://")) || (p_wspath.startsWith("wss://")))
                return new WebSocket(p_wspath);

            return new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (p_wspath.startsWith("/") ? p_wspath : location.pathname + p_wspath ) );
        },

        getConfiguration : function ( p_data ) {
            var l_data = {};

            $.ajax({
                async: false,
                url : "/cconfiguration/get",
                success : function( p_data ) { l_data = p_data }
            });

            return l_data;
        }

    };};


    return {

        getInstance: function () {
            if ( !s_instance )
                s_instance = _ctor();

            return s_instance;
        }

  };

})();
