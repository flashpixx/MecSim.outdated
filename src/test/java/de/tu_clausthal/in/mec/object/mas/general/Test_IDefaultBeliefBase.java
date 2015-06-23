package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.general.CLiteral;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * test class for generic belief base
 */
public class Test_IDefaultBeliefBase
{
    /**
     * initializes a predefined beliefbase
     *
     * @return predefined beliefbase
     */
    private IBeliefBase<Literal> generateTestset()
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};

        // add some inherited beliefbases
        l_beliefbase.add( new CPath( "aa1" ), new IDefaultBeliefBase<Literal>(  ){} );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new IDefaultBeliefBase<Literal>(  ){} );

        return l_beliefbase;
    }

    @Test
    public void testAddGetBeliefbase()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // create new beliefbase to add
        final IBeliefBase<Literal> l_testBeliefbase = new IDefaultBeliefBase<Literal>(){};
        l_testBeliefbase.add( CPath.EMPTY, new CLiteral( ASSyntax.createLiteral( "test" ) ) );

        // add to some existing paths
        l_beliefbase.add( new CPath( "aa3" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // add to some non-existing paths
        l_beliefbase.add( new CPath( "aa4/bb2/cc3/dd4" ), l_testBeliefbase );

        // check correct addition
        assertEquals( l_beliefbase.get( "aa3" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa1/bb1" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa4/bb2/cc3/dd4" ), l_testBeliefbase );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEmptyPath()
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};
        l_beliefbase.add( CPath.EMPTY, new IDefaultBeliefBase<Literal>(){} );
    }

    @Test
    public void testAddGetLiteral( )
    {
        // generate testset and literal
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );

        // push literal into top-level set
        l_beliefbase.add( CPath.EMPTY, l_testLiteral );

        // push literal into some existing inherited beliefbase
        l_beliefbase.add( "aa1", l_testLiteral );
        l_beliefbase.add( "aa2/bb1", l_testLiteral );

        // push literal into some non-existing inherited beliefbase
        l_beliefbase.add( "aa3", l_testLiteral );
        l_beliefbase.add( "aa4/bb1/cc1", l_testLiteral );

        assertTrue( l_beliefbase.getTopLiterals().contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa3" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa4/bb1/cc1" ) ).contains( l_testLiteral ) );
    }

    @Test
    public void testRemoveLiteral()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // create new beliefbase to add
        final IBeliefBase<Literal> l_testBeliefbase = new IDefaultBeliefBase<Literal>(){};
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );
        l_testBeliefbase.add( CPath.EMPTY, l_testLiteral );

        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // check if remove method returns true
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1/cc1/dd1" ), l_testLiteral ) );

        // check if literal was removed
        assertFalse( l_beliefbase.getTopLiterals( new CPath( "aa1/bb1/cc1/dd1" ) ).contains( l_testLiteral ) );

        // check if beliefbase is still existing
        assertEquals( l_beliefbase.get( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );
    }

    @Test
    public void testRemoveBeliefbase()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();
        final IBeliefBase<Literal> l_testBeliefbase = new IDefaultBeliefBase<Literal>(){};

        // add to some existing paths
        l_beliefbase.add( new CPath( "aa3" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // add to some non-existing paths
        l_beliefbase.add( new CPath( "aa4/bb2/cc3/dd4" ), l_testBeliefbase );

        assertFalse( l_beliefbase.remove( CPath.EMPTY ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa3" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1/cc1/dd1" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa4/bb2/cc3/dd4" ) ) );

        // check if beliefbases were removed
        assertEquals( l_beliefbase.get( "aa3" ), null );
        assertEquals( l_beliefbase.get( "aa1/bb1/cc1/dd1" ), null );
        assertEquals( l_beliefbase.get( "aa1/bb1" ), null );
        assertEquals( l_beliefbase.get( "aa4/bb2/cc3/dd4" ), null );
    }

    @Test
    public void testIterator()
    {
        // todo
    }

    @Test
    public void testCollapse()
    {
        // todo
    }
}
