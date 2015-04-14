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
        websocket : function( pc_relativepath ) {
            return new WebSocket( ((window.location.protocol === "https:") ? "wss://" : "ws://") + window.location.hostname + (((window.location.port != 80) && (window.location.port != 443)) ? ":" + window.location.port : "") + window.location.pathname + pc_relativepath );
        },


        // private member
        //_myPrivateMethod: function( argument ) { }


    });

})(jQuery);
