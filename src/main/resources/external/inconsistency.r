######################################################################################
# GPL License                                                                        #
#                                                                                    #
# This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
# Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
# This program is free software: you can redistribute it and/or modify               #
# it under the terms of the GNU General Public License as                            #
# published by the Free Software Foundation, either version 3 of the                 #
# License, or (at your option) any later version.                                    #
#                                                                                    #
# This program is distributed in the hope that it will be useful,                    #
# but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
# GNU General Public License for more details.                                       #
#                                                                                    #
# You should have received a copy of the GNU General Public License                  #
# along with this program. If not, see http://www.gnu.org/licenses/                  #
######################################################################################


# evaluation of inconsistency data
#
# @warning the R package of the database driver e.g. RMySL is needed and must installed manually
#
# @param databasedriver database driver
#
mecsim.inconsistency <- function(
    onlymyself=TRUE,
    configuration = file.path( Sys.getenv("HOME"), ".mecsim" )
){
    # --- check required packages ---
    # RJDBC (https://cran.r-project.org/web/packages/RJDBC/)
    # JsonLite (https://cran.r-project.org/web/packages/jsonlite/)
    # StarGazer (https://cran.r-project.org/web/packages/stargazer/)
    # GGplot2 (https://cran.r-project.org/web/packages/ggplot2/)
    # GridExtra (https://cran.r-project.org/web/packages/gridExtra)
    for ( i in c("RJDBC", "jsonlite", "stargazer", "ggplot2", "gridExtra") )
        if (!is.element( i, installed.packages()[,1] ) )
            install.packages(i)


    # --- read MecSim configuration (database settings) ---
    lo_config    <- jsonlite::fromJSON( file.path(configuration, "config.json"), flatten = TRUE );
    #print(lo_config$database)
    # --- try to load JDBC driver from Jar ---
    lo_connection <- NULL
    for ( i  in list.files( file.path(configuration, "jar"), pattern= ".jar", full.names = TRUE ) )
    {
        #print(i)
        lo_connection <-RJDBC::dbConnect( RJDBC::JDBC( lo_config$database$driver, i ), lo_config$database$url, lo_config$database$username, lo_config$database$password )

        #print( RJDBC::dbGetInfo(lo_driver) )
        #RJDBC::dbListConnections(lo_driver)

        #print( RJDBC::dbGetTables(lo_connection) )
        #print( lo_connection )
        #print("")
        #print("")

    }



    # --- read table data ---
    lc_tablename <- paste(lo_config$database$tableprefix, "inconsistency")

    lo_result <- NULL
    if (onlymyself)
        lo_result <- RJDBC::dbGetQuery( lo_connection, paste("select * from ", lc_tablename, " where instance=?"), lo_config$uuid )
    else
        lo_result <- RJDBC::dbGetQuery( lo_connection, paste("select * from ", lc_tablename) )


    print(lo_result)


    # --- close database connetion and clear result ---
    RJDBC::dbDisconnect(lo_connection);
}
