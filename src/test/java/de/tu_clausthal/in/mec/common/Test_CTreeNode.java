/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.common;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * test for CTreeNode class
 */
public class Test_CTreeNode
{

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
        l_root.traverseto( new CPath( "sub/subsub" ) );

        assertTrue( l_root.pathexist( new CPath( "sub/subsub" ) ) );
    }

}
