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

package org.jetbrains.webdemo.common.utils.json

import org.jetbrains.webdemo.common.*
import org.json.JSONArray
import org.json.JSONObject

inline fun <T> Collection<T>.toJsonString(): String {
    val json = JSONArray()
    for (el in this) {
        json.put(el)
    }
    return json.toString().orEmpty()
}

inline fun Example.toJsonString(): String {
    val result = JSONObject()

    result.put(NAME_PROP, this.name)
    result.put(TEXT_PROP, this.text)
    result.put(TARGET_PROP, this.targets.toList().map { it.toString().toLowerCase() }.makeString(" "))
    result.put(ARGS_PROP, this.args)
    result.put(SOURCE_PROP, this.source)

    return result.toString().orEmpty()
}