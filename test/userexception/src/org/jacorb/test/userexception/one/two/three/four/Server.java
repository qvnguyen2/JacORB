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

import java.util.Properties;
import java.io.*;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.jacorb.orb.util.*;
import org.jacorb.util.ObjectUtil;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
//import org.jacorb.test.listenendpoints.echo_corbaloc.CmdArgs;

public class Server
{
   public static void main(String[] args)
   {
      try
      {
         CmdArgs cmdArgs = new CmdArgs("Server", args);
         boolean cmdArgsStatus = cmdArgs.processArgs();

         // translate any properties set on the commandline but after the
         // class name to a properties
         java.util.Properties props = org.jacorb.util.ObjectUtil.argsToProps(args);


         //init ORB
         org.omg.CORBA_2_3.ORB orb = (org.omg.CORBA_2_3.ORB)
                                        org.omg.CORBA_2_3.ORB.init(args, props);

         //init POA
         POA rootPOA_ = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

         // Create the POA policy list
         org.omg.CORBA.Policy[] poaPolicies =
                    new org.omg.CORBA.Policy[0];

         // Use the rootPOA's POAManager
         org.omg.PortableServer.POAManager poaManager =
                    rootPOA_.the_POAManager();

         // Create the child POA
         POA childPOA_ = rootPOA_.create_POA( "TEST_POA", poaManager, poaPolicies);

         // Activate the POA manager
         poaManager.activate();

         // create servant object
         UserExceptionServerImpl myServant_ = new UserExceptionServerImpl();

         // Activate the servant
         byte[] objectId = childPOA_.activate_object(myServant_);

         final org.omg.CORBA.Object ref = childPOA_.id_to_reference(objectId);
         String ior = orb.object_to_string(ref);
         System.out.println("SERVER IOR: " + ior);
         System.out.flush();

         if (cmdArgs.getIORFile() != null)
         {
            PrintWriter ps = new PrintWriter(new FileOutputStream(
            new File( cmdArgs.getIORFile())));
            ps.println(ior);
            ps.close();
         }


         // wait for requests
         orb.run();

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
