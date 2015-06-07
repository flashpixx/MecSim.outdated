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
