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
mecsim.inconsistency <- function(

){
    # --- check required packages ---
    # SQLDF (https://cran.r-project.org/web/packages/sqldf/)
    # StarGazer (https://cran.r-project.org/web/packages/stargazer/)
    # GGplot2 (https://cran.r-project.org/web/packages/ggplot2/)
    # GridExtra (https://cran.r-project.org/web/packages/gridExtra)
    for ( i in c("sqldf", "stargazer", "ggplot2", "gridExtra") )
        if (!is.element( i, installed.packages()[,1] ) )
            install.packages(i)

}