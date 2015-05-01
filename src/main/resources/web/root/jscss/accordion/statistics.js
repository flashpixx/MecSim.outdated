// --- STATISTICS PANEL --------------------------------------------------------------------------------------------
function statisticsSlider(){
    $("#ui-id-8").on("click", function(data){
        UI().getContent().empty();
        UI().getContent().load("template/clean.htm");
    });
}
