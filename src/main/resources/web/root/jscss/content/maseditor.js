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
 * ctor to create the source editor for MAS files
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASEditor( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    // object with arrays of file objects in the structure { name: -filename-, config: -key of the configuration object- }
    this.mo_files = {};
    // tab instances
    this.mo_tabs = null;
    // tab ID name
    this.mc_tabs = "tabs";
    // object with hash and instances of the current editor
    //this.mo_instances = {};


    // set the configuration for all file access
    this.mo_configuration = {

        "Jason" : {
            reader : "/cagentenvironment/jason/read",
            writer : "/cagentenvironment/jason/write",
            list   : "/cagentenvironment/jason/list"
        }

    }

    // read files on initialisation
    this.readAgents();
}

/** inheritance call **/
MASEditor.prototype = Object.create(Pane.prototype);


/**
 * @Overload
**/
Simulation.prototype.getGlobalCSS = function()
{
    return  ".CodeMirror { border: 1px solid #eee; height: auto; }" + Pane.prototype.getGlobalCSS.call(this);
}


/**
 * @Overwrite
**/
MASEditor.prototype.getContent = function()
{
    return '<div id="' + this.generateSubID("files") + '"></div>' + Pane.prototype.getContent.call(this);
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

    // create Ajax calls
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


    // collect results if all calls are finished
    jQuery.when.apply(jQuery, la_tasks).done(

        function()
        {
            self.mo_files = {};

            // collapse data into on object
            Array.prototype.slice.call(arguments).forEach( function(px) {

                // on multiple Ajax call px is an array
                if ((Array.isArray(px)) && (px.length == 3) && (px[1] == "success"))
                {
                    if (!Array.isArray(self.mo_files[ px[0].config ]))
                        self.mo_files[ px[0].config ] = [];

                    Array.prototype.push.apply( self.mo_files[ px[0].config ], px[0].agents );
                    return;
                }

                // on single Ajax call px is an object
                if ((px instanceof Object) && (px.agents) && (px.config))
                {
                    if (!Array.isArray(self.mo_files[px.config]))
                        self.mo_files[px.config] = [];

                    Array.prototype.push.apply( self.mo_files[px.config], px.agents );
                    return;
                }
            });

            // clear div and add a new select box
            jQuery( self.generateSubID("files", "#") ).empty();
            jQuery( Layout.selectgroup({ id: self.generateSubID("agents"),  label: "Agents",  options: self.mo_files }) ).appendTo( self.generateSubID("files", "#") );
            jQuery( self.generateSubID("agents", "#") ).selectmenu({
                change : function( po_event, po_ui ) {
                    self.buildTabView();
                    self.addContentTab( po_ui.item.optgroup, po_ui.item.value );
                }
            });
        }
    );

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

        url     : this.mo_configuration[pc_group].writer,
        data    : { "name" : pc_agent, "source" : pc_content }

    }).fail( function( po_data ) {

        console.log(po_data);

    });
}


/**
 * returns a deep-copy of the filelist
 *
 * @return array with filelist objects
**/
MASEditor.prototype.getFiles = function()
{
    return jQuery.extend( {}, this.mo_files );
}


/**
 * reads the file content and calls the result
 * function with the data
 *
 * @param pc_group option group name
 * @param pc_agent agent name
**/
MASEditor.prototype.addContentTab = function( pc_group, pc_agent  )
{
    if (!this.mo_tabs)
        return;

    var self = this;
    MecSim.ajax({

        url     : this.mo_configuration[pc_group].reader,
        data    : { "name" : pc_agent },
        success : function( po_data )
        {
            // create DOM ID
            var lc_tabid = self.generateSubID( pc_group+"_"+pc_agent );

            // create tab and editor instance
            self.mo_tabs.find( ".ui-tabs-nav" ).append( '<li><a href="#' + lc_tabid + '">'  + pc_agent+ ' (' + pc_group + ')' +  '</a></li>' );
            self.mo_tabs.append( '<div id="' + lc_tabid + '"><textarea id="' + lc_tabid+ '_edit">' + po_data.source + '</textarea></div>' );

            var lo_editor = CodeMirror.fromTextArea( document.getElementById( lc_tabid + "_edit" ), {

                lineNumbers    : true,
                viewportMargin : Infinity,

            });

            // create editor bind action, on blur (focus lost) the data are written
            lo_editor.on( "blur", function( po_editor ) { self.writeAgent( pc_group, pc_agent, po_editor.getValue() ); });

            // refresh tab & editor
            lo_editor.refresh();
            self.mo_tabs.tabs( "refresh" );
        }

    });

}


/**
 * creates the tab view in the content pane if not exists
**/
MASEditor.prototype.buildTabView = function()
{
    if( jQuery( this.generateSubID(this.mc_tabs, "#") ).length )
        return;

    // create tab div within the DOM
    jQuery( MecSim.ui().content("#") ).empty();
    jQuery( MecSim.ui().content("#") ).append( '<div id="' + this.generateSubID(this.mc_tabs) + '"><ul></ul></div>' );
    this.mo_tabs = jQuery( this.generateSubID(this.mc_tabs, "#") ).tabs();
}
