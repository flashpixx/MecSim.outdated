package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.general.CLiteral;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


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

        // add some inherited getBeliefbases
        l_beliefbase.add( new CPath( "aa1" ), new IDefaultBeliefBase<Literal>(){} );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new IDefaultBeliefBase<Literal>(  ){} );

        // add some literals
        l_beliefbase.add( new CPath( "aa1" ), new CLiteral( ASSyntax.createLiteral( "test1" ) ) );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new CLiteral( ASSyntax.createLiteral( "test2" ) ) );

        return l_beliefbase;
    }

    @Test
    public void testGetMethods()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // non existing getBeliefbases should return null
        assertEquals( l_beliefbase.get( new CPath( "a" ) ), null );
        assertEquals( l_beliefbase.get( new CPath( "xxx/ab" ) ), null );

        l_beliefbase.add( new CPath( "x" ), new IDefaultBeliefBase<Literal>(  ){});
        l_beliefbase.add( new CPath( "xxx/yyy" ), new IDefaultBeliefBase<Literal>(  ){});

        // non existing literals should return empty set
        assertEquals(l_beliefbase.get( new CPath( "x" ) ).getLiterals(), Collections.EMPTY_SET );
        assertEquals( l_beliefbase.get( new CPath( "xxx/yyy" ) ).literals(), Collections.EMPTY_SET );
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
        assertEquals( l_beliefbase.get( new CPath( "aa3" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1/cc1/dd1" ) ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( new CPath( "aa4/bb2/cc3/dd4" ) ), l_testBeliefbase );
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
        final CLiteral l_testLiteral2 = new CLiteral( ASSyntax.createLiteral( "test2" ) );
        final CLiteral l_testLiteral3 = new CLiteral( ASSyntax.createLiteral( "test3" ) );

        // push literal into top-level set
        l_beliefbase.add( CPath.EMPTY, l_testLiteral );
        l_beliefbase.add( l_testLiteral2 );

        // push literal into some existing inherited beliefbase
        l_beliefbase.add( new CPath( "aa1" ), l_testLiteral );
        l_beliefbase.add( new CPath( "aa2/bb1" ), l_testLiteral );
        l_beliefbase.add( new CLiteral( ASSyntax.createLiteral( "aa2/bb1/test2" ) ) );

        // push literal into some non-existing inherited beliefbase
        l_beliefbase.add( new CPath( "aa3" ), l_testLiteral );
        l_beliefbase.add( new CPath( "aa4/bb1/cc1" ), l_testLiteral );
        l_beliefbase.add( new CLiteral( ASSyntax.createLiteral( "xxx/yyy/zzz/test3" ) ) );

        assertTrue( l_beliefbase.literals().contains( l_testLiteral ) );
        assertTrue( l_beliefbase.literals().contains( l_testLiteral2 ) );
        assertTrue( l_beliefbase.literals( new CPath( "aa1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.literals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.literals( new CPath( "aa3" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.literals( new CPath( "aa4/bb1/cc1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.literals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral2 ) );
        assertTrue( l_beliefbase.literals( new CPath( "xxx/yyy/zzz" ) ).contains( l_testLiteral3 ) );
    }

    @Test
    public void testRemoveLiteral()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // create new beliefbase to add
        final IBeliefBase<Literal> l_testBeliefbase = new IDefaultBeliefBase<Literal>(){};

        // add some test literals
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );
        l_testBeliefbase.add( CPath.EMPTY, l_testLiteral );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // check if remove method returns true
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1/cc1/dd1" ), l_testLiteral ) );

        // check if literal was removed
        assertFalse( l_beliefbase.get( new CPath( "aa1/bb1/cc1/dd1" ), l_testLiteral.getFunctor().toString(), ILiteral.class ).contains( l_testLiteral ) );

        // check if beliefbase is still existing
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1/cc1/dd1" ) ), l_testBeliefbase );
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

        // test return values of remove function
        assertFalse( l_beliefbase.remove( CPath.EMPTY ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa3" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1/cc1/dd1" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa1/bb1" ) ) );
        assertTrue( l_beliefbase.remove( new CPath( "aa4/bb2/cc3/dd4" ) ) );

        // check if getBeliefbases were correctly removed
        assertEquals( l_beliefbase.get( new CPath( "aa3" ) ), null );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1/cc1/dd1" ) ), null );
        assertEquals( l_beliefbase.get( new CPath( "aa1/bb1" ) ), null );
        assertEquals( l_beliefbase.get( new CPath( "aa4/bb2/cc3/dd4" ) ), null );
    }

    @Test
    public void testIterator()
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};
        final Map<CPath, ILiteral<Literal>> l_literals = new HashMap(){{
            put( new CPath("aa1"), new CLiteral( ASSyntax.createLiteral( "test1" ) ) );
            put(new CPath("aa2/bb1/cc1/dd1"), new CLiteral(ASSyntax.createLiteral("test2")));
            put(CPath.EMPTY, new CLiteral(ASSyntax.createLiteral("test3")));
        }};
        final Set<ILiteral<Literal>> l_initial = new HashSet(){{
            for (final Map.Entry<CPath, ILiteral<Literal>> l_entry : l_literals.entrySet())
                add( new CLiteral( l_entry.getValue().getLiteral(), l_entry.getKey() ));
        }};


        // add some literals
        for (final Map.Entry<CPath,ILiteral<Literal>> l_entry : l_literals.entrySet())
            l_beliefbase.add( l_entry.getKey(), l_entry.getValue() );

        // build literal set with iterator
        final Set<ILiteral<Literal>> l_beliefbaseElements = new HashSet<ILiteral<Literal>>(){{
            for (final ILiteral<Literal> l_element : l_beliefbase)
                add( l_element );
        }};

        // both sets have to be equal
        assertEquals( l_beliefbaseElements, l_initial);
    }

    @Test
    public void testClear()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // check if there are getBeliefbases before removing
        assertTrue(l_beliefbase.get(new CPath( "aa2/bb1/cc1/dd1" ) ) != null);
        assertTrue(l_beliefbase.get(new CPath( "aa2/bb1/cc1" ) ) != null);
        assertTrue(l_beliefbase.get( new CPath( "aa2" ) ) != null);

        // check if there are non existing
        assertTrue(l_beliefbase.get( new CPath( "xxx" ) ) == null);
        assertEquals(l_beliefbase.get( new CPath( "xxx" ) ), null);

        // do clear
        l_beliefbase.clear( new CPath( "aa2/bb1" ) );

        // check if getBeliefbases were removed
        assertEquals( l_beliefbase.get( new CPath( "aa2/bb1/cc1/dd1" ) ), null );
        assertEquals( l_beliefbase.get( new CPath( "aa2/bb1/cc1" ) ), null );

        // this beliefbase must not be removed
        assertTrue(l_beliefbase.get( new CPath( "aa2/bb1" ) ) != null);
    }

    @Test
    public void testCollapse()
    {
/*
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        final Set<ILiteral<Literal>> l_collapsed = l_beliefbase.prefixedLiterals( CPath.EMPTY );

        assertTrue( l_collapsed.containsAll(
                            new HashSet<ILiteral<Literal>>(){{
                                add(new CLiteral( ASSyntax.createLiteral( "test1" ) ));
                                add(new CLiteral( ASSyntax.createLiteral( "test2" ) ));
                            }}
                    )
        );
        */
    }
}
