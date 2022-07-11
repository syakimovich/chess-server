package com.github.syakimovich.chessserver.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public class GameDTO {
    @Getter
    private long id;
    @Getter
    private String creator;
    @Getter
    private String opponent;
    @Getter
    private boolean creatorWhite;
    @Getter
    private List<String> moves;
    @Getter
    private String status;
}
