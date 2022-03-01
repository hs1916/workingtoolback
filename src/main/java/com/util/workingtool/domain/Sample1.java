package com.util.workingtool.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "sample")
public class Sample1 {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private String col1;

    @Column
    private String col2;

    @Column
    private String col3;

    @Column
    private String col4;

    @Column
    private String col5;

    @Builder
    public Sample1(String col1, String col2, String col3, String col4, String col5) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
    }
}
