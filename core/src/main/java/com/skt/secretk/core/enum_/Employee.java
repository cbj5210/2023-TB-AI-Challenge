package com.skt.secretk.core.enum_;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum Employee {
    JJS("1112340", "정주상", "Camping Platform", "07:00 ~ 17:00", "판교"),
    CBJ("1112341", "최병준", "Camping Platform", "09:00 ~ 19:00", "판교"),
    JMB("1112342", "주민범", "Camping Platform", "07:00 ~ 18:00", "신도림"),
    CHH("1112343", "최하혁", "Camping Platform", "06:00 ~ 14:00", "판교"),
    KYR("1112344", "김영래", "Camping Platform", "07:00 ~ 18:00", "수내");

    private final String idNumber;
    private final String name;
    private final String team;
    private final String workTime;
    private final String office;

    public static Employee findByEmployee(String user) {
        return Arrays.stream(Employee.values())
                     .filter(idNo -> StringUtils.equalsIgnoreCase(user, idNo.getIdNumber()))
                     .findFirst()
                     .orElse(null);
    }
}
