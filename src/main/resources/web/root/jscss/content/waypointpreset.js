/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */


"use strict";

//@todo fix HTML5 data attributes


/**
 * ctor to create the preset wizard
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
 * @param po_options configuration options
**/
function WaypointPreset( pc_id, pc_name, pa_panel, po_options )
{
    var lo_options    = jQuery.extend(true, {}, po_options);
    lo_options.cancel = true;
    Wizard.call(this, pc_id, pc_name, pa_panel, lo_options );

    // list with IDs
    this.mo_elements = {
        selects   : [],
        texts     : [],
        sliders   : [],
        spinners  : []
    };

    // reference of colorpicker (for closing after finish) and agent menu for refreshing
    this.mo_colorpicker = null;
    this.mo_agentmenu   = null
}

/** inheritance call **/
WaypointPreset.prototype = Object.create(Wizard.prototype);


/**
 * @Overwrite
**/
WaypointPreset.prototype.getContent = function()
{
    // add manual error dialog
    jQuery(Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("text"),
        title     : "Information"
    })).appendTo("body");

    // add wizard content
    return Wizard.prototype.getContent.call( this,

        // step - general data
        '<h3 id="' + this.generateSubID("factoryhead") + '" />' +
        '<section><div id="' + this.generateSubID("factorysettings") + '">' +

        '<h4 id="' + this.generateSubID("factorysettingshead") + '" />' +
        '<div>' +
        '<p>' + Layout.select(  { id: this.generateSubID("factory"),  label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("agent"),    label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("waypointsettingshead") + '" />' +
        '<div>' +
        '<p>' + Layout.select(  { id: this.generateSubID("waypoint"), label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '<p>' + Layout.input(   { id: this.generateSubID("radius"),   label: " ",   list: this.mo_elements.texts })   + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("generatorsettingshead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("generator"),                                                                   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("carcount"),                                                                    label: " ",    list: this.mo_elements.spinners }) + '</p>' +
        '<p>' + Layout.select( { id: this.generateSubID("generatordistribution"),       class: this.generateSubID("distribution"),      label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("generatordistributionfirstmomentum"),                                          label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("generatordistributionsecondmomentum"),                                         label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '</div></section >' +


        // step - car settings
        '<h3 id="' + this.generateSubID("carhead") + '" />' +
        '<section><div id="' + this.generateSubID("carsettings") + '">' +

        '<h4 id="' + this.generateSubID("accelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("accelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionfirstmomentum"),                                            label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionsecondmomentum"),                                           label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("decelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("decelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionfirstmomentum"),                                            label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionsecondmomentum"),                                           label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("lingerhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("lingerdistribution"),                  class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("lingerdistributionfirstmomentum"),                                                  label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("lingerdistributionsecondmomentum"),                                                 label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("maxspeedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("maxspeeddistribution"),                class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeeddistributionfirstmomentum"),                                                label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeeddistributionsecondmomentum"),                                               label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("speedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speedfactor"),                                                                      label: " ",   list: this.mo_elements.spinners })    + '</p>' +
        '</div>' +

        '</div></section >' +


        // step - customizing settings
        '<h3 id="' + this.generateSubID("customhead") + '" />' +
        '<section>' +
        '<p>' + Layout.input(  { id: this.generateSubID("name"),    label: " ",   list: this.mo_elements.texts }) + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("color"),   label: " " })                                 + '</p>' +
        '<p id="' + this.generateSubID("error") + '"></p>' +
        '</section >'
    );
}


/**
 * sets the labels of an select menu
 *
 * @param pc_id ID of the select menu
 * @param px_option data bound options
**/
WaypointPreset.prototype.setLabelOfDistribution = function( pc_id, px_option )
{
    var lo_bound = jQuery(px_option).data("bound");

    // show / hide call
    ["firstmomentum", "secondmomentum"].forEach( function(pc_label) {

        // set label
        jQuery( 'label[for="' + pc_id + pc_label + '"]' ).text(lo_bound[pc_label].label)

        // hide / show momentums
        if (lo_bound[pc_label].used)
        {
            jQuery( 'label[for="' + pc_id + pc_label + '"]' ).show();
            jQuery( '#' + pc_id + pc_label ).show();
        } else {
            jQuery( 'label[for="' + pc_id + pc_label + '"]' ).hide();
            jQuery( '#' + pc_id + pc_label ).hide();
        }

    });
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.afterDOMAdded = function()
{
    var self = this;
    MecSim.language({

        url : "/cwaypointenvironment/label",

        // set static labels
        each : function( pc_key, pc_value ) {
            var la = pc_key.split("_");

            if ((la.length == 1) && (la[0] === "name"))
                self.setTitle( pc_value );

            if (la.length != 2)
                return;
            if (la[0] === "id")
                jQuery( self.generateSubID(la[1], "#") ).text(pc_value);
            if (la[0] === "label")
                jQuery( 'label[for="' + self.generateSubID(la[1]) + '"]' ).text(pc_value);
        },

        // set dynamic content, action binds and layout after finishing language labels
        finish : function() {

            Wizard.prototype.afterDOMAdded.call(self);

            // --- initialize layout ---

            // set accordion for wizard steps
            jQuery( self.generateSubID("carsettings", "#") ).accordion({ header: "h4", collapsible: true, heightStyle: "content", active: false });
            jQuery( self.generateSubID("generatorsettings", "#") ).accordion({ header: "h4", collapsible: true, heightStyle: "content", active: false });
            jQuery( self.generateSubID("factorysettings", "#") ).accordion({ header: "h4", collapsible: true, heightStyle: "content", active: false });

            // set color picker
            self.mo_colorpicker = jQuery( self.generateSubID("color", "#") ).spectrum({
                showPaletteOnly: true,
                togglePaletteOnly: true,
                togglePaletteMoreText: 'more',
                togglePaletteLessText: 'less',
                color: '#008C4F',
                palette: [
                    ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
                    ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
                    ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"]
                ]
            });

            // set distribution binds
            jQuery(self.generateSubID("distribution", ".")).on('selectmenuchange', function( po_event, po_ui ) { self.setLabelOfDistribution(po_event.target.id, po_ui.item.element); });

            // set jQuery element definition
            self.mo_elements.selects.forEach(  function( pc_id ) { jQuery( "#"+pc_id ).selectmenu({ width: 150 }); });
            self.mo_elements.spinners.forEach( function( pc_id ) { jQuery( "#"+pc_id ).spinner() });
            self.mo_elements.texts.forEach(    function( pc_id ) { jQuery( "#"+pc_id ).jqxInput({ height: 25, width: 150 }); });

            self.mo_agentmenu = jQuery( self.generateSubID("agent") );
            self.reset();
        }
    });
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.finish = function()
{
    // collect wizard options and build Json structure
    var self = this;
    var lo   = { distribution : {} };
    [
      { id: "color",        isnumber : false},
      { id: "name",         isnumber : false},
      { id: "speedfactor",  isnumber : true},
      { id: "carcount",     isnumber : true},
      { id: "generator",    isnumber : false},
      { id: "radius",       isnumber : true},
      { id: "factory",      isnumber : false},
      { id: "agent",        isnumber : false},
      { id: "waypoint",     isnumber : false}
    ].forEach( function( po_object ) {
        lo[po_object.id] = po_object.isnumber ? parseFloat(jQuery(self.generateSubID(po_object.id, "#")).val()) : jQuery(self.generateSubID(po_object.id, "#")).val();
    });

    [ "generatordistribution", "maxspeeddistribution", "accelerationdistribution", "decelerationdistribution", "lingerdistribution" ].forEach( function( pc_key ) {
        lo.distribution[pc_key.replace("distribution", "")] = {
            distribution   : jQuery(self.generateSubID(pc_key, "#")).val(),
            firstmomentum  : parseFloat(jQuery(self.generateSubID(pc_key+"firstmomentum", "#")).val()),
            secondmomentum : parseFloat(jQuery(self.generateSubID(pc_key+"secondmomentum", "#")).val())
        };
    });

    // replace color-text to an object
    var la_color = lo.color.replace(/rgb|\)|\(/g, "").split(",");
    lo.color = { red : parseInt(la_color[0]), green : parseInt(la_color[1]), blue : parseInt(la_color[2]) };

    // replace agent to detect
    lo.agent = {
        agent : jQuery(this.generateSubID("agent", "#")).val(),
        type  : jQuery(this.generateSubID("agent", "#")+ " :selected").closest("optgroup").attr("label")
    };

    // scale speed factor
    lo.speedfactor = lo.speedfactor/100;


    // send Ajax request for creating preset
    MecSim.ajax({

        url     : "/cwaypointenvironment/set",
        data    : lo,
        success : function()
        {
            // store configuration persistent
            var lo_store = {ui : {web : {waypointpreset : {} } } };
            lo_store.ui.web.waypointpreset[lo.name] = lo;

            // @todo add persistent storage

            self.mo_colorpicker.hide();
            self.hide();
        }

    }).fail( function(po_data) {

        jQuery( self.generateSubID("text", "#")   ).text(po_data.responseJSON.error);
        jQuery( self.generateSubID("dialog", "#") ).dialog();

    });
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.validatestep = function( po_event , pn_current, pn_next )
{
    // call for previous step is always okay
    if (pn_current > pn_next)
        return true;

    var self = this;

    /**
     * function for distribution check
     * @param pc distribution name
     * @return bool if values are okay
    **/
    var lx_checkDistribution = function( pc )
    {
        var ln_first  = parseFloat(jQuery( self.generateSubID(pc+"firstmomentum", "#") ).val());
        var ln_second = parseFloat(jQuery( self.generateSubID(pc+"secondmomentum", "#") ).val());

        switch (jQuery( self.generateSubID(pc, "#") ).val())
        {

            case "Uniform" :
                return (!isNaN(ln_first)) && (!isNaN(ln_second)) && (ln_first < ln_second) && (ln_first >= 0) && (ln_first != ln_second);

            case "Normal" :
                return (!isNaN(ln_first)) && (!isNaN(ln_second)) && (ln_first - 6*ln_second > 0) && (ln_first > 0);

            case "Exponential" :
                return (!isNaN(ln_first)) && (ln_first > 0);

        }

        return false;
    }

    /**
     * sets the CSS error element
     * @param pc_select DOM selector
     * @return pl_error true / false for set error
    **/
    var lx_setErrorLabelCSS = function( pc_selector, pl_error )
    {
        jQuery( 'label[for="' + pc_selector + '"]').css( "background-color", pl_error ? "#ff0000" : "" );
    }


    // check wizard steps
    switch (pn_current)
    {

        // first step
        case 0 :

            var ln = null;

            // check random waypoint & radius
            if ( jQuery(this.generateSubID("waypoint", "#")).val() == "CarWaypointRandom")
            {
                lx_setErrorLabelCSS( this.generateSubID("radius") );
                ln = parseFloat( jQuery(this.generateSubID("radius", "#")).val() );
                if ( ((isNaN(ln))) || (ln <= 0) )
                {
                    lx_setErrorLabelCSS( this.generateSubID("radius"), true );
                    return false;
                }
            }

            // check car count
            lx_setErrorLabelCSS( this.generateSubID("carcount") );
            ln = parseInt( jQuery(this.generateSubID("carcount", "#")).val() );
            if ( ((isNaN(ln))) || (ln <= 0) )
            {
                lx_setErrorLabelCSS( this.generateSubID("carcount"), true );
                return false;
            }

            // check generator distribution
            lx_setErrorLabelCSS( this.generateSubID("generatordistribution") );
            if (!lx_checkDistribution("generatordistribution"))
            {
                lx_setErrorLabelCSS( this.generateSubID("generatordistribution"), true );
                return false;
            }

            // agent need not to be checked, because the value is also set by default
            return true;


        // second step
        case 1 :

            var ln = null;

            // check max speed distribution
            [ "accelerationdistribution", "decelerationdistribution", "lingerdistribution", "maxspeeddistribution" ].forEach( function( pc_key ) {
                lx_setErrorLabelCSS( self.generateSubID(pc_key) );
                if (!lx_checkDistribution(pc_key))
                {
                    lx_setErrorLabelCSS( self.generateSubID(pc_key), true );
                    return false;
                }
            });

            // check speed factor
            lx_setErrorLabelCSS( this.generateSubID("speedfactor") );
            ln = parseInt( jQuery(this.generateSubID("speedfactor", "#")).val() );
            if ( ((isNaN(ln))) || (ln < 0) || (ln > 100) )
            {
                lx_setErrorLabelCSS( this.generateSubID("speedfactor"), true );;
                return false;
            }

            return true;

    }

    return false;
}


/**
 * resets the wizard form data to the default values
**/
WaypointPreset.prototype.reset = function()
{
    var self       = this;
    var lx_setData = function( pc_url, px_success ) { MecSim.ajax({ url : pc_url, success : px_success }); }

    // --- data content read via ajax ---

    // read distribution list
    lx_setData( "/cwaypointenvironment/listdistribution", function( po_data )
    {
        jQuery(self.generateSubID("distribution", ".")).empty();
        jQuery.each( po_data, function( pc_key, po_object ) {
            jQuery("<option></option>")
                .attr("value", po_object.id)
                .attr("data-bound", JSON.stringify({firstmomentum : po_object.firstmomentum, secondmomentum : po_object.secondmomentum}))
                .text(pc_key)
                .appendTo(self.generateSubID("distribution", "."));
        });

        // set first item selected and update menu
        jQuery(self.generateSubID("distribution", ".")+" option:first-child").attr("selected", "selected").each( function() { self.setLabelOfDistribution( jQuery(this).parent().attr("id"), jQuery(this) ); });
        jQuery(self.generateSubID("distribution", ".")).selectmenu("refresh");
    });


    // read factory list
    lx_setData( "/cwaypointenvironment/listfactory", function( po_data )
    {
        jQuery(self.generateSubID("factory", "#")).empty();
        jQuery.each( po_data, function( pc_key, po_object ) {
            jQuery("<option></option>")
                .attr("value", po_object.id)
                .attr("data-requireagent", po_object.requireagent)
                .text(pc_key)
                .appendTo(self.generateSubID("factory", "#"));
        });

        // set first item selected and update menu
        jQuery(self.generateSubID("factory", "#")+" option:first-child").attr("selected", "selected");
        jQuery(self.generateSubID("factory", "#")).selectmenu("refresh");
    });

    // read waypoint types
    lx_setData( "/cwaypointenvironment/listwaypoint", function( po_data )
    {
        jQuery(self.generateSubID("waypoint", "#")).empty();
        jQuery.each( po_data, function( pc_key, pc_id ) {
            jQuery("<option></option>")
                .attr("value", pc_id)
                .text(pc_key)
                .appendTo(self.generateSubID("waypoint", "#"));
        });

        // set first item selected and update menu
        jQuery(self.generateSubID("waypoint", "#")+" option:first-child").attr("selected", "selected");
        jQuery(self.generateSubID("waypoint", "#")).selectmenu("refresh");
    });

    // read generator types
    lx_setData( "/cwaypointenvironment/listgenerator", function( po_data ) {
        jQuery(self.generateSubID("generator", "#")).empty();
        jQuery.each( po_data, function( pc_key, pc_id ) {
            jQuery("<option></option>")
                .attr("value", pc_id)
                .text(pc_key)
                .appendTo(self.generateSubID("generator", "#"));
        });

        // set first item selected and update menu
        jQuery(self.generateSubID("generator", "#")+" option:first-child").attr("selected", "selected");
        jQuery(self.generateSubID("generator", "#")).selectmenu("refresh");
    });

    // update agent list and set the first one on default
    jQuery( this.generateSubID("agent", "#") ).empty().append( Layout.opentiongroup( this.getElements().mecsim_mas_editor.getAgents() ) );
    jQuery( this.generateSubID("agent", "#") +" option:first-child").attr("selected", "selected");
    this.mo_agentmenu.selectmenu("refresh");

    // set default values - distribution defaults are set in the Ajax call
    jQuery( self.generateSubID("name", "#") ).val("-Name-");
    jQuery( self.generateSubID("radius", "#") ).val(0.5);
    jQuery( self.generateSubID("carcount", "#") ).val(3);
    jQuery( self.generateSubID("speedfactor", "#") ).val(95);
    jQuery( self.generateSubID("color", "#") ).val("rgb(0,140,49)");

    // set defaults of distributions

}


/**
 * @Overwrite
**/
WaypointPreset.prototype.cancel = function()
{
    this.hide();
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.show = function()
{
    this.reset();
    Wizard.prototype.show.call(this);
}