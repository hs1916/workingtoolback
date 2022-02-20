package com.util.workingtool.domain.table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "information_schema.tables")
public class TableList {

    @Id
    String tableName;

}
