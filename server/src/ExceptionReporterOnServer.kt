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

import org.jetbrains.jet.internal.com.intellij.errorreport.itn.ITNProxy
import org.jetbrains.jet.internal.com.intellij.errorreport.bean.ErrorBean
import org.jetbrains.jet.internal.com.intellij.diagnostic.errordialog.Attachment
import org.jetbrains.webdemo.common.ExceptionReporter
import org.jetbrains.webdemo.common

trait ExceptionReporterOnServer: ExceptionReporter {
    override fun report(exception: Throwable, lastAction: String, attach: String ) {
        val bean = ErrorBean(exception, lastAction);
        bean.setPluginName(common.PLUGIN_NAME);
        bean.setAttachments(arrayList(Attachment("Example.kt", attach)));
        if (common.Settings.IS_PRODUCTION) {
            sendViaITNProxy(bean);
        }
        //todo log
        //        LOG_FOR_EXCEPTIONS.error(ErrorWriter.getExceptionForLog(type, e, description))
    }

    private fun sendViaITNProxy(error: ErrorBean) {
        val login = "idea_anonymous"
        val password = "guest"
        try {
            val result = ITNProxy.postNewThread(login, password, error, System.currentTimeMillis().toString(), Settings.KOTLIN_COMPILER_VERSION)
            if (result == "unauthorized" || result == "update " || result == "message ") {
                //todo log
                //LOG_FOR_EXCEPTIONS.error(getExceptionForLog("SEND_TO_EA", result, ""))
                //LOG_FOR_EXCEPTIONS.error(getExceptionForLog(error.getLastAction(), error.getMessage(), error.getDescription()))
            } else {
                //todo
                //LOG_FOR_INFO.info("Submitted to Exception Analyzer: " + result)
            }
        } catch (e: IOException) {
            //todo log
            //LOG_FOR_EXCEPTIONS.error(getExceptionForLog("SEND_TO_EXCEPTION_ANALYZER", e1, login))
        }
    }
}