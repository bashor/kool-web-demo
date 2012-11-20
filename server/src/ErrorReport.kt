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

data class ErrorReport(val lastAction: String = "",
                       val message: String = "",
                       val stackTrace: String = "",
                       val description: String = "",
                       val attachment: String = "")

fun ErrorReport.toJsonString(): String {
    val map = hashMap(
            "last action" to this.lastAction,
            "plugin version" to Settings.KOTLIN_COMPILER_VERSION,
            "message" to this.message,
            "stackTrace" to this.stackTrace,
            "description" to this.description)

    return  JSONObject(map).toString()
}
