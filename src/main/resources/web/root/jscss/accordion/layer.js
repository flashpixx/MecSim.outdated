// --- LAYER PANEL --------------------------------------------------------------------------------------------

var mecsim_layer,
    LayerPanel = {

        settings: {
            clickableUI: $("#mecsim_layer_clickableUI"),
            switchdiv: $("#mecsim_layer_switchUI")
        },

        init: function() {
            mecsim_layer = this.settings;
            this.bind_ui_actions();
            this.list_clickable_layer();
            this.list_static_layer();
        },

        bind_ui_actions: function() {
            $("#ui-id-3").on("click", function(data){
                UI().getContent().empty();
                UI().getContent().load("template/clean.htm");
            });
        },

        list_clickable_layer: function() {
            $.ajax({
                url     : "/cosmviewer/listclickablelayer",
                success : function(px_data){
                    $.each( px_data, function(pc_key, px_value){
                        LayerPanel.settings.clickableUI.append("<li class='ui-state-default' id="+ px_value.id +">" + pc_key + "</li>" );
                    });

                    LayerPanel.settings.clickableUI.sortable({
                        placeholder: "ui-state-highlight",
                        stop: function(px_event, po_ui) {
                            $.ajax({
                                type: "POST",
                                url : "/cosmviewer/setclickablelayer",
                                data: {"id": $(this).children(".ui-sortable-handle:first").attr("id")}
                            });
                        }
                    });
                    LayerPanel.settings.clickableUI.disableSelection();
                }
            });
        },

        //TODO fix: undo if action failed
        list_static_layer: function() {
            //get layer list and create html strucutre
            $.ajax({
                url     : "/csimulation/listlayer",
                success : function( px_data ){
                    $.each( px_data, function( pc_key, px_value ) {
                        $("<label id='mecsim_simulation_switchlabel'>"+ pc_key +"</label>").appendTo(LayerPanel.settings.switchdiv);
                        $("<input class='mecsim_simulaton_switchActiv' type='checkbox' id='"+ px_value.id +"' checked>").appendTo(LayerPanel.settings.switchdiv);
                        if(px_value.isviewable)
                            $("<input class='mecsim_simulaton_switchVisibil' type='checkbox' id='"+ px_value.id +"' checked></p>").appendTo(LayerPanel.settings.switchdiv);
                    });
                }
            }).done(function(){

                //create active switches and listen
                $(".mecsim_simulaton_switchActiv").bootstrapSwitch({
                    size: "mini",
                    onText: "active",
                    offText: "inactive",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "csimulation/disableenablelayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail(function(p_data){
                            $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                            $("#mecsim_stop_error").dialog();
                        });
                    }
                });

                //create visibility switches and listen
                $(".mecsim_simulaton_switchVisibil").bootstrapSwitch({
                    size: "mini",
                    onText: "visible",
                    offText: "invisible",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "csimulation/hideshowlayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail(function(p_data){
                            $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                            $("#mecsim_stop_error").dialog();
                        });
                    }
                });

            });
        }

    };
