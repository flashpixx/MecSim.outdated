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
