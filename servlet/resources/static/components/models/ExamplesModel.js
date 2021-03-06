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

var ExamplesModel = (function () {

    function ExamplesModel() {

        var instance = {
            loadExample:function (url) {
                $.ajax({
                    url:"/webIde?do=loadExample&name=" + url,
                    context:document.body,
                    success:function (data) {
                        if (checkDataForNull(data)) {
                            if (checkDataForException(data)) {
                                instance.onLoadExample(data);
                            } else {
                                instance.onFail(data, ActionStatusMessages.load_example_fail);
                            }
                        } else {
                            instance.onFail("Incorrect data format.", ActionStatusMessages.load_example_fail);
                        }
                    },
                    dataType:"json",
                    type:"GET",
                    timeout:10000,
                    error:function (jqXHR, textStatus, errorThrown) {
                        instance.onFail(textStatus + " : " + errorThrown, ActionStatusMessages.load_example_fail);
                    }
                });
            },
            getAllExamples:function () {
                getAllExamples();
            },
            onAllExamplesLoaded:function (data) {
            },
            onLoadExample:function (data) {
            },
            onFail:function (exception, statusBarMessage) {
            }
        };

        function getAllExamples() {
            $.ajax({
                url:"/webIde?do=loadExamplesList",
                context:document.body,
                success:function (data) {
                    if (checkDataForNull(data)) {
                        if (checkDataForException(data)) {
                            instance.onAllExamplesLoaded(data);
                        } else {
                            instance.onFail(data, ActionStatusMessages.load_examples_fail);
                        }
                    } else {
                        instance.onFail("Incorrect data format.", ActionStatusMessages.load_examples_fail);
                    }
                },
                dataType:"json",
                type:"GET",
                timeout:10000,
                error:function (jqXHR, textStatus, errorThrown) {
                    instance.onFail(textStatus + " : " + errorThrown, ActionStatusMessages.load_examples_fail);
                }
            });
        }


        return instance;
    }

    return ExamplesModel;
})();