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

/**
 * Created with IntelliJ IDEA.
 * User: Natalia.Ukhorskaya
 * Date: 3/30/12
 * Time: 3:37 PM
 */

var ConverterProvider = (function () {

    function ConverterProvider() {

        var instance = {
            convert:function (text) {
                convert(text);
            },
            onConvert:function (text) {
            },
            onFail:function (error) {
            }
        };

        function convert(text) {
            $.ajax({
                url:generateAjaxUrl("convertToKotlin", ""),
                context:document.body,
                success:function (data) {
                    if (checkDataForNull(data)) {
                        if (checkDataForException(data)) {
                            instance.onConvert(data[0].text);
                        } else {
                            instance.onFail(data);
                        }
                    } else {
                        instance.onFail("Incorrect data format.");
                    }
                },
                dataType:"json",
                type:"POST",
                data:{text:text},
                timeout:10000,
                error:function (jqXHR, textStatus, errorThrown) {
                    instance.onFail(textStatus + " : " + errorThrown);
                }
            });
        }

        return instance;
    }


    return ConverterProvider;
})();