// --- STATISTICS PANEL --------------------------------------------------------------------------------------------

var mecsim_statistics,
    StatisticsPanel = {

        settings: {
        },

        init: function(){
            this.bind_ui_actions();
        },

        bind_ui_actions: function(){
            $("#ui-id-8").on("click", function(data){
                UI().getContent().empty();
                UI().getContent().load("template/clean.htm");
            });
        }

    };
