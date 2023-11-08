var AnswerPopupModule = function () { };

AnswerPopupModule.prototype = {
    consentArr: []
    , surveyArr: []
    , useConsentPopup : false
    , isConsentDimmedPopup: false
    , useSurveyPopup: false
    , isSurveyDimmedPopup: false
    , Initialize: function () {

        if (window.localStorage.getItem('NoConsentYN') == 'Y') {
            console.log('No Consent');
            window.localStorage.removeItem('NoConsentYN');
            return;
        }

        AnswerPopup.GetMyConsentInProgress();
        //var ajax = new AjaxHelperCallBox();
        //ajax.UseLoadingMask = false;
        //ajax.URL = mspEnvPortalWebUrl + 'IF/UserIF/AnswerResponse.aspx/GetUserAnswerInfo';
        //ajax.Function_Success = function (d, s, x) {
        //    //var json = JSON.parse(d.JsonData);

        //    if (d.d.Success) {
        //        AnswerPopup.consentArr = d.d.ConsentAnswerList;
        //        AnswerPopup.surveyArr = d.d.SurveyAnswerList;

        //        if (AnswerPopup.consentArr.length > 0 && AnswerPopup.useConsentPopup) {
        //            AnswerPopup.OpenConsentViewPopup();
        //        }

        //        if (AnswerPopup.surveyArr.length > 0 && AnswerPopup.useSurveyPopup) {
        //            // TODO: 구현 필요
        //            AnswerPopup.OpenSurveyAnswerPopup();
        //        }
        //    }
        //    else { alert(d.d.Message); }
        //};
        //ajax.Function_Error = function (d, s, e) { console.error(e); };
        //ajax.Send();
    }
    , GetMyConsentInProgress: function () {
        var ajax = new AjaxHelperCallBox();
        ajax.UseLoadingMask = false;
        ajax.URL = String.format("{0}API/v1/myConsent/{1}/myConsentListInProgress", gWebInfo.ConsentFormWebURL, gWebInfo.ActivatedTenantId);
        ajax.Function_Success = function (d, s, x) {
            console.log(d)
            if (d.Success) {
                if (d.Data != undefined && d.Data != null && d.Data.length > 0) {
                    console.log("Consent Load.");
                    AnswerPopup.consentArr = d.Data;

                    AnswerPopup.OpenConsentViewPopup();
                }
            } else {
                console.log("Consent Load Is Fail.");
            }

            //    if (AnswerPopup.surveyArr.length > 0 && AnswerPopup.useSurveyPopup) {
            //        // TODO: 구현 필요
            //        AnswerPopup.OpenSurveyAnswerPopup();
            //    }
        };
        ajax.Function_Error = function (d, s, e) { console.error(e); };
        ajax.Send();
    }
    // open consent answer form popup
    , OpenConsentViewPopup: function () {
        if (AnswerPopup.consentArr.length > 0) {
            OpenConsentViewPopupInternal(this.consentArr.shift());
        }
        //for (var i = 0; i < this.consentArr.length; i++) {
        //    this.OpenConsentViewPopupInternal(this.consentArr[i]);
        //}

        function OpenConsentViewPopupInternal(consentToAnswer) {

            if (consentToAnswer == undefined || consentToAnswer == null) return;

            let moveUrl = 'Pages/ConsentForm.aspx';
            moveUrl += '?formId=' + consentToAnswer.FormID;

            var url = gWebInfo.ConsentFormWebURL + moveUrl;
            var width = 1000;
            var height = window.screen.height * 0.9;

            // Dimmed Popup
            if (AnswerPopup.isConsentDimmedPopup) {
                height = window.screen.height * 0.6;
                var popupHelper = {
                    openWindow: function (url, opt) {
                        var consentPopup = new MikWindowPopupMethod();
                        consentPopup.show(url, opt);
                        return consentPopup;
                    }
                };

                var popupOption = new Object();
                popupOption = {
                    width: width
                    , height: height
                    , functions: {
                        closed: function () {
                            AnswerPopup.CheckConsentAnswered(consentToAnswer);
                        }
                    }
                };

                var consentPopupWindow = popupHelper.openWindow(url, popupOption);
                // 2개 이상 팝업이 열릴때 대비, 옵션을 따로 지정 (지정하지 않으면 openWindow시 $.extend로 인해 마지막으로 지정한 옵션만 적용됨)
                consentPopupWindow.options = popupOption;
            }
            // window Popup
            else {
                var popupX = (window.screen.width / 2) - (width / 2);  // 팝업 가로 크기 / 2만큼 보정
                var popupY = (window.screen.height / 2) - (height / 2);  // 팝업 세로 크기 / 2만큼 보정
                var option = String.format("width={0}, height={1}, toolbar=no, left={2}, top={3}, scrollbars=yes", width, height, popupX, popupY);

                var newConsentWindow = window.open(url, 'consentAnswer' + consentToAnswer.FormId, option);

                // 팝업이 차단된 경우 window는 null
                if (newConsentWindow == null) {
                    MikMessagePopup.alert('브라우저의 팝업을 허용해야합니다.', null, function () {
                        if (consentToAnswer.NecessaryYN == 'Y') {
                            AnswerPopup.ClosePopupWithoutAnswer();
                        }
                    });
                } else {
                    newConsentWindow.onbeforeunload = function () { AnswerPopup.CheckConsentAnswered(consentToAnswer.FormId) };
                }
            }

        }
    }
    //, OpenConsentViewPopupInternal: function (consentToAnswer) {
    //    var moveUrl = 'Pages/MyConsentView.aspx';
    //    moveUrl += '?formId=' + consentToAnswer.FormId;
    //    //var url = gWebInfo.ConsentFormWebURL.replace(document.location.origin, '') + moveUrl;
    //    var url = gWebInfo.ConsentFormWebURL + moveUrl;
    //    var width = 1100;
    //    var height = window.screen.height * 0.9;
        
    //    if (this.isConsentDimmedPopup) {
    //        height = window.screen.height * 0.6;
    //        var popupHelper = {
    //            openWindow: function (url, opt) {
    //                var consentPopup = new MikWindowPopupMethod();
    //                consentPopup.show(url, opt);
    //                return consentPopup;
    //            }
    //        };

    //        var popupOption = new Object();
    //        popupOption = {
    //            width: width
    //            , height: height
    //            , functions: {
    //                closed: function () {
    //                    AnswerPopup.CheckConsentAnswered(consentToAnswer.FormId);
    //                }
    //            }
    //        };

    //        var consentPopupWindow = popupHelper.openWindow(url, popupOption);
    //        // 2개 이상 팝업이 열릴때 대비, 옵션을 따로 지정 (지정하지 않으면 openWindow시 $.extend로 인해 마지막으로 지정한 옵션만 적용됨)
    //        consentPopupWindow.options = popupOption;
    //    } else {
    //        var popupX = (window.screen.width / 2) - (width / 2);  // 팝업 가로 크기 / 2만큼 보정
    //        var popupY = (window.screen.height / 2) - (height / 2);  // 팝업 세로 크기 / 2만큼 보정
    //        var option = String.format("width={0}, height={1}, toolbar=no, left={2}, top={3}, scrollbars=yes", width, height, popupX, popupY);

    //        var newConsentWindow = window.open(url, 'consentAnswer' + consentToAnswer.FormId, option);

    //        // 팝업이 차단된 경우 window는 null
    //        if (newConsentWindow == null) {
    //            MikMessagePopup.alert('브라우저의 팝업을 허용해야합니다.', null, function () {
    //                if (consentToAnswer.NecessaryYN == 'Y') {
    //                    AnswerPopup.ClosePopupWithoutAnswer();
    //                }
    //            });
    //        } else {
    //            newConsentWindow.onbeforeunload = function () { AnswerPopup.CheckConsentAnswered(consentToAnswer.FormId) };
    //        }
    //    }
    //}
    , ConsentAnswerCallback: function (formId) {
        // 2022-09-20 필요하지 않는 로직으로 삭제.

        //for (var i = 0; i < this.consentArr.length; i++) {
        //    if (this.consentArr[i].FormId == formId) {
        //        this.consentArr[i].AnsweredYN = 'Y';
        //    }
        //}
    }
    , CheckConsentAnswered: function (consentToAnswer) {
        console.log("CheckConsentAnswered");
        // 필수 설문인 경우 
        if (!!consentToAnswer.IsNessary) {
            this.ClosePopupWithoutAnswer();
            return;
        }

        // 다음 설문 팝업
        try {
            if (AnswerPopup.consentArr.length > 0) {
                AnswerPopup.OpenConsentViewPopup();
            }
        }
        catch (e) {
            console.log(e);
        }

        //for (var i = 0; i < this.consentArr.length; i++) {
        //    if (this.consentArr[i].FormId == formId) {
        //        if (this.consentArr[i].NecessaryYN == 'Y' && this.consentArr[i].AnsweredYN != 'Y') {
        //            this.ClosePopupWithoutAnswer();
        //        }
        //    }
        //}
    }
    , ClosePopupWithoutAnswer: function () {
        gwpGlobalMenu.logout();
    }
    // open survey answer page popup
    , OpenSurveyAnswerPopup: function () {
        for (var i = 0; i < this.surveyArr.length; i++) {
            this.OpenSurveyAnswerPopupInternal(this.surveyArr[i]);
        }
    }
    , OpenSurveyAnswerPopupInternal: function (surveyToAnswer) {
        //Portal/Survey/Pages/UserAnswer/AnswerForm.aspx?s=inprogress&SurveyID={}
        var moveUrl = 'Survey/Pages/UserAnswer/AnswerForm.aspx';
        moveUrl += '?s=inprogress&SurveyID=' + surveyToAnswer.SurveyId;
        var url = gWebInfo.PortalWebURL + moveUrl;
        var width = 1100;
        var height = window.screen.height * 0.9;

        if (this.isSurveyDimmedPopup) {
            height = window.screen.height * 0.6;
            var popupHelper = {
                openWindow: function (url, opt) {
                    var surveyPopup = new MikWindowPopupMethod();
                    surveyPopup.show(url, opt);
                    return surveyPopup;
                }
            };

            var popupOption = new Object();
            popupOption = {
                width: width
                , height: height
                , functions: {
                    closed: function () {
                        AnswerPopup.CheckSurveyAnswered(surveyToAnswer.SurveyId);
                    }
                }
            };

            var surveyPopupWindow = popupHelper.openWindow(url, popupOption);
            // 2개 이상 팝업이 열릴때 대비, 옵션을 따로 지정 (지정하지 않으면 openWindow시 $.extend로 인해 마지막으로 지정한 옵션만 적용됨)
            surveyPopupWindow.options = popupOption;
        } else {
            url += '&POPCLSID='
            var popupX = (window.screen.width / 2) - (width / 2);  // 팝업 가로 크기 / 2만큼 보정
            var popupY = (window.screen.height / 2) - (height / 2);  // 팝업 세로 크기 / 2만큼 보정
            var option = String.format("width={0}, height={1}, toolbar=no, left={2}, top={3}, scrollbars=yes", width, height, popupX, popupY);

            var newSurveyWindow = window.open(url, 'surveyAnswer' + surveyToAnswer.SurveyId, option);

            // 팝업이 차단된 경우 window는 null
            if (newSurveyWindow == null) {
                MikMessagePopup.alert('브라우저의 팝업을 허용해야합니다.', null, function () {
                    if (surveyToAnswer.NecessaryYN == 'Y') {
                        AnswerPopup.ClosePopupWithoutAnswer();
                    }
                });
            } else {
                newSurveyWindow.onbeforeunload = function () { AnswerPopup.CheckSurveyAnswered(surveyToAnswer.SurveyId) };
            }
        }
    }
    , SurveyAnswerCallback: function (surveyId) {
        for (var i = 0; i < this.surveyArr.length; i++) {
            if (this.surveyArr[i].SurveyId == surveyId) {
                this.surveyArr[i].AnsweredYN = 'Y';
            }
        }
    }
    , CheckSurveyAnswered: function (surveyId) {
        for (var i = 0; i < this.surveyArr.length; i++) {
            if (this.surveyArr[i].SurveyId == surveyId) {
                if (this.surveyArr[i].NecessaryYN == 'Y' && this.surveyArr[i].AnsweredYN != 'Y') {
                    this.ClosePopupWithoutAnswer();
                }
            }
        }
    }
}

var AnswerPopup = new AnswerPopupModule();