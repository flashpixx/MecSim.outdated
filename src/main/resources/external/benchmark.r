######################################################################################
# GPL License                                                                        #
#                                                                                    #
# This file is part of the micro agent-based traffic simulation MecSim of            #
# Clausthal University of Technology - Mobile and Enterprise Computing               #
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


# visualization script for benchmarks json files
#
# @param filename benchmark json file
# @param type output type of the table - allowed values: text, latex, html
# @param scaling scaling factor of the time
# @param x-label label of the plot x-axis
# @param y-label label if the plot y-axis - % element will be replaced with scaling time factor
# @param calllabel y-axis label of the bar blot
#
# @return matrix / table with statistic data
#
mecsim.benchmark <- function(
    filename,
    type="text",
    scaling=1e+6,
    xlabel="",
    ylabel="time in %1.0E seconds (log-scale)",
    calllabel="number of calls"
){
    # --- check required packages ---
    # JsonLite (https://cran.r-project.org/web/packages/jsonlite/)
    # StarGazer (https://cran.r-project.org/web/packages/stargazer/)
    # GGplot2 (https://cran.r-project.org/web/packages/ggplot2/)
    # GridExtra (https://cran.r-project.org/web/packages/gridExtra)
    for ( i in c("jsonlite", "stargazer", "ggplot2", "gridExtra") )
        if (!is.element( i, installed.packages()[,1] ) )
            install.packages(i)

  
    # --- read data, keys and columns ---
    lo_data    <- jsonlite::fromJSON( filename, flatten = TRUE );
  
    la_keys <- names( lo_data )
    if (length(la_keys) < 1) stop(sprintf("no elements are found within the benchmarkfile [%s]", filename))

    la_columns <- names( lo_data[[la_keys[1]]] )
    if (length(la_columns) < 1) stop(sprintf("no columns elements are found within the benchmarkfile [%s]", filename))
  

    # --- build table ---
    ln_table = matrix(, nrow = length(la_keys), ncol = length(la_columns), dimnames = list( la_keys, la_columns ) )
    i = 1
    for ( r in la_keys )
    {
        j = 1
        for ( c in la_columns )
        {
            ln_table[i,j] <- lo_data[[r]][[c]]
            j <- j+1
        }
        i <- i+1
    }
  
  
    # --- scaling ---
    for (i in c("arithmetic mean", "geometric mean", "kurtosis", "max", "min", "25-percentile", "50-percentile", "75-percentile", "skewness", "standard deviation", "sum", "sum square", "variance"))
        ln_table[, i] <- ln_table[, i] / scaling

  
    # --- build output ---
    stargazer::stargazer(
        data  = ln_table,
        type  = type
    )
  
  
    # --- build boxplot ---
    lo_frame <- data.frame(
        label  = gsub("\\(", "\n\\(", la_keys),
        min    = ln_table[, "min"],
        max    = ln_table[, "max"],
        low    = ln_table[, "25-percentile"],
        mid    = ln_table[, "50-percentile"],
        top    = ln_table[, "75-percentile"],
        mean   = ln_table[, "arithmetic mean"],
        count  = ln_table[, "count"]
    )
    # boxplot
    lo_boxplot <- ggplot2::ggplot(
        lo_frame,
        ggplot2::aes(
            x      = label,
            y      = mean,
            ymin   = min,
            lower  = low,
            middle = mid,
            upper  = top,
            ymax   = max,
            fill   = label
        ),
        alpha    = 0.25,
        width    = 0.5,
        position = position_dodge(width = 0.9)
    ) +
    ggplot2::geom_boxplot(
        stat = "identity"
    ) +
    # axis labeling
    ggplot2::labs(
        y = sprintf(ylabel, 1e-9*scaling),
        x = xlabel
    ) +
    # mean point
    ggplot2::stat_summary(fun.y = "mean", geom = "text", label="----", size= 10, color= "black" ) +
    # logarithm scaling on the time axis
    ggplot2::coord_trans(y = "log10") +
    # theme layout
    ggplot2::guides( fill = FALSE ) +
    ggplot2::theme_bw() +
    ggplot2::theme(
        panel.grid.major = ggplot2::element_line(colour = "grey60")
    )

  
    # calling caller plot
    lo_callerplot <-  ggplot2::qplot(
        x        = label,
        y        = count,
        fill     = label,
        data     = lo_frame,
        geom     = "bar",
        stat     = "identity",
        position = "dodge"
    ) +
    # disable legend
    ggplot2::guides( fill = FALSE ) +
    # axis labeling
    ggplot2::labs(
        y = calllabel,
        x = xlabel
    )  +
    # logarithm scaling on the caller axis
    ggplot2::scale_y_log10(
        labels = function(x) format( x, nsmall = 0, scientific = FALSE )
    ) +
    # theme layout
    ggplot2::guides( fill = FALSE ) +
    ggplot2::theme_bw() +
    ggplot2::theme(
        panel.grid.major = ggplot2::element_line(colour = "grey60")
    )
  
    # arrange plots and output
    gridExtra::grid.arrange(
        lo_boxplot,
        lo_callerplot,
        nrow = 2
    )


  return(ln_table)
}
