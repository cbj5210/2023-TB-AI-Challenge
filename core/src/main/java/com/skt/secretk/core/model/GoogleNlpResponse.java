package com.skt.secretk.core.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleNlpResponse {

    private List<Entity> entities;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {

        private String name;
        private String type;
        private Map<String, String> metadata;
        private List<EntityMention> mentions;
        private EntitySentiment sentiment;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EntityMention {

            private EntityMentionText text;
            private String type;
            private Double probability;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            public static class EntityMentionText {

                private String content;
                private Integer beginOffset;
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EntitySentiment {

            private Double magnitude;
            private Double score;
        }
    }
}
