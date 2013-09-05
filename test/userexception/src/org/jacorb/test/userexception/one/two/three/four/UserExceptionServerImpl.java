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

import org.jacorb.test.userexception.one.two.three.four.*;
import org.jacorb.test.userexception.one.two.three.four.UserExceptionServerPackage.*;


public class UserExceptionServerImpl extends UserExceptionServerPOA
{

    public UserExceptionServerImpl()
    {
    }

    public void throwMyException() throws MyException
    {
        throw new MyException();
    }

    public void throwMyNestedException() throws MyNestedException
    {
        throw new MyNestedException();
    }
}
