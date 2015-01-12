/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.CConfiguration;
import org.metawidget.inspector.annotation.MetawidgetAnnotationInspector;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.javabean.JavaBeanPropertyStyle;
import org.metawidget.inspector.impl.propertystyle.javabean.JavaBeanPropertyStyleConfig;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.swing.SwingMetawidget;

import javax.swing.*;


/**
 * preference dialog http://blog.kennardconsulting.com/2008/06/metawidget-and-tooltips.html
 * http://blog.kennardconsulting.com/2008/06/metawidget-and-namevalue-pairs.html http://blog.kennardconsulting.com/2008/07/metawidget-and-filtering-properties.html
 * http://metawidget.sourceforge.net/doc/reference/en/html/ch01.html#section-introduction-java-part1
 */
public class CPreferenceDialog extends JDialog
{

    public CPreferenceDialog()
    {
        this.setSize( 800, 300 );
        //this.setLocationRelativeTo( p_frame );
        this.setModalityType( ModalityType.APPLICATION_MODAL );


        SwingMetawidget l_widget = new SwingMetawidget();

        BaseObjectInspectorConfig l_inspectconfig = new BaseObjectInspectorConfig().setPropertyStyle(
                new JavaBeanPropertyStyle(
                        new JavaBeanPropertyStyleConfig().setSupportPublicFields( true )
                )
        );

        l_widget.setInspector(
                new CompositeInspector(
                        new CompositeInspectorConfig().setInspectors(
                                new PropertyTypeInspector( l_inspectconfig ),
                                new MetawidgetAnnotationInspector( l_inspectconfig )
                        )
                )
        );

        l_widget.setToInspect( CConfiguration.getInstance().get() );
        this.add( l_widget );
    }

}
