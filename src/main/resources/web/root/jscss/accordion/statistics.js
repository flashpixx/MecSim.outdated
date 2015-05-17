// --- STATISTICS PANEL --------------------------------------------------------------------------------------------

var mecsim_statistics,
    StatisticsPanel = {

        settings: {
        },

        init: function(){
            this.bind_ui_actions();
        },

        bind_ui_actions: function(){
            $("#ui-id-10").on("click", function(data){
                MecSim.ui().content().empty();
                MecSim.ui().content().load("template/clean.htm");
            });
        }

    };
