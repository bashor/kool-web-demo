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

import org.apache.log4j.Logger
import org.jetbrains.webdemo.common.utils.throwable.*

public val LOG_FOR_EXCEPTIONS: Logger = Logger.getLogger("exceptionLogger");
public val LOG: Logger = Logger.getLogger("infoLogger")

fun Logger.exception(exception: Throwable,
                   lastAction: String = "",
                   description: String = "",
                   attachment: Attachment? = null) {


    this.exception(ErrorReport(
            lastAction = lastAction,
            message = exception.message,
            stackTrace = exception.stackTrace,
            description = description,
            attachment = attachment))
}

fun Logger.exception(lastAction: String = "",
                   message: String = "",
                   stackTrace: String = "",
                   description: String = "",
                   attachment: Attachment? = null) {

    this.exception(ErrorReport(
            lastAction = lastAction,
            message = message,
            stackTrace = stackTrace,
            description = description,
            attachment = attachment))
}

fun Logger.exception(error: ErrorReport) {
    this.error(error)
}