$(document).ready(function(){

    // TODO: add button text according to selected language
    /*$('#mecsim_user_documentation').text(function() {
        $.post(
            ""
        );
    });*/

    /**
     * call to set template links
     * @see http://www.html5rocks.com/en/tutorials/webcomponents/imports/?redirect_from_locale=de
    **/

    $("a.template").click(function( p_event ){
        p_event.preventDefault();
        $("#mecsim_content").load( this.href );
    });

    // reload default page
    $("a.mecsim_home").button().click(function( p_event ){
        p_event.preventDefault();
        location.reload();
    });

    $( "#mecsim_accordion" ).accordion({
        active: false,
        collapsible: true
    });

    $( "#mecsim_start_button" ).button().click(function() {
        $.post(
            "csimulation/start"
        ).fail( function( p_data ) { console.log(p_data); } );
    });

    $( "#mecsim_stop_button" ).button().click(function() {
        $.post(
            "csimulation/stop"
        ).fail( function( p_data ) { console.log(p_data); } );
    });

    $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });

    $("#root_menu").menu();


});
