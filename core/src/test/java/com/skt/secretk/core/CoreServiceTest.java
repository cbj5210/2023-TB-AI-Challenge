package com.skt.secretk.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.skt.secretk.core.enum_.Employee;
import com.skt.secretk.core.model.Firebase;
import com.skt.secretk.core.service.GcpNlpService;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CoreServiceTest {

    @Autowired
    private GcpNlpService gcpNlpService;

    @Test
    public void url_test() {
        //String content = "https://github.com/tatsu-lab/stanford_alpaca#authors";
        String content = "테스트 https://github.com/tatsu-lab/stanford_alpaca#authors";
        try {
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find()) {
                System.out.println("url = " + m.group());
            }
        } catch (Exception e) {
            System.out.println("error = " + e.getMessage());
        }
    }

    @Test
    public void employee_test() {
        List<String> entitiesMsg = List.of("근무시간", "T끌", "나무", "t꿀");
        Firebase request = Firebase.builder()
                                   .user("1112340")
                                   .type("request")
                                   .message("")
                                   .build();

        Map<String, String> dataSetMap = Maps.newHashMap(ImmutableMap.of(
            "근무시간", Employee.findByEmployee(request.getUser()).getName() + "(" +
                Employee.findByEmployee(request.getUser()).getTeam() + ")님의 근무 시간은 " +
                Employee.findByEmployee(request.getUser()).getWorkTime() + " 이고 " +
                Employee.findByEmployee(request.getUser()).getOffice() + "에서 근무하고 있습니다.",
            "공지사항", "https://cloud.google.com/natural-language",
            "T끌", "https://github.com/tatsu-lab/stanford_alpaca#authors",
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

        System.out.println("dataSet = " + dataSet.toString());
    }

    @Test
    @Disabled
    public void nlp_query_test() {
        // String query = "정주상님 근무시간 알려줘"; // result : 정주상님, 근무
        // String query = "공지사항 알려줘"; // result : 공지사항
        // String query = "t끌 열어줘"; // result : 티끌 (T끌 도 추가필요)
        String query = "주차권 신청 페이지 알려줘"; // result : 주차권, 신청 페이지
        List<String> response = gcpNlpService.query(query);

        for (String result: response) {
            System.out.println("response : " + result);
        }
    }
}
