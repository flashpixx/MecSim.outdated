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
**/
function Widget( pc_id, pc_name, pa_panel, options )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    //settings
    var self = this;

    var lo_options          = options || {};
    this._name              = lo_options.name                      || "Default Widgetname";
    this._resizeable        = !lo_options.resizeable  ? true : lo_options.resizeable;
    this._draggable         = !lo_options.draggable   ? true : lo_options.draggable;
    this._handle            = lo_options.handle                    || false;
    this._cancel            = lo_options.cancel                    || false;
    this._animate           = !lo_options.animate     ? true : lo_options.animate;
    this._animateEffect     = lo_options.animateEffect             || "drop";
    this._animationTime     = lo_options.animateTime               || 400;
    this._background        = lo_options.background                || "white";
    this._width             = lo_options.width                     || 750;
    this._height            = lo_options.height                    || 550;
    this._minWidth          = lo_options.minWidth                  || this._width;
    this._minHeight         = lo_options.minHeight                 || this._height;
    this._collapseWidth     = lo_options.collapseWidth             || 400;
    this._collapseHeight    = lo_options.collapseHeight            || 20;
    this._minOffset         = lo_options.minOffSet                 || 50;

    this._closedStatus = false;
    this._minimizedStatus = false;
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
           ["    width: ", this._width, ";"].join("") +
           ["    height: ", this._height, ";"].join("") +
           ["    background: ", this._background, ";"].join("") +
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
**/
Widget.prototype.getContent = function()
{
    return '<div id="' + this.generateSubID("widget") + '">' +
           '<h3 class="' + this.generateSubID("header") + '">' +
           '<span>' + this._name + '</span>' +
           '<span class = "' + this.generateSubID("button") + '">' +
           '<button id="' + this.generateSubID("collapsebutton") + '"></button>' +
           '<button id="' + this.generateSubID("closebutton") + '"></button>' +
           '</span>' +
           '</div>';
}


/**
 * @Overwrite
**/
Widget.prototype.afterDOMAdded = function()
{
    jQuery( this.generateSubID("collapsebutton", "#") ).button( { icons : { primary: "ui-icon-newwin"},     text: false }).click( this.collapse );
    jQuery( this.generateSubID("closebutton", "#") ).button(    { icons : { primary: "ui-icon-closethick"}, text: false }).click( this.close );

    //create widget
    if (this._draggable)
        jQuery( this.generateSubID("widget", "#") ).draggable({
            cancel: self._cancel + ", input,textarea,button,select,option",
            drag: function(event, ui) {
                ui.position.top = Math.max( -1*(self._minHeight - self._minOffset), ui.position.top );
                ui.position.left = Math.max( -1*(self._minWidth - self._minOffset), ui.position.left );
        }});

    if (this._resizeable)
        jQuery( this.generateSubID("widget", "#") ).resizable({
            animate: this._animate,
            minWidth: this._minWidth,
            minHeight: this._minHeight
        });
}


//method to collapse widget
Widget.prototype.collapse = function(){
    var self = this;
    if(this._minimizedStatus){
        this._div.animate({width: this._minWidth+"px", height: this._minHeight+"px"}, this._animateEffect, function(){
            self._div.children().not(this.generateSubID("header", ".")).show();
            self._div.resizable('enable');
        });
    }else{
        this._div.resizable('disable');
        this._div.children().not(this.generateSubID("header", ".")).hide();
        this._div.animate({width: this._collapseWidth+"px", height: this._collapseHeight+"px"}, this._animationTime);
    }
    this._minimizedStatus = !this._minimizedStatus;
};

//method to close widget
Widget.prototype.close = function()
{
    if(this._closedStatus)
        this._div.show(this._animateEffect, this._animationTime);
    else
        this._div.hide(this._animateEffect, this._animationTime);

    this._closedStatus = !this._closedStatus;
};

