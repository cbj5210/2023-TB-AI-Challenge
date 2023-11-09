package com.skt.secretk.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.skt.secretk.core.enum_.Employee;
import com.skt.secretk.core.model.Firebase;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class CoreServiceTest {

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
        List<String> entitiesMsg = List.of("공지사항");
        Firebase request = Firebase.builder()
                                   .user("1112340")
                                   .type("request")
                                   .message("")
                                   .build();

        Map<String, String> dataSetMap = Maps.newHashMap(ImmutableMap.of(
            "근무시간", Employee.findByEmployee(request.getUser()).getName() + "(" +
                Employee.findByEmployee(request.getUser()).getTeam() + ")님의 근무 시간은 " +
                Employee.findByEmployee(request.getUser()).getWorkTime() + " 이고" +
                Employee.findByEmployee(request.getUser()).getOffice() + "에서 근무하고 있습니다.",
            "공지사항", "https://cloud.google.com/natural-language",
            "T끌", "https://github.com/tatsu-lab/stanford_alpaca#authors"
        ));

        Map<String, String> resultMap = Maps.newHashMap();
        for (String msg: entitiesMsg) {
            if (dataSetMap.containsKey(msg)) {
                resultMap.put(msg, dataSetMap.get(msg));
            }
        }
        System.out.println("resultMap = " + resultMap);
    }
}
