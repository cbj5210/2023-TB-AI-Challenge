package com.skt.secretk.core.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleNlpResult {

    private List<String> totalEntityList;
    private List<String> peopleList;
    private List<String> entityList;
}
