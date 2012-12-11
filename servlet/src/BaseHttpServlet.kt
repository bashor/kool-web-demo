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

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpUtils
import org.jetbrains.webdemo.common.utils.StatusCode
import org.jetbrains.webdemo.common.utils.header
import org.jetbrains.webdemo.common.utils.status
import org.jetbrains.webdemo.common.utils.write
import org.jetbrains.webdemo.common.utils.error
import org.jetbrains.webdemo.server.ExceptionAnalyzerUtils.sendToAnalyzer
import javax.naming.InitialContext
import javax.naming.Context
import javax.naming.NamingException
import org.jetbrains.webdemo.server.Settings.constants.*
import org.jetbrains.webdemo.common.Settings
import javax.servlet.ServletConfig

abstract class BaseHttpServlet: HttpServlet() {

    //todo replace to init? need single init!
    {
        val envContext = InitialContext().lookup("java:comp/env") as Context

        val appHome = try {
            envContext.lookup(PROP_APP_HOME) as String
        } catch (e: NamingException) {
            sendToAnalyzer(exception = e, lastAction = "Lookup app_home in Servlet's context")
            ""
        }

        Settings.setProperty(PROP_APP_HOME, appHome)
        Settings.setProperty(PROP_LOG4J, appHome)
    }

    abstract protected fun handle(command: String, params: Map<String, Array<String>>): String?

    protected override fun service(request: HttpServletRequest, response: HttpServletResponse) {
        response.header("Cache-Control" to "no-cache")

        //fixme after issue KT-2982 will be fixed
        val decodedQuery =
                try {
                    val query = request.getQueryString()
                    if (query == null) {
                        response.error(StatusCode.BAD_REQUEST, "The URL does not contain query string.")
                        return
                    }

                    URLDecoder.decode(query, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    //todo extract
                    val exceptionMessage = e.getMessage().orEmpty()
                    val message =
                            if (exceptionMessage.contains("URLDecoder:")) {
                                exceptionMessage
                            } else {
                                "${exceptionMessage} character encoding is not supported"
                            }
                    //todo logging
                    response.error(StatusCode.BAD_REQUEST, message)
                    return
                }

        val params =
                try {
                    HttpUtils.parseQueryString(decodedQuery) as Map<String, Array<String>>
                } catch (e: IllegalArgumentException) {
                    response.error(StatusCode.BAD_REQUEST, "The query string is invalid")
                    return
                }

        val command = params["do"]

        if (command != null && command.notEmpty()) {
            val responseBody =
                    try {
                        handle(command[0], params)
                    } catch (e: Throwable) {
                        //Do not stop server
                        sendToAnalyzer(exception = e, description = "QueryString: $decodedQuery", lastAction = "Call handler")
                        response.error(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error")
                        return
                    }

            if (responseBody == null) {
                response.error(StatusCode.BAD_REQUEST, "Unsupported command")
                return
            }

            if (!response.status(StatusCode.OK).write(responseBody)) {
                response.error(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error")
            }
        } else {
            response.error(StatusCode.BAD_REQUEST, "The Parameter \"do\" is not found or empty")
        }
    }
}