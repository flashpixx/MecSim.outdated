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

package de.tu_clausthal.in.winf.object.norm.institution;

import de.tu_clausthal.in.winf.object.norm.*;
import de.tu_clausthal.in.winf.object.norm.range.CUnionRangeCollection;
import de.tu_clausthal.in.winf.object.norm.range.IRange;
import de.tu_clausthal.in.winf.object.norm.range.IRangeCollection;
import de.tu_clausthal.in.winf.ui.COSMViewer;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;


/**
 * default institution
 */
public class CDefaultInstitution implements IInstitution<INormObject>
{

    /**
     * name of the institution *
     */
    String m_name = "Default Institution";
    /**
     * superior institutions *
     */
    private IInstitutionCollection<INormObject> m_superior = new CDefaultInstitutionCollection();
    /**
     * inferior instituions *
     */
    private IInstitutionCollection<INormObject> m_inferior = new CDefaultInstitutionCollection();
    /**
     * range collection with a disjoint set *
     */
    private IRangeCollection<INormObject> m_range = new CUnionRangeCollection();
    /**
     * norms of the institution *
     */
    private INormCollection<INormObject> m_norms = new CDefaultNormCollection();


    /**
     * default ctor
     */
    public CDefaultInstitution()
    {
        //CSimulationData.getInstance().getCarInstitutionQueue().add(this);
    }

    /**
     * ctor to add a name
     *
     * @param p_name name
     */
    public CDefaultInstitution( String p_name )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "name need not be empty" );
        m_name = p_name;
        //CSimulationData.getInstance().getCarInstitutionQueue().add(this);
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void check( INormObject p_object )
    {
        if ( m_range.check( p_object ) )
            p_object.setMatchedNorm( m_norms.match( p_object ) );
    }

    @Override
    public IRangeCollection<INormObject> getRange()
    {
        return m_range;
    }

    @Override
    public void update( INorm<INormObject> p_norm )
    {
        m_norms.add( p_norm );
    }

    @Override
    public void receive( INormMessage<INormObject> p_message )
    {
        if ( p_message.getTTL() < 0 )
            return;

        p_message.decrementTTL();
        switch ( p_message.getType() )
        {
            case Create:
                m_norms.add( p_message.getNorm() );
                break;
            case Update:
                m_norms.add( p_message.getNorm() );
                break;
            case Delete:
                m_norms.remove( p_message.getNorm() );
                break;
        }

        m_superior.send( p_message );
        m_inferior.send( p_message );
    }

    @Override
    public IInstitutionCollection getSuperior()
    {
        return m_superior;
    }

    @Override
    public IInstitutionCollection getInferior()
    {
        return m_inferior;
    }

    @Override
    public void release()
    {
        //CSimulationData.getInstance().getCarInstitutionQueue().remove(this);
        m_norms.release();
        m_range.release();
        m_inferior.release();
        m_superior.release();

    }

    @Override
    public int size()
    {
        return m_norms.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_norms.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        return m_norms.contains( o );
    }

    @Override
    public Iterator<INorm<INormObject>> iterator()
    {
        return m_norms.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_norms.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a )
    {
        return m_norms.toArray( a );
    }

    @Override
    public boolean add( INorm<INormObject> iNormCarINorm )
    {
        return m_norms.add( iNormCarINorm );
    }

    @Override
    public boolean remove( Object o )
    {
        return m_norms.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        return m_norms.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends INorm<INormObject>> c )
    {
        return m_norms.addAll( c );
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        return m_norms.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return m_norms.retainAll( c );
    }

    @Override
    public void clear()
    {
        m_norms.clear();
    }

    @Override
    public void paint( Graphics2D g, COSMViewer object, int width, int height )
    {
        for ( IRange<INormObject> l_item : m_range )
            l_item.paint( g, object, width, height );

    }
}
