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

package de.tu_clausthal.in.mec.object.mas.jason.belief;

import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.IStorage;
import jason.asSyntax.Literal;


/**
 * Jason specfic beliefbase
 */
public class CBeliefBase extends de.tu_clausthal.in.mec.object.mas.general.implementation.CBeliefBase<Literal>
{
    /**
     * separator
     */
    private final String m_seperator;


    /**
     * ctor
     *
     * @param p_seperator separator
     */
    public CBeliefBase( final String p_seperator )
    {
        super();
        m_seperator = p_seperator;
    }

    /**
     * ctor
     *
     * @param p_storage storage
     * @param p_seperator separator
     */
    public CBeliefBase( final IStorage<ILiteral<Literal>, IBeliefBaseMask<Literal>> p_storage, final String p_seperator
    )
    {
        super( p_storage );
        m_seperator = p_seperator;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <E extends IBeliefBaseMask<Literal>> E createMask( final String p_name )
    {
        return (E) new CMask( p_name, this, m_seperator );
    }

}