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

package org.jetbrains.webdemo.server

import org.json.JSONObject

data class ErrorReport(val message: String = "",
                       val stackTrace: String = "",
                       val lastAction: String = "",
                       val description: String = "",
                       val attachment: Attachment? = null) {

    public fun toString(): String {
        fun format(name: String, value: String, indentCount: Int): String {
            if (name.isEmpty() || value.isEmpty())
                return ""

            val indent = " ".repeat(4 * indentCount)
            return "$indent'$name' : '$value'\n"
        }
        fun format(attachment: Attachment?): String {
            if (attachment == null)
                return ""
            return """ { "name" : "${attachment.name}", "content" : "${attachment.content}" }"""
        }

        val out = StringBuilder("{\n")
        out += format("message", message, 1)
        out += format("stackTrace", stackTrace, 1)
        out += format("lastAction", lastAction, 1)
        out += format("description", description, 1)
        out += format("attachment", format(attachment), 1)
        out += "}"

        return out.toString()
    }
}

fun StringBuilder.plusAssign(s: Any) {
    this.append(s)
}
