package com.skt.secretk.core.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.skt.secretk.core.enum_.Employee;
import com.skt.secretk.core.enum_.MassageType;
import com.skt.secretk.core.model.Firebase;
import com.skt.secretk.core.model.GoogleNlpResult;
import com.skt.secretk.core.model.SecretTResponse;
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
    private static final String EMPTY_MSG = "요청하신 결과가 없습니다. 다른 질문으로 요청해주세요.";

    private final GcpNlpService gcpNlpService;
    private final AlpacaService alpacaService;


    public Firebase execute(Firebase request) {
        String createTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));

        String user = request.getUser();
        String message = request.getMessage();

        // 1. 요청한 질문을 기반으로 GCP NLP 에 질의하여 단어 추출한다.
        GoogleNlpResult nlpResult = gcpNlpService.query(message);

        // 2. 요청한 질문에 사람을 지칭하는 단어가 존재하면 준비된 응답값을 가져온다.
        SecretTResponse responseResult = getPeopleMsg(nlpResult, request);
        if (responseResult != null && StringUtils.isNotBlank(responseResult.getMessage())) {

            return Firebase.builder()
                           .user(user)
                           .type("response")
                           .message(responseResult.getMessage())
                           .responseType(responseResult.getType())
                           .createTime(createTime)
                           .build();
        }

        // 3. 준비된 대답이 아닌 경우 alpaca 에 질의하여 결과를 가져온다.
        String alpacaResponse = alpacaService.query(message);

        // 4. 텍스트와 URL 을 구분하여 타입을 지정한다. (Default : text)
        String responseType = MassageType.TEXT.getName();
        if (StringUtils.isNotBlank(validUrl(alpacaResponse))) {
            responseType = MassageType.URL.getName();
        }

        return Firebase.builder()
                       .user(user)
                       .type("response")
                       .message(StringUtils.isNotBlank(alpacaResponse) ? ALPACA_PREFIX + alpacaResponse : EMPTY_MSG)
                       .responseType(responseType)
                       .createTime(createTime)
                       .build();
    }

    private SecretTResponse getPeopleMsg(GoogleNlpResult nlpResult, Firebase request) {
        if (nlpResult == null) {
            return null;
        }

        // 1. 사람 지칭 없이 명령어만 있는 경우 준비된 응답을 가져온다.
        if (CollectionUtils.isEmpty(nlpResult.getPeopleList())) {
            return matchMsg(nlpResult.getEntityList(), Employee.findById(request.getUser()));
        }

        // 2 사람 지칭이 있는 경우 반복하여 데이터를 가져온다.
        String complexTest
            = StreamUtils.ofNullable(nlpResult.getPeopleList())
                         .map(people -> {
                              String name = getPeopleName(people);

                              Employee employee = Employee.findByName(name);

                              if (employee == null) {
                                  return null;
                              }

                              return matchMsg(nlpResult.getEntityList(), employee);
                          })
                         .filter(Objects::nonNull)
                         .map(SecretTResponse::getMessage)
                         .collect(Collectors.joining(" "));

        return new SecretTResponse(complexTest, MassageType.TEXT.getName());
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

    /**
     * 준비된 응답은 6개로 구성된디. (추후 사내 API 로 전환)
     */
    private SecretTResponse matchMsg(List<String> entityList, Employee employee) {
        Map<String, SecretTResponse> dataSetMap = Maps.newLinkedHashMap(ImmutableMap.of(
            "캔미팅", new SecretTResponse("캔미팅은 경영 문제 해결과 동적요소 관리의 장으로 활용되는 우리의 SUPEX 추구의 장입니다. 통상적인 근무지를 벗어난 경우에는 표준근로시간(8시간) 근무한 것으로 간주합니다.", MassageType.TEXT.getName()),
            "식당", new SecretTResponse("오늘 구내 식당의 점심 메뉴는 비빔밥, 시래기국, 잡채, 단호박부꾸미, 청포묵김가루무침 입니다. 저녁 메뉴는 육개장, 쌀밥, 완자야채볶음, 메추리알 조림, 유채나물 입니다.", MassageType.TEXT.getName()),
            "근무", new SecretTResponse(employee.getName() + "(" +
                employee.getTeam() + ")님의 근무 시간은 " +
                employee.getWorkTime() + " 이고 " +
                employee.getOffice() + "에서 근무하고 있습니다.", MassageType.TEXT.getName()),
            "재직증명서", new SecretTResponse("https://cdn-dev.skt-asum.com/cf5eb2ac-0f43-4784-a592-344ad7aa9977.pdf", MassageType.FILE.getName()),
            "공지사항", new SecretTResponse("https://cloud.google.com/natural-language", MassageType.URL.getName()),
            "T끌",new SecretTResponse("https://github.com/tatsu-lab/stanford_alpaca#authors", MassageType.URL.getName())
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
