package com.googlecode.meteorframework.utils;

/*
 * org.jlense.util plugin 
 * JLense Application Framework
 * http://jlense.sourceforge.net
 * 
 * Copyright (C) 2002 Ted Stockwell, et al.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name JLense nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * <code>Assert</code> is useful for for embedding runtime sanity checks
 * in code. The static predicate methods all test a condition and throw some
 * type of unchecked exception if the condition does not hold.
 * <p>
 * Assertion failure exceptions, like most runtime exceptions, are
 * thrown when something is misbehaving. Assertion failures are invariably
 * unspecified behavior; consequently, clients should never rely on
 * these being thrown (or not thrown). <b>If you find yourself in the
 * position where you need to catch an assertion failure, you have most 
 * certainly written your program incorrectly.</b>
 * </p>
 * <p>
 * Note that an <code>assert</code> statement is slated to be added to the
 * Java language in JDK 1.4, rending this class obsolete.
 * </p>
 */
public final class Assert
{

    /**
     * <code>AssertionFailedException</code> is a runtime exception thrown
     * by some of the methods in <code>Assert</code>.
     * <p>
     * This class is not declared public to prevent some misuses; programs that catch 
     * or otherwise depend on assertion failures are susceptible to unexpected
     * breakage when assertions in the code are added or removed.
     * </p>
     */
    private static class AssertionFailedException extends RuntimeException
    {
		private static final long serialVersionUID = 1L;

		/**
         * Constructs a new exception.
         */
        public AssertionFailedException()
        {
        }

        /**
         * Constructs a new exception with the given message.
         */
        public AssertionFailedException(String detail)
        {
            super(detail);
        }
    }
    /* This class is not intended to be instantiated. */
    private Assert()
    {
    }
    /**
     * Asserts that an argument is legal. If the given boolean is
     * not <code>true</code>, an <code>IllegalArgumentException</code>
     * is thrown.
     *
     * @param expression the outcome of the check
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     * @exception IllegalArgumentException if the legality test failed
     */
    public static boolean isLegal(boolean expression)
    {
        // succeed as quickly as possible
        if (expression)
        {
            return true;
        }
        return isLegal(expression, ""); //$NON-NLS-1$
    }
    /**
     * Asserts that an argument is legal. If the given boolean is
     * not <code>true</code>, an <code>IllegalArgumentException</code>
     * is thrown.
     * The given message is included in that exception, to aid debugging.
     *
     * @param expression the outcome of the check
     * @param message the message to include in the exception
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     * @exception IllegalArgumentException if the legality test failed
     */
    public static boolean isLegal(boolean expression, String message)
    {
        if (!expression)
            throw new IllegalArgumentException();
        return expression;
    }
    /**
     * Asserts that the given object is not <code>null</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     * <p>
     * As a general rule, parameters passed to API methods must not be
     * <code>null</code> unless <b>explicitly</b> allowed in the method's
     * specification. Similarly, results returned from API methods are never
     * <code>null</code> unless <b>explicitly</b> allowed in the method's
     * specification. Implementations are encouraged to make regular use of 
     * <code>Assert.isNotNull</code> to ensure that <code>null</code> 
     * parameters are detected as early as possible.
     * </p>
     * 
     * @param object the value to test
     * @exception Throwable an unspecified unchecked exception if the object
     *   is <code>null</code>
     */
    public static void isNotNull(Object object)
    {
        // succeed as quickly as possible
        if (object != null)
        {
            return;
        }
        isNotNull(object, ""); //$NON-NLS-1$
    }
    /**
     * Asserts that the given object is not <code>null</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     * The given message is included in that exception, to aid debugging.
     * <p>
     * As a general rule, parameters passed to API methods must not be
     * <code>null</code> unless <b>explicitly</b> allowed in the method's
     * specification. Similarly, results returned from API methods are never
     * <code>null</code> unless <b>explicitly</b> allowed in the method's
     * specification. Implementations are encouraged to make regular use of 
     * <code>Assert.isNotNull</code> to ensure that <code>null</code> 
     * parameters are detected as early as possible.
     * </p>
     * 
     * @param object the value to test
     * @param message the message to include in the exception
     * @exception Throwable an unspecified unchecked exception if the object
     *   is <code>null</code>
     */
    public static void isNotNull(Object object, String message)
    {
        if (object == null)
            throw new AssertionFailedException("null argument;" + message); //$NON-NLS-1$
    }
    /**
     * Asserts that the given boolean is <code>true</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     *
     * @param expression the outcome of the check
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     */
    public static boolean isTrue(boolean expression)
    {
        // succeed as quickly as possible
        if (expression)
        {
            return true;
        }
        return isTrue(expression, ""); //$NON-NLS-1$
    }
    /**
     * Asserts that the given boolean is <code>true</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     * The given message is included in that exception, to aid debugging.
     *
     * @param expression the outcome of the check
     * @param message the message to include in the exception
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     */
    public static boolean isTrue(boolean expression, String message)
    {
        if (!expression)
            throw new AssertionFailedException("assertion failed; " + message); //$NON-NLS-1$
        return expression;
    }
}
