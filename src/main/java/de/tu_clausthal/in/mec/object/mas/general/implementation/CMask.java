package de.tu_clausthal.in.mec.object.mas.general.implementation;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.IStorage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * mask of a beliefbase
 *
 * @tparam P type of the beliefbase element
 */
public class CMask<P> implements IBeliefBaseMask<P>
{
    /**
     * mask name
     */
    private final String m_name;
    /**
     * parent name
     */
    private final IBeliefBaseMask<P> m_parent;
    /**
     * reference to the beliefbase context
     */
    private final IBeliefBase<P> m_beliefbase;

    /**
     * private ctpr
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     */
    public CMask( final String p_name, final IBeliefBase<P> p_beliefbase )
    {
        this( p_name, p_beliefbase, null );
    }

    /**
     * private ctpr
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     * @param p_parent reference to the parent mask
     */
    public CMask( final String p_name, final IBeliefBase<P> p_beliefbase, final IBeliefBaseMask<P> p_parent )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CMask.class, "nameempty" ) );
        if ( p_beliefbase == null )
            throw new IllegalArgumentException( CCommon.getResourceString( CMask.class, "beliefbaseempty" ) );

        m_name = p_name;
        m_beliefbase = p_beliefbase;
        m_parent = p_parent;
    }

    @Override
    public void add( final CPath p_path, final ILiteral<P> p_literal )
    {
        this.add( p_path, p_literal, null );
    }

    @Override
    public IBeliefBaseMask<P> add( final CPath p_path, final IBeliefBaseMask<P> p_mask )
    {
        return this.add( p_path, p_mask, null );
    }

    @Override
    public IBeliefBaseMask<P> add( final CPath p_path, final IBeliefBaseMask<P> p_mask, final IGenerator<P> p_generator
    )
    {
        return walk( p_path, this, p_generator ).add( p_mask );
    }

    @Override
    public void add( final CPath p_path, final ILiteral<P> p_literal, final IGenerator<P> p_generator
    )
    {
        walk( p_path, this, p_generator ).add( p_literal );
    }

    @Override
    public IBeliefBaseMask<P> clone( final IBeliefBaseMask<P> p_parent )
    {
        return new CMask<>( m_name, m_beliefbase, p_parent );
    }

    @Override
    public Set<ILiteral<P>> getLiterals( final CPath p_path )
    {
        return walk( p_path, this, null ).getLiterals();
    }

    @Override
    public Set<ILiteral<P>> getLiterals()
    {
        return new HashSet<ILiteral<P>>()
        {{
                for ( final ILiteral<P> l_item : m_beliefbase )
                    add( l_item );
            }};
    }

    @Override
    public Set<IBeliefBaseMask<P>> getMasks( final CPath p_path )
    {
        return null;
    }

    @Override
    public Set<IBeliefBaseMask<P>> getMasks()
    {
        return new HashSet<IBeliefBaseMask<P>>()
        {{

            }};
    }

    @Override
    public CPath getFQNPath()
    {
        return getFQNPath( this, new CPath() );
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public IBeliefBaseMask<P> getParent()
    {
        return m_parent;
    }

    @Override
    public boolean hasParent()
    {
        return m_parent != null;
    }

    @Override
    public Iterator<ILiteral<P>> iterator()
    {
        final CPath l_path = this.getFQNPath();
        final Set<ILiteral<P>> l_items = new HashSet<>();
        for ( final ILiteral<P> l_literal : m_beliefbase )
            l_items.add( l_literal.clone( l_path ) );

        return l_items.iterator();
    }

    @Override
    public int hashCode()
    {
        return 47 * m_name.hashCode() + 49 * m_beliefbase.hashCode();
    }

    @Override
    public String toString()
    {
        return "{ name : " + m_name + ", fqn : " + this.getFQNPath() + ", storage : " + m_beliefbase.getStorage() + " }";
    }

    @Override
    public void add( final ILiteral<P> p_literal )
    {
        m_beliefbase.add( p_literal );
    }

    @Override
    public IBeliefBaseMask<P> add( final IBeliefBaseMask<P> p_mask )
    {
        // check first, if a mask with an equal storage exists  on the path
        for ( IBeliefBaseMask<P> l_mask = this; l_mask != null; )
        {
            if ( this.getStorage().equals( p_mask.getStorage() ) )
                throw new IllegalArgumentException( CCommon.getResourceString( CMask.class, "storageequal", p_mask.getName(), l_mask.getFQNPath() ) );

            l_mask = l_mask.getParent();
        }

        return m_beliefbase.add( p_mask.clone( this ) );
    }

    @Override
    public void clear()
    {
        m_beliefbase.clear();
    }

    @Override
    public IBeliefBaseMask<P> createMask( final String p_name )
    {
        return m_beliefbase.createMask( p_name );
    }

    @Override
    public <L extends IStorage<ILiteral<P>, IBeliefBaseMask<P>>> L getStorage()
    {
        return m_beliefbase.getStorage();
    }

    @Override
    public boolean isEmpty()
    {
        return m_beliefbase.isEmpty();
    }

    @Override
    public void remove( final IBeliefBaseMask<P> p_mask )
    {
        m_beliefbase.remove( p_mask );
    }

    @Override
    public void remove( final ILiteral<P> p_literal )
    {
        m_beliefbase.remove( p_literal );
    }

    @Override
    public void update()
    {
        m_beliefbase.update();
    }

    /**
     * static method to generate FQN path
     *
     * @param p_mask curretn path
     * @param p_path current path
     * @return path
     *
     * @tparam Q type of the beliefbase elements
     */
    private static <Q> CPath getFQNPath( final IBeliefBaseMask<Q> p_mask, final CPath p_path )
    {
        p_path.pushfront( p_mask.getName() );
        return !p_mask.hasParent() ? p_path : getFQNPath( p_mask.getParent(), p_path );
    }

    /**
     * returns a mask on the recursive descend
     *
     * @param p_path path
     * @param p_root start / root node
     * @param p_generator generator object for new masks
     * @return mask
     *
     * @note a path can contains ".." to use the parent object
     * @tparam Q literal type
     */
    private static <Q> IBeliefBaseMask<Q> walk( final CPath p_path, final IBeliefBaseMask<Q> p_root, final IGenerator<Q> p_generator )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) )
            return p_root;

        // get the next mask (on ".." the parent is returned otherwise the child is used)
        IBeliefBaseMask<Q> l_mask = "..".equals( p_path.get( 0 ) ) ? p_root.getParent() : p_root.getStorage().getSingleElement( p_path.get( 0 ) );

        // if a generator is exists and the mask is null, a new mask is created and added to the current
        if ( ( l_mask == null ) && ( p_generator != null ) && ( !( "..".equals( p_path.get( 0 ) ) ) ) )
            l_mask = p_root.add( p_generator.create( p_path.get( 0 ) ) );

        // if mask null an exception is thrown
        if ( l_mask == null )
            throw new IllegalArgumentException( CCommon.getResourceString( CMask.class, "pathelementnotfound", p_path.get( 0 ), p_path ) );

        // recursive descend
        return walk( p_path.getSubPath( 1 ), l_mask, p_generator );
    }
}
