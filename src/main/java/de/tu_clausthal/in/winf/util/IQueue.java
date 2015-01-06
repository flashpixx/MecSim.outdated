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

package de.tu_clausthal.in.winf.util;

import java.util.Collection;
import java.util.Queue;


/**
 *
 */
public interface IQueue<T> {

    /**
     * returns the queue of all items
     *
     * @return queue
     */
    public Queue<T> getAll();


    /**
     * remove an item from the processed list
     *
     * @return item
     */
    public T pop();


    /**
     * push an item to the unprocessed list
     *
     * @param p_item item
     */
    public void push(T p_item);


    /**
     * checks that the queue is empty
     *
     * @return empty boolean value
     */
    public boolean isEmpty();


    /**
     * moves elements from the unprocessed queue
     * to the processed queue
     */
    public void reset();


    /**
     * number of elements
     *
     * @return value
     */
    public int size();


    /**
     * number of unprocessed items
     *
     * @return count values
     */
    public int unprocessedsize();


    /**
     * number of proccessed items
     *
     * @return count value
     */
    public int processsize();


    /**
     * checks that an elements is in the queue
     *
     * @param p_item item
     * @return boolean for existence
     */
    public boolean contains(T p_item);


    /**
     * converts the unprocessed items to an array
     *
     * @param p_array ouput array
     */
    public void unprocessed2array(T[] p_array);


    /**
     * converts the unprocessed data to an array
     *
     * @param p_array output array
     */
    public void process2array(T[] p_array);


    /**
     * adds an item
     *
     * @param p_item item
     */
    public void add(T p_item);


    /**
     * adds a collection
     *
     * @param p_item collection
     */
    public void add(Collection<T> p_item);


    /**
     * removes an item
     *
     * @param p_item item
     */
    public void remove(T p_item);


    /**
     * clear all data *
     */
    public void clear();


}
