package com.github.syakimovich.chessserver.entities;

import com.github.syakimovich.chessserver.utils.ListToStringConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @Getter
    private User creator;

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    @Getter
    @Setter
    private User opponent;

    @Getter
    private boolean creatorWhite;

    @Convert(converter = ListToStringConverter.class)
    @Getter
    @Setter
    private List<String> moves;

    @Getter
    @Setter
    private String status;

    public Game(User creator, boolean creatorWhite) {
        this.creator = creator;
        this.creatorWhite = creatorWhite;
    }
}
