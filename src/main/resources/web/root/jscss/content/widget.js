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
 * ctor to create the configuration view
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
 * @param po_options configuration options
**/
function Widget( pc_id, pc_name, pa_panel, po_options )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    var lo_options            = po_options || {};
    this.ml_resizeable        = !lo_options.resizeable  ? true : lo_options.resizeable;
    this.ml_draggable         = !lo_options.draggable   ? true : lo_options.draggable;
    this.ml_cancel            = lo_options.cancel                    || false;
    this.ml_animate           = !lo_options.animate     ? true : lo_options.animate;
    this.mc_animateEffect     = lo_options.animateeffect             || "drop";
    this.mn_animationTime     = lo_options.animatetime               || 400;
    this.mc_background        = lo_options.background                || "white";
    this.mc_sizeunit          = lo_options.mc_sizeunit               || "px";
    this.mn_width             = lo_options.width                     || 750;
    this.mn_height            = lo_options.height                    || 550;
    this.mn_minWidth          = lo_options.minWidth                  || this.mn_width;
    this.mn_minHeight         = lo_options.minHeight                 || this.mn_height;
    this.mn_collapseWidth     = lo_options.collapsewidth             || 400;
    this.mn_collapseHeight    = lo_options.collapseheight            || 20;

    this.ml_closedStatus      = false;
    this.ml_minimizedStatus   = false;
}

/** inheritance call **/
Widget.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Widget.prototype.getCSS = function()
{
    return '.ui-resizable-helper{ border: 1px dotted gray; }' +

           this.generateSubID("default", ".") +
           '{' +
           '    position: absolute;' +
           '    padding: 5px;' +
           '    margin: 5px;' +
           '    border: 1px solid #CACACA;' +
           '    overflow: hidden;' +
           ["    width: ",  this.mn_width,  this.mc_sizeunit, ";"].join("") +
           ["    height: ", this.mn_height, this.mc_sizeunit, ";"].join("") +
           ["    background: ", this.mc_background, ";"].join("") +
           '}' +

           this.generateSubID("header", ".") +
           '{' +
           '    margin: 0;' +
           '    color: white;' +
           '    background: #008C4F;' +
           '    border: 1px solid #CACACA;' +
           '    position: relative;' +
           '    text-align: center;' +
           '    margin-bottom: 10px;' +
           '}' +

           this.generateSubID("button", ".") +
           '{' +
           '    position: absolute;' +
           '    right: 0;' +
           '}';
}


/**
 * @Overwrite
 *
 * @param pc_content optional content
**/
Widget.prototype.getContent = function(pc_content)
{
    return '<div id="' + this.generateSubID("widget") + '">' +
           '<h3 class="' + this.generateSubID("header") + '">' +
           '<span>' + this.mc_name + '</span>' +
           '<span class = "' + this.generateSubID("button") + '">' +
           '<button id="' + this.generateSubID("collapsebutton") + '"></button>' +
           '<button id="' + this.generateSubID("closebutton") + '"></button>' +
           '</span>' +
           (pc_content ? pc_content : "") +
           '</div>';
}


/**
 * @Overwrite
**/
Widget.prototype.afterDOMAdded = function()
{
    // create window buttons with action bind
    jQuery( this.generateSubID("collapsebutton", "#") ).button( { icons : { primary: "ui-icon-newwin"},     text: false }).click( this.collapse );
    jQuery( this.generateSubID("closebutton", "#") ).button(    { icons : { primary: "ui-icon-closethick"}, text: false }).click( this.close );

    // resize / collcapse / dragable action bind
    if (this.ml_draggable)
        jQuery( this.generateSubID("widget", "#") ).draggable({
            cancel: self.ml_cancel + ", input,textarea,button,select,option",
            drag: function(event, ui) {
                ui.position.top = Math.max(  -1*(self.mn_minHeight - self._minOffset), ui.position.top );
                ui.position.left = Math.max( -1*(self.mn_minWidth  - self._minOffset), ui.position.left );
        }});

    if (this.ml_resizeable)
        jQuery( this.generateSubID("widget", "#") ).resizable({
            animate   : this.ml_animate,
            minWidth  : this.mn_minWidth,
            minHeight : this.mn_minHeight
        });
}


/**
 * collapse action
**/
Widget.prototype.collapse = function(){
    var self = this;
    if(this.ml_minimizedStatus)

        jQuery( this.generateSubID("widget", "#") ).animate(
            {
                width  : this.mn_minWidth  + this.mc_sizeunit,
                height : this.mn_minHeight + this.mc_sizeunit
            },
            this.mc_animateEffect,
            function(){
                self._div.children().not(this.generateSubID("header", ".")).show();
                self._div.resizable('enable');
            }
        );

    else
    {
        this._div.resizable('disable');
        this._div.children().not(this.generateSubID("header", ".")).hide();
        this._div.animate(
            {
                width  : this.mn_collapseWidth  + this.mc_sizeunit,
                height : this.mn_collapseHeight + this.mc_sizeunit
            },
            this.mn_animationTime
        );
    }

    this.ml_minimizedStatus = !this.ml_minimizedStatus;
};

/**
 * close action
**/
Widget.prototype.close = function()
{
    if(this.ml_closedStatus)
        this._div.show(this.mc_animateEffect, this.mn_animationTime);
    else
        this._div.hide(this.mc_animateEffect, this.mn_animationTime);

    this.ml_closedStatus = !this.ml_closedStatus;
};

