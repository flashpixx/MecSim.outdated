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
import java.awt.*;


/**
 * preference class to visualize the configuration - create a modal window
 */
public class CConfigurationDialog extends JDialog
{

    /**
     * ctor *
     */
    public CConfigurationDialog()
    {
        this.initialize();
    }


    /**
     * ctor
     *
     * @param p_frame parent frame
     */
    public CConfigurationDialog( Frame p_frame )
    {
        this.setLocationRelativeTo( p_frame );
        this.initialize();
    }


    /**
     * create UI with data binding *
     */
    private void initialize()
    {
        //this.setSize( 800, 300 );
        this.setAlwaysOnTop( true );
        this.setResizable( false );
        this.setModalityType( ModalityType.APPLICATION_MODAL );

        // MetaWidget create a full UI of the object, be we do not use a getter / setter method, we are using
        // public properties instead
        SwingMetawidget l_widget = new SwingMetawidget();

        BaseObjectInspectorConfig l_inspectconfig = new BaseObjectInspectorConfig().setPropertyStyle(
                new JavaBeanPropertyStyle(
                        new JavaBeanPropertyStyleConfig().setSupportPublicFields( true )
                )
        );

        // there exists object-in-object relation and annotation in the object, so we redefine the inspector
        // as a composit inspector and within a propertytype and annotation inspector
        l_widget.setInspector(
                new CompositeInspector(
                        new CompositeInspectorConfig().setInspectors(
                                new PropertyTypeInspector( l_inspectconfig ),
                                new MetawidgetAnnotationInspector( l_inspectconfig )
                        )
                )
        );

        // at last we need a databinding between UI components and properties of the object, so that
        // the properties are update automatically on changing
        /*
        l_widget.addWidgetProcessor(
                new BeansBindingProcessor(
                        new BeansBindingProcessorConfig().setUpdateStrategy( AutoBinding.UpdateStrategy.READ_WRITE )
                )
        );
        */

        // bind the object to the UI and add the metawidget to the frame
        l_widget.setToInspect( CConfiguration.getInstance().get() );

        this.setLayout( new FlowLayout() );
        this.add( l_widget );
        this.pack();
    }

}
