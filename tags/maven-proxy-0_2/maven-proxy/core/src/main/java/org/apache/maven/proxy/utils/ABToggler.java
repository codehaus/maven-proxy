package org.apache.maven.proxy.utils;


/*
 Copyright 2003 (C) Walding Consulting Services. All Rights Reserved.
 
 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.
 
 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.
 
 3. The name "com.walding" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of Walding Consulting Services.  For written permission,
    please contact ben@walding.com.
 
 4. Products derived from this Software may not be called "com.walding"
    nor may "com.walding" appear in their names without prior written
    permission of Walding Consulting Services. "com.walding" is a registered
    trademark of Walding Consulting Services.
 
 5. Due credit should be given to Walding Consulting Services.
    (http://www.walding.com/).
 
 THIS SOFTWARE IS PROVIDED BY WALDING CONSULTING SERVICES AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 WALDING CONSULTING SERVICES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
 
 */

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class ABToggler extends Toggler {
    private static final String[] abstates = { "a", "b" };

    //inherit JavaDoc
    public ABToggler() {
        super(abstates, 0);
    }
}
