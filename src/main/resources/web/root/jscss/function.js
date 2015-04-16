// --- global prototype functions ------------------------------------------------------------------------------------------------------------------------------
String.prototype.startsWith = function(prefix) {
    return this.indexOf(prefix) === 0;
}

String.prototype.endsWith = function(suffix) {
    return this.match(suffix+"$") == suffix;
};


// --- jQuery --------------------------------------------------------------------------------------------------------------------------------------------------
// @warn need jQuery & jQuery-UI
// @see http://learn.jquery.com/jquery-ui/widget-factory/why-use-the-widget-factory/
(function($){

	$.widget("mecsim.mecsim", {

        options: {
        },


        // ctor
        _create : function() {
        },


        // public member
        websocket : function( p_wspath ) {
            return new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (wspath.startsWith("/") ? p_wspath : location.pathname + p_wspath ) );
        },

        configuration : function ( p_data ) {
        }


        // private member
        //_myPrivateMethod: function( argument ) { }


    });

})(jQuery);



