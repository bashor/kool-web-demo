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

package org.jetbrains.webdemo.server.ExceptionAnalyzerUtils

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import org.jetbrains.webdemo.common
import org.jetbrains.webdemo.common.utils.throwable.*
import org.jetbrains.webdemo.server.LOG
import org.jetbrains.webdemo.server.LOG_FOR_EXCEPTIONS
import org.jetbrains.webdemo.server.Settings
import org.jetbrains.webdemo.server.exception
import kotlin.test.fail


private val ENCODING = "UTF8"
private val POST_DELIMITER = "&"
private val NEW_THREAD_URL = "http://www.intellij.net/trackerRpc/idea/createScr"

private val HTTP_CONTENT_LENGTH = "Content-Length"
private val HTTP_CONTENT_TYPE = "Content-Type"
private val HTTP_WWW_FORM = "application/x-www-form-urlencoded"
private val HTTP_POST = "POST"
private val TIMEOUT = 10 * 1000

private val JAVA_VERSION = System.getProperty("java.version").orEmpty()
private val JAVA_VM_VENDOR = System.getProperty("java.vendor").orEmpty()
private val APP_NAME = "Kotlin"
private val PLUGIN_NAME = "Kool Web Demo"
private val LOGIN = "idea_anonymous"
private val PASSWORD = "guest"

public fun sendToAnalyzer(exception: Throwable,
                          lastAction: String = "",
                          description: String = "",
                          attachment: Attachment? = null) {

    sendToAnalyzer(ErrorReport(
            message = exception.message,
            stackTrace = exception.stackTrace,
            lastAction = lastAction,
            description = description,
            attachment = attachment))
}

public fun sendToAnalyzer(error: ErrorReport) {
    //fixme logging??? or assert???
    if (common.Settings.IS_TESTING) {
        fail(error.toString())
        return
    }

    LOG_FOR_EXCEPTIONS.exception(error)

    if (common.Settings.IS_PRODUCTION) {
        sendReport(error);
    }
}

private fun sendReport(error: ErrorReport) {
    try {
        postReport(error)
    } catch (e: IOException) {
        LOG_FOR_EXCEPTIONS.exception(exception = e, lastAction = "Send to Exception Analyzer")
    }
}

private fun postReport(error: ErrorReport) {
    val params: List<Pair<String, String>> = createParametersFor(error)
    val response = URL(NEW_THREAD_URL).duplexConnection().post(params.toByteArray())

    val responseCode = response.getResponseCode()
    if (responseCode != HttpURLConnection.HTTP_OK) {
        LOG_FOR_EXCEPTIONS.exception(description = "Response code $responseCode for $error")
        return
    }

    val reply = response.readContent()

    if (reply == null) {
        LOG_FOR_EXCEPTIONS.exception(description = "Can not read responce for $error}")
        return
    }

    if (reply == "unauthorized" || reply.startsWith("update ") || reply.startsWith("message ")) {
        LOG_FOR_EXCEPTIONS.exception(description = "Exception Analyzer respond $reply for $error")
        return
    }

    LOG.info("Submitted to Exception Analyzer: " + reply)

    return
}

private fun createParametersFor(error: ErrorReport): List<Pair<String, String>> {
    val date = Calendar.getInstance().format()
    val compilationTimestamp = System.currentTimeMillis().toString()

    val params = arrayListOf(
            "protocol.version" to "1",
            "user.login" to LOGIN,
            "user.password" to PASSWORD,
            "os.name" to " ",
            "java.version" to JAVA_VERSION,
            "java.vm.vendor" to JAVA_VM_VENDOR,
            "app.name" to APP_NAME,
            "app.name.full" to APP_NAME,
            "app.name.version" to APP_NAME,
            "app.eap" to "false",
            "app.build" to "Kotlin-0.0",
            "app.version.major" to Settings.KOTLIN_COMPILER_VERSION,
            "app.version.minor" to Settings.KOTLIN_COMPILER_VERSION,
            "app.build.date" to date,
            "app.build.date.release." to date,
            "app.update.channel" to "update-chanel",
            "app.compilation.timestamp" to compilationTimestamp,
            "plugin.name" to PLUGIN_NAME,
            "plugin.version" to Settings.KOTLIN_COMPILER_VERSION,
            "last.action" to error.lastAction,
            "error.message" to error.message,
            "error.stacktrace" to error.stackTrace,
            "error.description" to error.description)

    if (error.attachment != null) {
        params.addAll(listOf("attachment.name" to error.attachment.name, "attachment.value" to error.attachment.content))
    }

    return params
}