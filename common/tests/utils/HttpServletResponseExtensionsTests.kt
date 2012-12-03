/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.webdemo.common.utils

import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals
import org.jetbrains.webdemo.common.tests.helpers.ifCall
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

RunWith(javaClass<JUnit4>())
public class HttpServletResponseExtensionsTests {
    test fun `write something`() {
        testWrite("something data")
    }

    test fun `call write with empty string`() {
        testWrite("")
    }

    test fun `set status code`() {
        val response = mock(javaClass<HttpServletResponse>())
        val code = StatusCode.OK

        response.status(code)

        verify(response).setStatus(code.value);
    }

    test fun `set error status code`() {
        testSendError()
    }

    test fun `set error status code with message`() {
        testSendError("error message")
    }

    test fun `set int value in header`() {
        testSetHeader(3) { (name, value) -> this.setIntHeader(name, value) }
    }

    test fun `set date value in header`() {
        testSetHeader(1.toLong()) { (name, value) -> this.setDateHeader(name, value) }
    }

    test fun `set string value in header`() {
        testSetHeader("string") { (name, value) -> this.setHeader(name, value) }
    }

    test fun `add int value to header`() {
        testAddHeader(0) { (name, value) -> this.addIntHeader(name, value) }
    }

    test fun `add date value to header`() {
        testAddHeader(22.toLong()) { (name, value) -> this.addDateHeader(name, value) }
    }

    test fun `add string value to header`() {
        testAddHeader("yet another string") { (name, value) -> this.addHeader(name, value) }
    }

    private fun testWrite(data: String) {
        val response = mock(javaClass<HttpServletResponse>())
        val stringWriter = StringWriter()
        val writer = PrintWriter(stringWriter)

        ifCall(response.getWriter()).thenReturn(writer)

        response.write(data)

        assertEquals(data, stringWriter.toString())
    }

    private fun testSendError(message: String? = null) {
        val response = mock(javaClass<HttpServletResponse>())
        val code = StatusCode.BAD_REQUEST

        if (message == null){
            response.error(code)
            verify(response).sendError(code.value);
        } else{
            response.error(code, message)
            verify(response).sendError(code.value, message);
        }
    }

    private fun <T> testSetHeader(value: T, checker: HttpServletResponse.(name:String, value: T)->Unit) {
        val response = mock(javaClass<HttpServletResponse>())
        val name = "name"
        response.header(name to value)

        verify(response).checker(name, value)
    }

    private fun <T> testAddHeader(value: T, checker: HttpServletResponse.(name:String, value: T)->Unit) {
        val response = mock(javaClass<HttpServletResponse>())
        val name = "name"
        response.addHeader(name to value)

        verify(response).checker(name, value)
    }
}