/**
 * @cond ###################################################################################### # GPL License # # # #
 * This file is part of the TUC Wirtschaftsinformatik - MecSim                        # # Copyright (c) 2014-15,
 * Philipp
 * Kraus (philipp.kraus@tu-clausthal.de)               # # This program is free software: you can redistribute it
 * and/or
 * modify               # # it under the terms of the GNU General Public License as                            # #
 * published by the Free Software Foundation, either version 3 of the # # License, or (at your option) any later
 * version.                                    # # # # This program is distributed in the hope that it will be useful,
 * # # but WITHOUT ANY WARRANTY; without even the implied warranty of # # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the                      # # GNU General Public License for more details.
 * # # # # You should have received a copy of the GNU General Public License # # along with
 * this program. If not, see http://www.gnu.org/licenses/                  # ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.common;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * test for CPath class
 */
public class Test_CPath
{


    /**
     * test-case empty path
     */
    @Test
    public void testEmpty()
    {
        final CPath l_path = new CPath();

        assertTrue( l_path.isEmpty() );
        assertEquals( l_path.size(), 0 );
    }


    /**
     * test-case check path iterator
     */
    @Test
    public void testIterator()
    {
        final ArrayList<String> l_part = new ArrayList()
        {{
                add( "a" );
                add( "b" );
                add( "c" );
            }};
        final String l_fullpath = StringUtils.join( l_part, "/" );


        final CPath l_path = new CPath( l_fullpath );
        assertEquals( l_part.size(), l_path.size() );
        assertEquals( l_path, l_fullpath );

        for ( int i = 0; i < l_part.size(); i++ )
            assertEquals( l_part.get( i ), l_path.get( i ) );
    }

}
