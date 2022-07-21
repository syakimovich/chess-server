package com.github.syakimovich.chessserver.entities;

import com.github.syakimovich.chessserver.consts.DrawStatus;
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
    @Column(length = 100000)
    private List<String> moves;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private DrawStatus drawStatus;

    public Game(User creator, boolean creatorWhite) {
        this.creator = creator;
        this.creatorWhite = creatorWhite;
    }

    public User getWhiteUser() {
        if (isCreatorWhite()) {
            return getCreator();
        } else {
            return getOpponent();
        }
    }

    public User getBlackUser() {
        if (isCreatorWhite()) {
            return getOpponent();
        } else {
            return getCreator();
        }
    }
}
