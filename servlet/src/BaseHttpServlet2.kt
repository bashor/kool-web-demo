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

/**
 * Created by IntelliJ IDEA.
 * User: Zalim Bahsorov
 * Date: 10/23/12
 * Time: 9:45 PM
 */

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpUtils
import java.util.Hashtable

import org.jetbrains.webdemo.common.utils.firstOrDefault


trait BaseHttpServlet2 : HttpServlet {
    private inline fun handler(f: (params: Map<String, Array<String>>, response: HttpServletResponse) -> Unit) = f

    val handlers : Map<String, (params: Map<String, Array<String>>, response: HttpServletResponse) -> Unit>

    protected override fun service(request: HttpServletRequest, response: HttpServletResponse) {
        //todo fix this workaround after issue KT-2982 will be fixed
        val params = request.getParameterMap() as Map<String, Array<String>>

        println("${request.getQueryString()} sid==${request.getSession()?.getId()} isNew==${request.getSession()?.isNew()}")

        val commands = params["do"]

        if (commands != null && !commands.isEmpty()) {
            handlers[commands[0]]?.invoke(params, response)
            return
        }

        //todo bad request

    }
}