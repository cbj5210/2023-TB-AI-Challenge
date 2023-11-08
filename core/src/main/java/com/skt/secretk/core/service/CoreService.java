package com.skt.secretk.core.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.skt.secretk.core.enum_.Employee;
import com.skt.secretk.core.model.Firebase;
import com.skt.secretk.core.model.GoogleNlpResult;
import com.skt.secretk.core.util.StreamUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CoreService {
    private static final String ALPACA_PREFIX = "요청하신 내용의 답변을 시크릿 T는 찾지 못했어요.. 하지만 친구인 Alpaca가 알려준 답변을 전달 드립니다. ";
    private static final String EMPTY_MSG = "요청하신 결과가 없습니다.\n다른 질문으로 요청해주세요.";

    private final GcpNlpService gcpNlpService;
    private final AlpacaService alpacaService;


    public Firebase execute(Firebase request) {
        String createTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));

        String user = request.getUser();
        String message = request.getMessage();

        // 1. GCP NLP 질의하여 단어 추출
        GoogleNlpResult nlpResult = gcpNlpService.query(message);

        // 2. entities 에 해당하는 key 가 있으면 메세지 만들어서 리턴
        String responseMsg = getEntryMsg(nlpResult, request);
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
                       .message(StringUtils.isNotBlank(alpacaResponse) ? ALPACA_PREFIX + alpacaResponse : EMPTY_MSG)
                       .responseType(responseType)
                       .createTime(createTime)
                       .build();
    }

    private String getEntryMsg(GoogleNlpResult nlpResult, Firebase request) {
        if (nlpResult == null) {
            return null;
        }

        // 사람 지칭 없이 명령어만 있는 경우
        if (CollectionUtils.isEmpty(nlpResult.getPeopleList())) {
            return match(nlpResult.getEntityList(), Employee.findById(request.getUser()));
        }

        // 사람 지칭이 있는 경우 반복
        return StreamUtils.ofNullable(nlpResult.getPeopleList())
                          .map(people -> {
                              String name = getPeopleName(people);

                              Employee employee = Employee.findByName(name);

                              if (employee == null) {
                                  return null;
                              }

                              return match(nlpResult.getEntityList(), employee);
                          })
                          .filter(Objects::nonNull)
                          .collect(Collectors.joining(" "));
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

    private String getPeopleName (String name) {
        if (name.endsWith("님") || name.endsWith("씨")) {
            return name.substring(0, name.length()-1);
        }

        return name;
    }

    private String match(List<String> entityList, Employee employee) {
        Map<String, String> dataSetMap = Maps.newHashMap(ImmutableMap.of(
            "근무", employee.getName() + "(" +
                employee.getTeam() + ")님의 근무 시간은 " +
                employee.getWorkTime() + " 이고 " +
                employee.getOffice() + "에서 근무하고 있습니다.",
            "공지사항", "https://cloud.google.com/natural-language",
            "T끌","https://github.com/tatsu-lab/stanford_alpaca#authors"
        ));

        List<String> newEntityList = StreamUtils.ofNullable(entityList)
                                                .map(entity -> entity.replaceAll(" ", ""))
                                                .toList();

        for (String key : dataSetMap.keySet()) {
            if (newEntityList.contains(key)) {
                return dataSetMap.get(key);
            }
        }

        return null;
    }
}
