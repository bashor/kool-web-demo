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

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import org.jetbrains.webdemo.common
import org.jetbrains.webdemo.common.utils.throwable.*

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
//fixme don't try send report if it's happened in test mode
public fun sendToAnalyzer(error: ErrorReport) {
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

    val params = arrayList("protocol.version" to "1",
            Pair("user.login", LOGIN),
            Pair("user.password", PASSWORD),
            Pair("os.name", " "),
            Pair("java.version", JAVA_VERSION),
            Pair("java.vm.vendor", JAVA_VM_VENDOR),
            Pair("app.name", APP_NAME),
            Pair("app.name.full", APP_NAME),
            Pair("app.name.version", APP_NAME),
            Pair("app.eap", "false"),
            Pair("app.build", "Kotlin-0.0"),
            Pair("app.version.major", Settings.KOTLIN_COMPILER_VERSION),
            Pair("app.version.minor", Settings.KOTLIN_COMPILER_VERSION),
            Pair("app.build.date", date),
            Pair("app.build.date.release.", date),
            Pair("app.update.channel", "update-chanel"),
            Pair("app.compilation.timestamp", compilationTimestamp),
            Pair("plugin.name", PLUGIN_NAME),
            Pair("plugin.version", Settings.KOTLIN_COMPILER_VERSION),
            Pair("last.action", error.lastAction),
            Pair("error.message", error.message),
            Pair("error.stacktrace", error.stackTrace),
            Pair("error.description", error.description))

    if (error.attachment != null) {
        params.addAll(arrayList(Pair<String, String>("attachment.name", error.attachment.name),
                Pair("attachment.value", error.attachment.content)))
    }

    return params
}