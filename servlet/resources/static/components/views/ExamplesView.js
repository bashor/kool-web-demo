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


var ExamplesView = (function () {
    function ExamplesView(examplesModel) {
        var confirmDialog = new ConfirmDialog();
        var model = examplesModel;

        var instance = {
            loadAllExamples:function (data) {
                addAllExamplesInAccordion(data);
                instance.onAllExamplesLoaded();
            },
            processLoadExample:function (status, data) {
                if (status) {
                    loadExampleSuccess(data);
                }
            },
            loadAllContent:function () {
                loadAllContent();
            },
            loadExample:function (url) {
                loadExample(url);
            },
            onAllExamplesLoaded:function () {
            }
        };

        function loadAllContent() {
            ExamplesView.getMainElement().html("");
            var acc = document.createElement("div");
            acc.className = "accordionForExamplesAndPrograms";
            ExamplesView.getMainElement().append(acc);
            model.getAllExamples();
        }

        function addAllExamplesInAccordion(data) {
            var acc = $("div.accordionForExamplesAndPrograms");
            addAllExamplesTo(data, acc)
        }

        function addAllExamplesTo(data, to) {
            var i = 0;
            while (data[i] != undefined) {
                var lastFolderName;
                var ids = [];
                var cont = document.createElement("div");
                if (data[i].type == "folder") {
                    var folder = document.createElement("h3");
                    var folderA = document.createElement("a");
                    folderA.href = "#";
//z                    folderA.id = replaceAll(data[i].text, " ", "_");

                    folderA.innerHTML = data[i].name;
                    lastFolderName = data[i].name;
                    folder.appendChild(folderA);
                    $(to).append(folder);
                    addAllExamplesTo(data[i].files, cont)
//z                    var cont = document.createElement("div");
                } else if (data[i].type == "file") {
                    var table = document.createElement("table");
                    var tr = document.createElement("tr");
                    var tdIcon = document.createElement("td");
                    tdIcon.style.width = "16px";
                    var span = document.createElement("div");
                    span.className = "bullet";

                    if (data[i].target == "" || data[i].target == undefined) {
                        span.style.background = "url(/static/icons/java.png) no-repeat";
                        span.style.title = "java";
                    } else {
                        if (data[i].target === "js java") {
                            data[i].target = "java js"
                        }
                        span.style.background = "url(/static/icons/" + replaceAll(data[i].target, " ", "") + ".png) no-repeat";
                        if (data[i].target == "java js") {
                            span.style.background = "Java / JavaScript";
                        } else if (data[i].target == "java") {
                            span.style.background = "Java (Standard)";
                        } else if (data[i].target == "js") {
                            span.style.background = "JavaScript (Standard)";
                        } else if (data[i].target == "canvas") {
                            span.style.background = "JavaScript (Canvas)";
                        }
                    }

                    span.id = "bullet" + replaceAll(data[i].text, " ", "_");
                    tdIcon.appendChild(span);
                    var tdContent = document.createElement("td");
                    var contA = document.createElement("a");
                    contA.id = data[i].name
                    contA.style.cursor = "pointer";
                    contA.onclick = function () {
                        loadExample(this.id);
                    };
                    contA.innerHTML = data[i].name;
                    tdContent.appendChild(contA);
                    var tdLink = document.createElement("td");
                    var linkImg = document.createElement("img");
                    linkImg.src = "/static/icons/link1.png";
                    linkImg.title = "Public link for this example";
                    linkImg.onclick = function () {
                        //in table get td with a element - id of a
                        generatePublicLinkForExample(this.parentNode.parentNode.childNodes[1].childNodes[0].id);
                    };
                    tdLink.appendChild(linkImg);

                    tr.appendChild(tdIcon);
                    tr.appendChild(tdContent);
                    tr.appendChild(tdLink);
                    table.appendChild(tr);

                    cont.appendChild(table);
                }
                $(to).append(cont);

                i++;
            }
        }

        function loadExample(url) {
            confirmDialog.open(function (url) {
                return function () {
                    if (ExamplesView.getLastSelectedItem() == url) {
                        return;
                    }

                    $("a[id='" + ExamplesView.getLastSelectedItem() + "']").attr("class", "");
                    ExamplesView.setLastSelectedItem(url);
                    $("a[id='" + ExamplesView.getLastSelectedItem() + "']").attr("class", "selectedExample");

                    model.loadExample(url);
                };
            }(url));

        }

        function loadExampleSuccess(data) {
        }


        function generatePublicLinkForExample(name) {
            if ($("div[id='pld" + name + "']").length <= 0) {
                var url = [location.protocol, '//', location.host, "/"].join('');
                var href = url + "?folder=" + name;
                $("a[id='" + name + "']").parent("td").parent("tr").parent("table").after("<div class=\"toolbox\" id=\"pld" + name + "\" style=\"display:block;\"><div align=\"center\"><div class=\"fixedpage\"><div class=\"publicLinkHref\" id=\"pl" + name + "\"></div><img class=\"closePopup\" id=\"cp" + name + "\" src=\"/static/icons/close.png\" title=\"Close popup\"></span></div></div></div>");
                $("div[id='pl" + name + "']").html(href);
                $("img[id='cp" + name + "']").click(function () {
                    $("div[id='pld" + name + "']").remove("div");
                });
            }
        }


        return instance;
    }

    ExamplesView.setLastSelectedItem = function () {
    };
    ExamplesView.getLastSelectedItem = function () {
    };
    ExamplesView.getMainElement = function () {
    };


    return ExamplesView;
})();