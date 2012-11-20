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

package org.jetbrains.webdemo.common.utils.throwable

import java.io.ByteArrayOutputStream
import java.io.PrintStream

inline public val Throwable.message: String
    get() = getMessage().orEmpty()


inline public val Throwable.stackTrace: String
    get() {
        val stackTrace = ByteArrayOutputStream()
        this.printStackTrace(PrintStream(stackTrace, true))
        return stackTrace.toString()
    }