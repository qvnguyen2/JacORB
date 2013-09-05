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

/*******************************************************************************
 * Description      :
 *   The ORBInitializer registers PIs.
 *******************************************************************************
 */

public class ORBInitializerImpl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.ORBInitializer
{
    // for logging
    //private Object thisTestCase_ = null;


    public ORBInitializerImpl()
    {
    }

    //public static void setTestCase(Object tc)
    //{
    //    thisTestCase_ = tc;
    //}

    public void pre_init(org.omg.PortableInterceptor.ORBInitInfo info)
    {
    }

    public void post_init(org.omg.PortableInterceptor.ORBInitInfo info)
    {
        try
        {

            //log("post_init: Registering PIs...");

            org.omg.PortableInterceptor
                    .ClientRequestInterceptor clientRequestInterceptor =
                            new org.jacorb.test.userexception.one.two.three.four.ClientRequestInterceptorImpl();

            info.add_client_request_interceptor(clientRequestInterceptor);

            //log(methodName + " PIs have been registered!");

        }
        catch (java.lang.Throwable ex)
        {
            logException("post_init: Caught exception while registering PIs", ex);
        }
    }

    private void log(String msg)
    {
        System.out.println("ORBInitializerImpl." + msg);
        System.out.flush();
    }

    private void logException(String message, java.lang.Throwable thex) {

        log("ORBInitializerImpl." + message);
        thex.printStackTrace();
        System.out.flush();
    }
}
