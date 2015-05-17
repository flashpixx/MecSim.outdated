// --- HELP PANEL --------------------------------------------------------------------------------------------------

var mecsim_help,
    HelpPanel = {

        settings: {
            about_button: $("#mecsim_help_about").button(),
            user_doc_button: $("#mecsim_help_userdoku").button(),
            developer_doc_button: $("#mecsim_help_devdoku").button()
        },

        init: function() {
            mecsim_help = this.settings;
            this.bind_ui_actions();
        },

        bind_ui_actions: function() {

            $("#ui-id-12").on("click", function(data){
                MecSim.UI().content.empty();
            });

            HelpPanel.settings.about_button.on("click", function(){

                $.getJSON( "cconfiguration/get", function( p_data ) {
                    console.log(p_data);
                    $("#mecsim_project_name")
                            .attr("href", p_data.manifest["project-url"])
                            .text(p_data.manifest["project-name"]);

                    $("#mecsim_license")
                            .attr("href", p_data.manifest["license-url"])
                            .text(p_data.manifest["license"]);

                    $("#mecsim_buildversion").text(p_data.manifest["build-version"]);
                    //$("#mecsim_buildnumber").text(p_data.manifest["build-number"]);
                    //$("#mecsim_buildcommit").text(p_data.manifest["build-commit"]);

                }).done( function() {
                    $("#mecsim_about").dialog({
                        width: 500,
                        modal: true
                    });
                });

            });

            HelpPanel.settings.user_doc_button.on("click", function(){
                $.get("/userdoc/", function( p_result ) {
                    console.log(p_result);
                    MecSim.UI().content.empty();
                    MecSim.UI().content.append( p_result );
                });
            });

            HelpPanel.settings.developer_doc_button.on("click", function(){
                MecSim.UI().content.load("template/develdoc.htm");
            });

        }

    };
