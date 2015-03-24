$(document).ready(function(){

    /**
     * call to set template links
     * @see http://www.html5rocks.com/en/tutorials/webcomponents/imports/?redirect_from_locale=de
    **/
    $("a.template").click(function( p_event ){
        p_event.preventDefault();
        $("#content").load( this.href );
    });


    $( "#mecsim_accordion" ).accordion();


    $( "#mecsim_start_button" ).click(function() {
        $(this).css('color','red');

        $.post(
            "simulation/csimulation/start"
        );
    });


    $( "#mecsim_stop_button" ).click(function() {
        $(this).css('color','red');
    });


    $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });

});
