$(document).ready(function(){

    // http://www.html5rocks.com/en/tutorials/webcomponents/imports/?redirect_from_locale=de
    $("a.template").click(function( p_event ){
        p_event.preventDefault();
        $("#content").load( this.href );
    });

});
