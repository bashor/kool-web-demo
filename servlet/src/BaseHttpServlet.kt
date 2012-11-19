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

package org.jetbrains.webdemo.servlet

import java.net.URLDecoder
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpUtils
import org.jetbrains.webdemo.common.utils.StatusCode
import org.jetbrains.webdemo.common.utils.write
import org.jetbrains.webdemo.common.utils.status
import org.jetbrains.webdemo.common.utils.header
import java.io.UnsupportedEncodingException

abstract class BaseHttpServlet : HttpServlet() {
    abstract protected fun handle(command: String, params: Map<String, Array<String>>): String?

    protected override fun service(request: HttpServletRequest, response: HttpServletResponse) {
        response.header("Cache-Control" to "no-cache")

        //fixme after issue KT-2982 will be fixed
        val decodedQuery = try {
            URLDecoder.decode(request.getQueryString(), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            val exceptionMessage = e.getMessage().orEmpty()
            val message = if (exceptionMessage.contains("URLDecoder:")) {
                exceptionMessage
            } else {
                "${exceptionMessage} character encoding is not supported"
            }
            response.status(StatusCode.BAD_REQUEST, message)
            return
        }

        val params = try {
            HttpUtils.parseQueryString(decodedQuery) as Map<String, Array<String>>
        } catch (e: IllegalArgumentException) {
            response.status(StatusCode.BAD_REQUEST, "The query string is invalid")
            return
        }

        val command = params["do"]

        if (command != null && command.notEmpty()) {
            val responseBody = try {
                handle(command[0], params)
            } catch (e: Throwable) {
                //Do not stop server
                //todo write to log
//                ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e,
//                        "UNKNOWN", param);
                response.status(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error")
                return
            }

            if (responseBody == null) {
                response.status(StatusCode.BAD_REQUEST, "Unsupported command")
                return
            }

            if (!response.status(StatusCode.OK).write(responseBody)) {
                response.status(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        } else {
            response.status(StatusCode.BAD_REQUEST, "The Parameter \"do\" is not found or empty")
        }
    }
}