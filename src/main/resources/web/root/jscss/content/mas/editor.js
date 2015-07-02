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
 * ctor to create the source editor for the MAS
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASEditor( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
    var self = this;

    // object with arrays of agent names in the structure { name: -agentname-, config: -key of the configuration object- }
    this.mo_agents = {};
    // tab instances
    this.mo_tabs = null;
    // tab ID name
    this.mc_tabs = "tabs";


    // --- set the configuration for all agent access ----------------------------------------------------------------------------------------------------------
    this.mo_configuration = {};

    ["Jason"].forEach( function(pc_name) {

        self.mo_configuration[pc_name] = {};
        ["read", "write", "list", "create", "remove", "check"].forEach( function(pc_option) {
            self.mo_configuration[pc_name][pc_option] = ["/cagentenvironment", pc_name.toLowerCase(), pc_option].join("/");
        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}

/** inheritance call **/
MASEditor.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASEditor.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("content"),
        title     : ""
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overload
**/
MASEditor.prototype.getGlobalCSS = function()
{
    return  ".CodeMirror { border: 1px solid #eee; height: auto; }" + Pane.prototype.getGlobalCSS.call(this);
}


/**
 * @Overwrite
**/
MASEditor.prototype.getContent = function()
{
    return '<span id="' + this.generateSubID("agentlist") + '"></span>' +
    '<p><button id = "' + this.generateSubID("new") + '" ></button ></p>' +
    '<p><button id = "' + this.generateSubID("remove") + '" ></button ></p>' +
    '<p><button id = "' + this.generateSubID("check") + '" ></button ></p>' +
    Pane.prototype.getContent.call(this);
}


/**
 * returns a Json object with the selected agent data
 *
 * @return Json object
**/
MASEditor.prototype.getSelectedAgent = function()
{
    var lo_selected = jQuery( this.generateSubID("agentlist", "#") +" option:selected" );
    return {
        value : lo_selected.val(),
        group : lo_selected.closest("optgroup").attr("label")
    };
}

/**
 * adapted generator function for DOM IDs of the editor
 *
 * @param pc_id DOM ID name
 * @param pc_prefix prefix
 * @return ID
**/
MASEditor.prototype.generateSubIDElements = function(pc_id, pc_prefix)
{
    var lc = this.generateSubID(pc_id, pc_prefix);
    if (["agents"].indexOf(pc_id) > -1)
        lc += "-button";

    return lc;
}


/**
 * @Overwrite
**/
MASEditor.prototype.afterDOMAdded = function()
{
    var self = this;

    // bind reading action
    this.readAgents();

    // --- bind new-agent button action ------------------------------------------------------------------------------------------------------------------------
    jQuery( this.generateSubID("new", "#") ).button().click( function() {

        // set dialog content
        jQuery(self.generateSubID("content", "#")).empty().append(
            "<p>" +
            Layout.select({

                id      : self.generateSubID("agenttype"),
                label   : "Agent Type",
                options : Object.keys( self.mo_configuration )

            }) +
            "</p><p>" +
            Layout.input({

                id: self.generateSubID("agentname"),
                label : "Agent Name"

            }) +
            "</p>"
        );
        jQuery(self.generateSubID("agenttype", "#")).selectmenu();


        // open dialog
        jQuery(self.generateSubID("dialog", "#")).dialog({
            width   : "auto",
            modal   : true,
            buttons : {

                Create : function() {
                    self.createAgent( jQuery(self.generateSubID("agenttype", "#")).val(), jQuery(self.generateSubID("agentname", "#")).val() );
                    jQuery(this).dialog("close");
                },

                Cancel : function() { jQuery(this).dialog("close"); }

            }
        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- bind agent-remove button action ---------------------------------------------------------------------------------------------------------------------
    jQuery( this.generateSubID("remove", "#") ).button().click( function() {

        var lo = self.getSelectedAgent();

        // set dialog content
        jQuery( self.generateSubID("content", "#") ).empty().append( "<p>Should the [" + lo.group + "] agent [" + lo.value + "] be deleted?</p>" );

         // open dialog
        jQuery(self.generateSubID("dialog", "#")).dialog({
            width   : "auto",
            modal   : true,
            buttons : {

                Remove : function() {
                    self.removeAgent( lo.group, lo.value );
                    jQuery(this).dialog("close");
                },

                Cancel : function() { jQuery(this).dialog("close"); }

            }
        });


    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- bind agent-check button action ----------------------------------------------------------------------------------------------------------------------
    jQuery( this.generateSubID("check", "#") ).button().click( function() {

        var lo = self.getSelectedAgent();
        MecSim.ajax({
            url     : self.mo_configuration[lo.group].check,
            data    : { "name" : lo.value  },
        }).fail(function( po ) {

            console.log(po);

        }).done(function() {

            console.log("ok");

        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * read all agents from the REST-API
 * @see https://api.jquery.com/category/deferred-object/
 * @see https://api.jquery.com/jquery.when/
 * @note handle asynchron calls and create at the done call one array with all data
**/
MASEditor.prototype.readAgents = function()
{
    var self = this;

    // --- create Ajax calls for each agent-language-type ------------------------------------------------------------------------------------------------------
    var la_tasks = [];
    jQuery.each(this.mo_configuration, function( pc_configkey, po_config ) {

        var lo_task = new jQuery.Deferred();
        lo_task.done( function( po_data ) {
            po_data.config = pc_configkey;
        });

        MecSim.ajax({
            url     :  po_config.list,
            success : lo_task.resolve
        });

        la_tasks.push( lo_task.promise() );
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- collect results of all agent-language-types if all calls are finished -------------------------------------------------------------------------------
    jQuery.when.apply(jQuery, la_tasks).done(

        function()
        {
            self.mo_agents = {};

            // collapse data into on object
            Array.prototype.slice.call(arguments).forEach( function(px) {

                // on multiple Ajax call px is an array
                if ((Array.isArray(px)) && (px.length == 3) && (px[1] == "success"))
                {
                    if (!Array.isArray(self.mo_agents[ px[0].config ]))
                        self.mo_agents[ px[0].config ] = [];

                    Array.prototype.push.apply( self.mo_agents[ px[0].config ], px[0].agents );
                    return;
                }

                // on single Ajax call px is an object
                if ((px instanceof Object) && (px.agents) && (px.config))
                {
                    if (!Array.isArray(self.mo_agents[px.config]))
                        self.mo_agents[px.config] = [];

                    Array.prototype.push.apply( self.mo_agents[px.config], px.agents );
                    return;
                }
            });

            // clear div and add a new select box
            jQuery( self.generateSubID("agentlist", "#") ).empty();
            jQuery( Layout.selectgroup({ id: self.generateSubID("agents"),  label: "",  options: self.mo_agents  }) ).appendTo( self.generateSubID("agentlist", "#") );
            jQuery( self.generateSubID("agents", "#") ).selectmenu({
                change : function( po_event, po_ui ) {
                    self.addTabView();
                    self.addTab( po_ui.item.optgroup, po_ui.item.value );
                }
            });

            // set language element
            MecSim.language({ url : "/clanguageenvironment/maseditor", target : self, idgenerator : self.generateSubIDElements });
        }
    );
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * write the content or caches the data if the write fails
 *
 * @param pc_group option group name
 * @param pc_agent agent name
 * @param pc_content content of the agent
**/
MASEditor.prototype.writeAgent = function( pc_group, pc_agent, pc_content )
{
    MecSim.ajax({

        url     : this.mo_configuration[pc_group].write,
        data    : { "name" : pc_agent, "source" : pc_content }

    }).fail( function( po_data ) {

        // @todo cache on fail
        //console.log(po_data);

    });
}


/**
 * creates a new agent
 *
 * @param pc_group option group name
 * @param pc_agent agent name
**/
MASEditor.prototype.createAgent = function( pc_group, pc_agent )
{
    var self = this;

    MecSim.ajax({

        url     : this.mo_configuration[pc_group].create,
        data    : { "name" : pc_agent },
        success : function() { self.readAgents(); }

    }).fail( function( po_data ) {

        // @todo show error dialog
        //console.log(po_data);

    });
}


/**
 * removes a new agent
 *
 * @param pc_group option group name
 * @param pc_agent agent name
**/
MASEditor.prototype.removeAgent = function( pc_group, pc_agent )
{
    var self = this;

    MecSim.ajax({

        url     : this.mo_configuration[pc_group].remove,
        data    : { "name" : pc_agent },
        success : function() {

            self.readAgents();

            // remove an existing tab
            var lc_tabid = self.generateSubID( pc_group+"_"+pc_agent );
            jQuery( "#"+lc_tabid ).remove();
            jQuery( "#"+lc_tabid+"_nav" ).remove();

            self.mo_tabs.tabs( "refresh" );
            self.mo_tabs.tabs({ active: 0 });

        }

    }).fail( function( po_data ) {

        // @todo show error dialog
        //console.log(po_data);

    });
}

/**
 * returns a deep-copy of the filelist
 *
 * @return array with filelist objects
**/
MASEditor.prototype.getAgents = function()
{
    return jQuery.extend( {}, this.mo_agents );
}


/**
 * reads the file content and calls the result
 * function with the data
 *
 * @param pc_group option group name
 * @param pc_agent agent name
**/
MASEditor.prototype.addTab = function( pc_group, pc_agent  )
{
    if (!this.mo_tabs)
        return;

    // create DOM ID with check if tab existst
    var lc_tabid = this.generateSubID( pc_group+"_"+pc_agent );
    if( jQuery( "#"+lc_tabid ).length )
        return;


    // --- build the tab view ----------------------------------------------------------------------------------------------------------------------------------
    var self = this;
    MecSim.ajax({

        url     : this.mo_configuration[pc_group].read,
        data    : { "name" : pc_agent },
        success : function( po_data )
        {

            // create tab and editor instance
            self.mo_tabs.find( ".ui-tabs-nav" ).append(
                '<li id="' + lc_tabid + '_nav' + '">' +
                '<a href="#' + lc_tabid + '">'  + pc_agent+ ' (' + pc_group + ')' +
                '<span class="ui-icon ui-icon-close" role="presentation"/>' +
                '</a>' +
                '</li>'
            );
            self.mo_tabs.append(
                '<div id="' + lc_tabid + '">' +
                '<textarea id="' + lc_tabid+ '_edit">' + po_data.source + '</textarea>' +
                '</div>'
            );

            var lo_editor = CodeMirror.fromTextArea( document.getElementById( lc_tabid + "_edit" ), {

                lineNumbers    : true,
                viewportMargin : Infinity

            });

            // create editor bind action, on blur (focus lost) the data are written to the REST-API and the textarea
            lo_editor.on( "blur", function( po_editor ) { self.writeAgent( pc_group, pc_agent, po_editor.getValue() ); po_editor.save(); });

            // refresh editor and tab structure
            lo_editor.refresh();
            self.mo_tabs.tabs( "refresh" );

            // set active tab to the last inserted tab
            self.mo_tabs.tabs({ active: self.mo_tabs.find( ' .ui-state-default' ).size()-1 });
        }

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * creates the tab view in the content pane if not exists
**/
MASEditor.prototype.addTabView = function()
{
    if( jQuery( this.generateSubID(this.mc_tabs, "#") ).length )
        return;

    var self = this;

    // create tab div within the DOM
    jQuery( MecSim.ui().content("#") ).empty().append( '<div id="' + this.generateSubID(this.mc_tabs) + '"><ul></ul></div>' );
    this.mo_tabs = jQuery( this.generateSubID(this.mc_tabs, "#") ).tabs();

    // bind close action
    this.mo_tabs.delegate( "span.ui-icon-close", "click", function() {
        jQuery( "#" + jQuery( this ).closest( "li" ).remove().attr( "aria-controls" ) ).remove();
        self.mo_tabs.tabs( "refresh" );
    });
}
