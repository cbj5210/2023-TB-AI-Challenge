package com.skt.secretk.core.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.skt.secretk.core.enum_.Employee;
import com.skt.secretk.core.model.Firebase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreService {
    private static final String EMPTY_MSG = "요청하신 결과가 없습니다.\n다른 질문으로 요청해주세요.";

    private final GcpNlpService gcpNlpService;
    private final AlpacaService alpacaService;


    public Firebase execute(Firebase request) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String createTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String user = request.getUser();
        String message = request.getMessage();

        // 1. GCP NLP 질의하여 단어 추출
        List<String> entitiesMsg = gcpNlpService.query(message);

        // 2. entities 에 해당하는 key 가 있으면 메세지 만들어서 리턴
        String responseMsg = getEntryMsg(entitiesMsg, request);
        if (StringUtils.isNotBlank(responseMsg)) {
            String responseType = "text";
            if (responseMsg.startsWith("http")) {
                responseType = "url";
            }

            return Firebase.builder()
                           .user(user)
                           .type("response")
                           .message(responseMsg)
                           .responseType(responseType)
                           .createTime(createTime)
                           .build();
        }

        // 3. 준비된 대답이 아닌 경우 alpaca 에 질의
        String alpacaResponse = alpacaService.query(message);

        String responseType = "text";
        if (StringUtils.isNotBlank(validUrl(alpacaResponse))) {
            responseType = "url";
        }

        return Firebase.builder()
                       .user(user)
                       .type("response")
                       .message(StringUtils.isNotBlank(alpacaResponse) ? alpacaResponse : EMPTY_MSG)
                       .responseType(responseType)
                       .createTime(createTime)
                       .build();
    }

    private String getEntryMsg(List<String> entitiesMsg, Firebase request) {
        Map<String, String> dataSetMap = Maps.newHashMap(ImmutableMap.of(
            "근무", Employee.findByEmployee(request.getUser()).getName() + "(" +
                Employee.findByEmployee(request.getUser()).getTeam() + ")님의 근무 시간은 " +
                Employee.findByEmployee(request.getUser()).getWorkTime() + " 이고 " +
                Employee.findByEmployee(request.getUser()).getOffice() + "에서 근무하고 있습니다.",
            "공지사항", "https://cloud.google.com/natural-language",
            "T끌","https://github.com/tatsu-lab/stanford_alpaca#authors",
            "주차권", "https://naver.me/GeWdImSA"
        ));

        StringBuilder dataSet = new StringBuilder();
        for (String msg: entitiesMsg) {
            if (dataSetMap.containsKey(msg.toUpperCase())) {
                dataSet.append(dataSetMap.get(msg));
                dataSet.append("\n");
            }
        }

        if (StringUtils.isNotBlank(dataSet)) {
            dataSet.deleteCharAt(dataSet.lastIndexOf("\n"));
        }

        return dataSet.toString();
    }

    public static String validUrl(String msg) {
        try {
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(msg);
            if (m.find()) {
                return m.group();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
