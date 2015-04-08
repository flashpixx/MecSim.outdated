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

    $( "#mecsim_start_button" ).click(function() {
        $.post(
            "csimulation/start"
        ).fail( function( p_data )
                {
                    console.log(p_data);
                    $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_start_error").dialog();
                } );
    });

    $( "#mecsim_stop_button" ).click(function() {
        $.post(
            "csimulation/stop"
        ).fail( function( p_data )
                {
                    console.log(p_data);
                    $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_stop_error").dialog();
                } );
    });

    $( "#mecsim_reset_button" ).click(function() {
        $.post(
            "csimulation/reset"
        );
    });


    $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });

    // activating menus
    $("#root_menu").menu();
    $("#simulation_menu").menu();
    $("#help_menu").menu();



});
