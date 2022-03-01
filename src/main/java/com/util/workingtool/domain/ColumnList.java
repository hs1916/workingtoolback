package com.util.workingtool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "information_schema.columns")
public class ColumnList {

    @Id
    String columnName;

    @Column
    String dataType;

    @Column
    String columnType;

    @Column
    String columnComment;

    @Column
    String isNullable;

    @Column
    String tableSchema;
}
