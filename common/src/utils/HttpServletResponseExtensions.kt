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

import javax.servlet.http.HttpServletResponse

inline public fun HttpServletResponse.write(body: String): Boolean {
    this.getWriter() use {
        it.write(body)
    }
    return this.getWriter().checkError()
}

inline public fun HttpServletResponse.status(statusCode: StatusCode, message: String? = null): HttpServletResponse {
    if (message == null)
        this.setStatus(statusCode.value)
    else
        this.setStatus(statusCode.value, message)
    return this
}

public fun HttpServletResponse.header(vararg headers: Pair<String, Any>): HttpServletResponse {
    for ((name, value) in headers) {
        when (value) {
            is Int -> this.setIntHeader(name, value)
            is Long -> this.setDateHeader(name, value)
            else -> this.setHeader(name, value.toString())
        }
    }
    return this
}

public fun HttpServletResponse.addHeader(vararg headers: Pair<String, Any>): HttpServletResponse {
    for ((name, value) in headers) {
        when (value) {
            is Int -> this.addIntHeader(name, value)
            is Long -> this.addDateHeader(name, value)
            else -> this.addHeader(name, value.toString())
        }
    }
    return this
}