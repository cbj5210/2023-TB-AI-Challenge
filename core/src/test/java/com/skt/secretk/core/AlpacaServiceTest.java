package com.skt.secretk.core;

import com.skt.secretk.core.properties.KeyProperties;
import com.skt.secretk.core.service.AlpacaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlpacaServiceTest {
    @Autowired
    private AlpacaService service;

    @Autowired
    private KeyProperties keyProperties;
    @Test
    public void queryTest() {
        System.out.println(service.query("오늘 메뉴 추천해줘"));
    }
}
