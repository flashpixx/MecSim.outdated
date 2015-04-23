// --- HELP PANEL --------------------------------------------------------------------------------------------------
function helpSlider(){

    $("#ui-id-10").on("click", function(data){
        UI().getContent().empty();
    });

    $("#mecsim_help_about").button().on("click", function(){
        $.getJSON( "cconfiguration/get", function( p_data ) {

            $("#mecsim_project_name")
                    .attr("href", p_data.manifest["project-url"])
                    .text(p_data.manifest["project-name"]);

            $("#mecsim_licence")
                    .attr("href", p_data.manifest["licence-url"])
                    .text(p_data.manifest["licence"]);

            $("#mecsim_buildversion").text(p_data.manifest["build-version"]);
            $("#mecsim_buildnumber").text(p_data.manifest["build-number"]);
            $("#mecsim_buildcommit").text(p_data.manifest["build-commit"]);

            $("#mecsim_about").dialog({
                width: 500
            });
        });
    });

    $("#mecsim_help_userdoku").button().on("click", function(){
        $.get("/userdoc/", function( p_result ) {
            console.log(p_result);
            UI().getContent().empty();
            UI().getContent().append( p_result );
        });
    });

    $("#mecsim_help_devdoku").button().on("click", function(){
        UI().getContent().load("template/develdoc.htm");
    });
}