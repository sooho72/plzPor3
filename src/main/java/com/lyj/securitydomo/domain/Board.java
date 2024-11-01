package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Getter
@Setter
@Entity(name="tbl_board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;
    private String title;
    private String writer;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="regdate")
    private Date regdate;

    @ColumnDefault("0")
    private Long hitcount;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            optional = true)
    @JoinColumn(name="user_id")
    private User user;

    public void updateHitcount() {

        this.hitcount = this.hitcount+1;
    }
}