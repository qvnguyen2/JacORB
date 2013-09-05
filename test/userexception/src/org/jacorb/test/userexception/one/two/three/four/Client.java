package org.jacorb.test.userexception.one.two.three.four;
/*
 *        JacORB  - a free Java ORB
 *
 *   Copyright (C) 1997-2013 Gerald Brose / The JacORB Team.
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Library General Public
 *   License as published by the Free Software Foundation; either
 *   version 2 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this library; if not, write to the Free
 *   Software Foundation, 51 Franklin Street, Fifth Floor, Boston,
 *   MA 02110-1301, USA.
 */

/**
 *
 * @author nguyenq
 */
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import org.omg.CORBA.*;
import org.jacorb.orb.util.*;
import org.jacorb.util.ObjectUtil;
//import org.jacorb.test.listenendpoints.echo_corbaloc.CmdArgs;


public class Client
{
    public static void main( String args[] )
    {
        CmdArgs cmdArgs = null;
        try
        {
            cmdArgs = new CmdArgs("Client", args);
            if (!cmdArgs.processArgs())
            {
                System.exit(1);
            }
            cmdArgs.show();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try
        {
            if (cmdArgs.getIORString() != null)
            {
                // translate any properties set on the commandline but after the
                // class name to a properties
                java.util.Properties props = org.jacorb.util.ObjectUtil.argsToProps(args);

                // Register ORB initializer
                props.setProperty(
                    "org.omg.PortableInterceptor.ORBInitializerClass."
                        + "org.jacorb.test.userexception.one.two.three.four.ORBInitializerImpl",
                    "org.jacorb.test.userexception.one.two.three.four.ORBInitializerImpl");

                for (int i = 1; i <= cmdArgs.getnThreads(); i++) {
                    ClientThread client_thd = new ClientThread (cmdArgs, props, i);
                    client_thd.start();
                    System.out.println ("Client thread " + i + " started");

                    // bring them up slowly
                    Thread.sleep(1000);
                }
            }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
