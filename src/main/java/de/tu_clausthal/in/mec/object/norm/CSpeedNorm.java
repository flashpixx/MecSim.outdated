/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.mec.object.norm;

import de.tu_clausthal.in.mec.object.norm.institution.IInstitution;


/**
 * norm for speed check
 */
public class CSpeedNorm implements INorm<INormObject>
{

    /**
     * tolerance value in [0,1] *
     */
    private double m_tolerance = 0.1;
    /* maximum speed value for check **/
    private int m_maxspeed = Integer.MAX_VALUE;
    /**
     * dedicated institution *
     */
    private IInstitution<INormObject> m_institution = null;
    /**
     * name der norm *
     */
    private String m_name = this.getClass().getName();


    /**
     * ctor
     *
     * @param p_institution defines the institution
     */
    public CSpeedNorm( IInstitution<INormObject> p_institution )
    {
        m_institution = p_institution;
    }


    /**
     * ctor
     *
     * @param p_institution defines the institution
     * @param p_name        name of the norm
     */
    public CSpeedNorm( IInstitution<INormObject> p_institution, String p_name )
    {
        m_institution = p_institution;
        m_name = p_name;
    }


    /**
     * ctor
     *
     * @param p_institution defines institution
     * @param p_maxspeed    defines maximum allowed speed
     */
    public CSpeedNorm( IInstitution<INormObject> p_institution, int p_maxspeed )
    {
        m_institution = p_institution;
        m_maxspeed = p_maxspeed;
    }


    /**
     * ctor
     *
     * @param p_institution defines institution
     * @param p_name        name of the norm
     * @param p_maxspeed    defines maximum allowed speed
     */
    public CSpeedNorm( IInstitution<INormObject> p_institution, String p_name, int p_maxspeed )
    {
        m_institution = p_institution;
        m_maxspeed = p_maxspeed;
        m_name = p_name;
    }


    @Override
    public INormCheckResult check( INormObject p_object )
    {
        return new CNormResultSpeed( p_object.getCurrentSpeed() > m_maxspeed, Math.max( 0, Math.min( 1, ( p_object.getCurrentSpeed() - m_maxspeed ) / ( m_maxspeed * m_tolerance ) ) ) );
    }

    @Override
    public IInstitution<INormObject> getInstitution()
    {
        return m_institution;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void release()
    {

    }

    /**
     * inner class to represent the norm result
     */
    public class CNormResultSpeed implements INormCheckResult<Boolean>
    {

        /**
         * satisfiable boolean *
         */
        private boolean m_match = false;
        /**
         * weight *
         */
        private double m_weight = 1;


        /**
         * ctor to create the value
         *
         * @param p_match  value
         * @param p_weight weight value
         */
        public CNormResultSpeed( boolean p_match, double p_weight )
        {
            if ( ( p_weight < 0 ) || ( p_weight > 1 ) )
                throw new IllegalArgumentException( "weight must be in [0,1]" );

            m_match = p_match;
            m_weight = p_weight;
        }

        @Override
        public double getWeight()
        {
            return m_weight;
        }

        @Override
        public Boolean getResult()
        {
            return m_match;
        }
    }
}
