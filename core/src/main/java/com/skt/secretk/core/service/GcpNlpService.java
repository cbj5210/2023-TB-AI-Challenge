package com.skt.secretk.core.service;

import com.skt.secretk.core.model.GoogleNlp;
import com.skt.secretk.core.model.GoogleNlp.Entity;
import com.skt.secretk.core.util.StreamUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@RequiredArgsConstructor
public class GcpNlpService {

    //TODO : 최병준
    public List<String> query(String message) {

        try {
            File file = ResourceUtils.getFile("classpath:googleNplApiKey.txt");
            Scanner scanner = new Scanner(file);
            String apiKey = scanner.nextLine();

            // todo : api call

            // for test
            GoogleNlp googleNlp = new GoogleNlp();

            return StreamUtils.ofNullable(googleNlp.getEntities())
                             .map(Entity::getName)
                             .toList();


        } catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }
}
