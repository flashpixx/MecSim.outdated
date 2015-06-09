package de.tu_clausthal.in.mec.object.mas.general;

/**
 * generic atom class
 */
public abstract class IDefaultAtom<T> implements IAtom<T>
{
    /**
     * value the atom represents (e.g. string, number)
     */
    private final T m_value;

    /**
     * generic class type
     */
    private Class<?> m_type;

    /**
     * default ctor
     */
    public IDefaultAtom() { m_value = null; }

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
     * Returns a string representation of the atom.
     *
     * @return string representation
     */
    @Override
    public String toString()
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
    public boolean instanceOf(final Class<?> p_class)
    {
        return m_type.isAssignableFrom(p_class);
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
    public final boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }
}
