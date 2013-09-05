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

import java.io.*;
import java.net.*;
import org.omg.CORBA.ORB;
//import org.jacorb.test.listenendpoints.echo_corbaloc.CmdArgs;
import org.jacorb.test.userexception.one.two.three.four.*;
import org.jacorb.test.userexception.one.two.three.four.UserExceptionServerPackage.*;

/**
 *
 * @author nguyenq
 */
public class ClientThread extends Thread
{
    private CmdArgs cmdArgs = null;
    private org.omg.CORBA.ORB orb = null;
    private org.omg.CORBA.Object obj = null;
    private UserExceptionServer servant = null;
    private int threadNum = 0;
    private UserExceptionServerTask task = null;
    private boolean loop = false;
    private java.util.Properties props;

    public ClientThread(CmdArgs cmdArgs, java.util.Properties props, int threadNum) throws Exception
    {
        super("ClientThread");
        this.cmdArgs = cmdArgs;
        this.threadNum = threadNum;
        this.props = props;
    }

    private void init() throws Exception
    {
        try
        {
            // initialize the ORB.
            orb = ORB.init( cmdArgs.getCmdArgs(), props );

            obj = orb.string_to_object( cmdArgs.getIORString() );

            // and narrow it to the servant
            // if this fails, a BAD_PARAM will be thrown
            servant = UserExceptionServerHelper.narrow( obj );
        }
        catch (Exception e)
        {
            throw new Exception ("ClientThread " + threadNum
                    + " got an exception in init(): " + e.getMessage());
            // e.printStackTrace();
            // terminate();
        }
    }

    public void run()
    {
        do
        {
            loop = cmdArgs.getLoop();
            try {
                init();
                task = new UserExceptionServerTask("ClientThread", threadNum, cmdArgs, servant);
                task.runTest();
                terminate();
            }
            catch(Exception e)
            {
               System.err.println("ClientThread " + threadNum
                       + " got an exception in run(): " + e.getMessage());
                // e.printStackTrace();
                terminate();
            }
        } while (cmdArgs.getLoop() == true && loop == true);

    }

    public void terminate()
    {
        loop = false;
        if (task != null) {
            task.terminate();
            task = null;
        }

        if (orb != null) {
            orb.shutdown(true);
            orb = null;
        }
    }

    public org.omg.CORBA.ORB getConnectORB()
    {
        return orb;
    }

    public org.omg.CORBA.Object getConnectObject()
    {
        return obj;
    }


}
