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
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import org.jetbrains.webdemo.server.LOG_FOR_EXCEPTIONS
import org.jetbrains.webdemo.server.exception

private fun Calendar.format(format: String = "yyyy-MM-dd") = SimpleDateFormat(format).format(this.getTime()).orEmpty()

private fun URL.duplexConnection(): HttpURLConnection {
    val connection = this.openConnection() as HttpURLConnection
    connection.setDoInput(true)
    connection.setDoOutput(true)

    connection.setReadTimeout(TIMEOUT)
    connection.setConnectTimeout(TIMEOUT)

    return connection
}

private fun HttpURLConnection.post(data: ByteArray): HttpURLConnection {
    this.setRequestMethod(HTTP_POST)
    this.setRequestProperty(HTTP_CONTENT_TYPE, "$HTTP_WWW_FORM; charset=$ENCODING")
    this.setRequestProperty(HTTP_CONTENT_LENGTH, data.size.toString())

    this.getOutputStream()!!.use<OutputStream, Unit> {
        it.write(data)
        it.flush()
    }

    return this
}

private fun HttpURLConnection.readContent(): String? {
    try {
        return this.getInputStream()?.reader()?.readText()
    } catch (e: IOException) {
        LOG_FOR_EXCEPTIONS.exception(exception = e, lastAction = "Read response")
        return null
    }
}

private fun List<Pair<String, String>>.toByteArray(): ByteArray {
    val parts = this.map {
        if (it.first.isEmpty())
            throw IllegalArgumentException(it.toString())

        if (it.second.isEmpty())
            ""
        else
            "${it.first}=${URLEncoder.encode(it.second, ENCODING)}"
    }

    return parts.makeString(POST_DELIMITER).getBytes()
}