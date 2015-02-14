/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.norm.institution;

import de.tu_clausthal.in.mec.object.norm.INormMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * default institution collection
 */
public class CDefaultInstitutionCollection<T> implements IInstitutionCollection<T>
{


    /**
     * list of insitutions
     */
    private Set<IInstitution<T>> m_institution = new HashSet<>();


    @Override
    public void send( INormMessage<T> p_message )
    {
        for ( IInstitution<T> l_item : m_institution )
            l_item.receive( p_message );
    }

    @Override
    public void release()
    {
        for ( IInstitution<T> l_item : m_institution )
            l_item.release();
    }

    @Override
    public int size()
    {
        return m_institution.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_institution.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        return m_institution.contains( o );
    }

    @Override
    public Iterator<IInstitution<T>> iterator()
    {
        return m_institution.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_institution.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a )
    {
        return m_institution.toArray( a );
    }

    @Override
    public boolean add( IInstitution<T> iInstitution )
    {
        return m_institution.add( iInstitution );
    }

    @Override
    public boolean remove( Object o )
    {
        return m_institution.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        return m_institution.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends IInstitution<T>> c )
    {
        return m_institution.addAll( c );
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        return m_institution.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return m_institution.retainAll( c );
    }

    @Override
    public void clear()
    {
        m_institution.clear();
    }
}
