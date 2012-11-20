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

fun Iterable<String>.join(separator: String): String {
    val result = StringBuilder()

    val it = this.iterator()

    if (!it.hasNext())
        return ""

    result.append(it.next())

    while(it.hasNext()) {
        result.append(separator)
        result.append(it.next())
    }

    return result.toString()
}

//todo implementation?
inline public fun String.stripMargin(marginChar: Char = '|'): String {
    return this.replaceFirst("""^[^\$marginChar]*\$marginChar""", "").replaceAll("""\n[^\$marginChar]*\$marginChar""", "\n")
    //    return StringReader(this).useLines {
    //        it.map {
    //            val marginPos = it.indexOf(marginChar)
    //            if (marginPos != -1)
    //                it.substring(marginPos)
    //            else
    //                it
    //        }
    //    }.fold(StringBuilder()) {(builder, str)-> builder.append(str)}.toString()
}