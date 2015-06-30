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

package de.tu_clausthal.in.mec.object.car;

import de.tu_clausthal.in.mec.CConfiguration;


/**
 * converting driving units
 *
 * @note [speed in meter/sec] * [timesampling in second] = [moved length in m] / ]cellsampling in meter] = moved cells ( km/h = 1000 / 3600 m/s )
 */
public class CUnitConvert
{

    /**
     * constant value to scale km/h in m/s
     **/
    private static final double c_kmhInMs = 1000.0 / 3600.0;
    /**
     * constant value to scale m/s in km/h
     **/
    private static final double c_MsInKmh = 1 / c_kmhInMs;
    /**
     * cell size in meter
     */
    private final int m_cellsize = CConfiguration.getInstance().get().<Integer>get("simulation/traffic/cellsampling");
    /**
     * timestep in seconds
     */
    private final int m_timestep = CConfiguration.getInstance().get().<Integer>get("simulation/traffic/timesampling");
    /**
     * meter which can be moved at one timestep
     */
    private final double m_distancetimestep = c_kmhInMs * m_timestep;
    /**
     * number of cells which can be moved at one timestep
     */
    private final int m_celltimestep = (int) Math.floor(m_distancetimestep / m_cellsize);


    /**
     * returns the speed change in one timestep
     *
     * @param p_acceleration acceleration or deceleration in m/sec^2
     * @return speed change in km/h
     */
    public final double getAccelerationToSpeed(final double p_acceleration)
    {
        return (p_acceleration * c_MsInKmh * m_timestep);
    }

    /**
     * returns the cell size
     *
     * @return cell size in meter
     */
    public final int getCellSize()
    {
        return m_cellsize;
    }

    /**
     * returns cell number to distance in meter
     *
     * @param p_cells cell number
     * @return meter value
     */
    public final double getCellToMeter(final int p_cells)
    {
        return p_cells * m_cellsize;
    }

    /**
     * returns the speed for traveling the given distance in one timestep
     *
     * @param p_distance distance in meter
     * @return speed in km/h
     */
    public final int getSpeedOfDistance(final double p_distance)
    {
        return (int) (p_distance / m_timestep * c_MsInKmh);
    }

    /**
     * returns the speed in cell positions at one timestep
     *
     * @param p_speed speed in km/h
     * @return amount of cells / timestep
     */
    public final int getSpeedToCell(final int p_speed)
    {
        return p_speed * m_celltimestep;
    }

    /**
     * returns the distance in meter which
     * can moved in one timestep
     *
     * @param p_speed speed in km/h
     * @return meter
     */
    public final double getSpeedToDistance(final int p_speed)
    {
        return p_speed * c_kmhInMs * m_timestep;
    }

    /**
     * returns the timestep in seconds
     */
    public final int getTime()
    {
        return m_timestep;
    }
}
