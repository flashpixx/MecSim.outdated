$(document).ready(function(){

    /**
     * call to set template links
     * @see http://www.html5rocks.com/en/tutorials/webcomponents/imports/?redirect_from_locale=de
    **/
    $("a.template").button().click(function( p_event ){
        p_event.preventDefault();
        $("#mecsim_content").load( this.href );
    });

    $( "#mecsim_accordion" ).accordion();


    $( "#mecsim_start_button" ).button().click(function() {
        $.post(
            "csimulation/start"
        );
    });

    $( "#mecsim_stop_button" ).button().click(function() {
        $.post(
            "csimulation/stop"
        );
    });

    $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });


});
