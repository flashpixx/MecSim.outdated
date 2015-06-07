"use strict";

// @todo http://tobyho.com/2010/11/22/javascript-constructors-and/
// @todo http://stackoverflow.com/questions/4012998/what-it-the-significance-of-the-javascript-constructor-property
// @todo http://javascriptissexy.com/javascript-prototype-in-plain-detailed-language/
// @todo http://raganwald.com/2014/07/09/javascript-constructor-problem.html

/**
 * ctor of a UI pane
 *
 * @param pc_id internal name of the id
**/
function Pane( pc_id )
{
    this.id = pc_id.replace(/[^a-z0-9]+|\s+/gmi, "").toLowerCase();
}


/**
 * returns the internal ID
 *
 * @return id
**/
Pane.prototype.getID = function()
{
    return this.id;
}

/**
 * returns a subID of the current pane
 *
 * @param pc_subid name of the sub structure
 * @return subID
**/
Pane.prototype.generateSubID = function( pc_subid )
{
    return ["mecsim", this.getID, pc_subid.replace(/[^a-z0-9]+|\s+/gmi, "").toLowerCase()].join("_");
}



Pane.prototype.getGlobalContent = function()
{
}

Pane.prototype.getGlobalCSS = function()
{
}



Pane.prototype.beforeShowMainContent = function()
{
}

Pane.prototype.beforeHideMainContent = function()
{
}

Pane.prototype.getMainContent = function()
{
}

Pane.prototype.getMainCSS     = function()
{
}



Pane.prototype.beforeOpenMenu = function()
{
}

Pane.prototype.getMenuName = function()
{
}

Pane.prototype.getMenuHeader = function()
{
}

Pane.prototype.getMenuContent = function()
{
}

Pane.prototype.getMenuCSS     = function()
{
}
