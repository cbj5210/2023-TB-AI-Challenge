package com.skt.secretk.core.enum_;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum Employee {
    JJS("1111111", "정주상", "Camping Platform", "07:00 ~ 17:00", "판교"),
    CBJ("2222222", "최병준", "Camping Platform", "09:00 ~ 19:00", "판교"),
    JMB("3333333", "주민범", "Camping Platform", "07:00 ~ 18:00", "신도림"),
    CHH("5555555", "최하혁", "Camping Platform", "06:00 ~ 14:00", "판교"),
    KYR("6666666", "김영래", "Camping Platform", "07:00 ~ 18:00", "수내");

    private final String id;
    private final String name;
    private final String team;
    private final String workTime;
    private final String office;

    public static Employee findById(String id) {
        return Arrays.stream(Employee.values())
                     .filter(employee -> StringUtils.equalsIgnoreCase(employee.getId(), id))
                     .findFirst()
                     .orElse(null);
    }

    public static Employee findByName(String name) {
        return Arrays.stream(Employee.values())
                     .filter(employee -> StringUtils.equalsIgnoreCase(employee.getName(), name))
                     .findFirst()
                     .orElse(null);
    }
}
