package com.sentimentdiary.demo.diary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sentimentdiary.demo.auditable.Auditable;
import com.sentimentdiary.demo.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Diary extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long diaryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private int emotion;

    @Column
    @ElementCollection
    private List<String> keywords = new LinkedList<>();

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
