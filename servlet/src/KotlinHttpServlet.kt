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

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpUtils
import java.util.Hashtable

class KotlinHttpServlet : HttpServlet() {
    protected override fun service(request: HttpServletRequest, response: HttpServletResponse) {
        //todo fix this workaround after issue KT-2982 will be fixed
        val params = HttpUtils.parseQueryString(request.getQueryString()) as Hashtable<String, Array<String>>

        fun <T> Array<T>.firstOrDefault(default: T): T {
            if (this.isEmpty())
                return default
            return this[0]
        }

        when(params["type"]?.firstOrDefault("")) {
            //
            // processing
            //
            else -> super.service(request, response)
        }
    }
}