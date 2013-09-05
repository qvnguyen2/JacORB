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
 * Description:
 *    A simple client request interceptor implementation.
 *******************************************************************************
 */

/**
 *   A simple client request interceptor implementation.
 */
class ClientRequestInterceptorImpl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.ClientRequestInterceptor {


    private static final String NAME = "CRI";



    ClientRequestInterceptorImpl() {

        super();
        //log("ClientRequestInterceptorImpl: Entering method...");
    }

    public void destroy() {

        //log("destroy: Entering method...");
    }

    public String name() {

        //log("name: Entering method...");

        return NAME;
    }

    public void receive_exception(
            org.omg.PortableInterceptor.ClientRequestInfo ri)
        throws org.omg.PortableInterceptor.ForwardRequest {

        //log("receive_exception: Entering method...");

    }

    public void receive_other(org.omg.PortableInterceptor.ClientRequestInfo ri)
        throws org.omg.PortableInterceptor.ForwardRequest {

        //log("receive_other: Entering method...");
    }

    public void receive_reply(
            org.omg.PortableInterceptor.ClientRequestInfo ri) {

        //log("receive_reply: Entering method...");
    }

    public void send_poll(org.omg.PortableInterceptor.ClientRequestInfo ri) {

        //log("send_poll: Entering method...");
    }

    public void send_request(org.omg.PortableInterceptor.ClientRequestInfo ri)
            throws org.omg.PortableInterceptor.ForwardRequest {

        //log("send_request: Entering method...");
    }

    private void log(String msg)
    {
        System.out.println("ClientRequestInterceptorImpl." + msg);
        System.out.flush();
    }
}
