package de.tu_clausthal.in.mec.object.mas.general.implementation;


import de.tu_clausthal.in.mec.object.mas.general.IAtom;


/**
 * generic atom class
 */
public abstract class IDefaultAtom<T> implements IAtom<T>
{
    /**
     * generic class type
     */
    private Class<?> m_type;
    /**
     * value the atom represents (e.g. string, number)
     */
    private final T m_value;

    /**
     * default ctor
     */
    public IDefaultAtom()
    {
        m_value = null;
    }

    /**
     * ctor - with value specified
     *
     * @param p_value the atoms value
     */
    public IDefaultAtom( final T p_value )
    {
        m_value = p_value;
    }

    /**
     * getter for atom value
     *
     * @return the atom's value
     */
    @Override
    public T get()
    {
        return m_value;
    }

    /**
     * hashcode method to compare atoms
     *
     * @return the atom's hashcode
     */
    @Override
    public final int hashCode()
    {
        return m_value.hashCode();
    }

    /**
     * method to check equivalence
     *
     * @param p_object
     * @return true if input parameter equals the atom
     */
    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    /**
     * Returns a string representation of the atom.
     *
     * @return string representation
     */
    @Override
    public final String toString()
    {
        return m_value.toString();
    }

    /**
     * check for the atom's class type
     *
     * @param p_class matching class
     * @return true if the atom's type is assignable from matching class
     */
    @Override
    public final boolean instanceOf( final Class<?> p_class )
    {
        return m_type.isAssignableFrom( p_class );
    }

}
