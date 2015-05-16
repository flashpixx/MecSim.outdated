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

package de.tu_clausthal.in.mec.common;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * test for CTreeNode class
 */
public class Test_CTreeNode
{

    /**
     * test-case for path-data store
     */
    @Test
    public void testDataStore()
    {
        final List<Pair<CPath, Double>> l_treedata = new LinkedList()
        {{
                add( new ImmutablePair<>( new CPath( "sub1" ), new Double( 1 ) ) );
                add( new ImmutablePair<>( new CPath( "sub1/subsub1.1" ), new Double( 1.1 ) ) );
                add( new ImmutablePair<>( new CPath( "sub1/subsub1.2" ), new Double( 1.2 ) ) );

                add( new ImmutablePair<>( new CPath( "sub2" ), new Double( 2 ) ) );
                add( new ImmutablePair<>( new CPath( "sub2/subsub2.1" ), new Double( 2.1 ) ) );

                add( new ImmutablePair<>( new CPath( "sub3" ), new Double( 3 ) ) );
                add( new ImmutablePair<>( new CPath( "sub3/subsub3.1" ), new Double( 3.1 ) ) );
                add( new ImmutablePair<>( new CPath( "sub3/subsub3.2" ), new Double( 3.2 ) ) );
                add( new ImmutablePair<>( new CPath( "sub3/subsub3.3" ), new Double( 3.3 ) ) );
            }};

        final List<Double> l_data = new LinkedList()
        {{
                add( new Double( 0 ) );
            }};
        for ( final Pair<CPath, Double> l_item : l_treedata )
            l_data.add( l_item.getValue() );

        final CTreeNode<Double> l_root = new CTreeNode<>( "root" );
        l_root.setData( new Double( 0 ) );
        l_root.setData( l_treedata );


        assertEquals( l_root.getNode( "sub3/subsub3.1" ).getData(), new Double( 3.1 ) );
        for ( final Double l_item : l_root.getTreeData( true ) )
        {
            assertTrue( l_data.contains( l_item ) );
            l_data.remove( l_item );
        }
        assertTrue( l_data.isEmpty() );
    }

    /**
     * test-case for root node
     */
    @Test
    public void testEmpty()
    {
        final CTreeNode<String> l_node = new CTreeNode<>( "root" );

        assertTrue( l_node.isDataNull() );
        assertFalse( l_node.hasParent() );
    }

    /**
     * test-case for path-node traversing
     */
    @Test
    public void testPathNodeTraversing()
    {
        final CTreeNode<String> l_root = new CTreeNode<>( "root" );
        l_root.getNode( "sub/subsub" );

        assertTrue( l_root.pathExist( "sub/subsub" ) );
        assertNotNull( l_root.getNode( "sub/subsub" ) );
    }

}
