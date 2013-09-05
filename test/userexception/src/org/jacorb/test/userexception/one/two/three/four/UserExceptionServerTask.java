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

//import org.jacorb.test.listenendpoints.echo_corbaloc.CmdArgs;
import org.jacorb.test.userexception.one.two.three.four.*;
import org.jacorb.test.userexception.one.two.three.four.UserExceptionServerPackage.*;

import java.io.*;

/**
 *
 * @author nguyenq
 */
public class UserExceptionServerTask
{
    private CmdArgs cmdArgs = null;
    private UserExceptionServer servant = null;
    private String threadName = "";
    private int threadNum = -1;
    private boolean terminate = false;

    public UserExceptionServerTask(String ThreadName, int threadNum, CmdArgs cmdArgs, UserExceptionServer servant)
    {
        this.cmdArgs = cmdArgs;
        this.servant = servant;
        this.threadName = ThreadName;
        this.threadNum = threadNum;
    }

    public void runTest()
    {
        if (cmdArgs == null) return;

        try
        {
            // run test
            testUserException();
            testUserNestedException();

        }
        catch (Exception e)
        {
            System.err.println("Thread " + threadNum
                                + ": got an exception in runTest(): " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Checks if a user exception is properly reported back to the client.
     */
    public void testUserException()
    {
        long tms_start = System.currentTimeMillis();
        for (int i = 0; i < 1000; ++i)
        {
            try
            {
                servant.throwMyException();
                System.err.println("FAILED: Thread " + threadNum + ": testUserException should have thrown MyException");
            }
            catch (MyException ex)
            {
                // System.out.println("PASSED: Thread " + threadNum + ": testUserException got server-side exception");
                // ex.printStackTrace();
            }
        }
        long tms_end = System.currentTimeMillis();
        long tms_dif = tms_end - tms_start;
        System.out.println("ENDOFTEST: testUserException: Invoking throwMyExceptions 1000 times took " + tms_dif + " msec");
    }


    public void testUserNestedException()
    {
        long tms_start = System.currentTimeMillis();
        for (int i = 0; i < 1000; ++i)
        {
            try
            {
                servant.throwMyNestedException();
                System.err.println("FAILED: Thread " + threadNum + ": testMyNestedException should have thrown MyNestedException");
            }
            catch(MyNestedException e)
            {
                // expected
                // System.out.println("PASSED: Thread " + threadNum + ": testMyNestedException got MyNestedException");
                // e.printStackTrace();
            }
        }
        long tms_end = System.currentTimeMillis();
        long tms_dif = tms_end - tms_start;
        System.out.println("ENDOFTEST: testUserNestedException: Invoking testMyNestedException 1000 times took " + tms_dif + " msec");
    }


    public void terminate()
    {
        terminate = true;
    }
}
