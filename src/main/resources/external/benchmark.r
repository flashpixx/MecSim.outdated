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



#' visualization script for benchmarks json files
#' 
#' @param pc_file
#' @param type output type of the table - allowed values: text, latex, html
#' @param label label of the plot and table - % element will be replaced with scaling time factor
#' @param xlabel label of the plot x-axis 
#' @param ylabel label if the plot y-axis - % element will be replaced with scaling time factor
mecsim.benchmark <- function( pc_file, type="text", scaling=1e+6, label="MecSim Method Microbenchmark in %1.0E seconds", xlabel="time in %1.0E seconds", ylabel="" )
{
  # --- check required packages ---
  # JsonLite (https://cran.r-project.org/web/packages/jsonlite/) 
  # StarGazer (https://cran.r-project.org/web/packages/stargazer/)
  # GGplot2 (https://cran.r-project.org/web/packages/ggplot2/)
  for ( i in c("jsonlite", "stargazer", "ggplot2") )
    if (!is.element( i, installed.packages()[,1] ) )
      install.packages(i)

  
  # --- read data, keys and columns ---
  lo_data    <- jsonlite::fromJSON( pc_file, flatten = TRUE );
  
  la_keys <- names( lo_data )
  if (length(la_keys) < 1) stop(sprintf("no elements are found within the benchmarkfile [%s]", pc_file))

  la_columns <- names( lo_data[[la_keys[1]]] )
  if (length(la_columns) < 1) stop(sprintf("no columns elements are found within the benchmarkfile [%s]", pc_file))
  
  lc_label <- sprintf(label, 1e-9*scaling)
  

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
      type  = type, 
      title = lc_label
  )
  
  
  # --- build boxplot ---
  lo_frame <- data.frame(
      label  = gsub("\\(", "\n\\(", la_keys),
      min    = ln_table[, "min"],
      max    = ln_table[, "max"],
      low    = ln_table[, "25-percentile"],
      mid    = ln_table[, "50-percentile"],
      top    = ln_table[, "75-percentile"],
      mean   = ln_table[, "arithmetic mean"]
  )
  # boxes
  lo_plot <- ggplot2::ggplot( 
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
  # title label
  ggplot2::ggtitle(
      lc_label
  ) +
  # axis labeling  
  ggplot2::labs(
      x = ylabel,
      y = sprintf(xlabel, 1e-9*scaling)
  ) +
  # mean point      
  ggplot2::stat_summary(fun.y = "mean", geom = "text", label="----", size= 10, color= "black" ) +
  # logarithm scaling on the time axis
  ggplot2::coord_trans(y = "log10") +
  # disable legend      
  ggplot2::guides(fill=FALSE) +
  # coloring      
  ggplot2::theme_bw() + ggplot2::theme( panel.grid.major = ggplot2::element_line(colour = "grey60") )
  # create output
  print(lo_plot)


  
  return(ln_table)
}
