package com.util.workingtool.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColumnListRepository extends JpaRepository<ColumnList, String> {
    @Query(value = "SELECT column_Name," +
            " data_Type, column_Type, column_Comment, is_nullable, table_schema  " +
            "FROM information_schema.columns u WHERE u.table_schema = :schema " +
            "and u.table_name = :table " +
            "order by ordinal_position", nativeQuery = true)
    List<ColumnList> findBySchemaAndTable(@Param("schema") String schema, @Param("table") String table);
}
