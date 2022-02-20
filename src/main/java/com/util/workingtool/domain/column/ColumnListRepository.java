package com.util.workingtool.domain.column;

import com.util.workingtool.domain.table.TableList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColumnListRepository extends JpaRepository<ColumnList, String> {
    @Query(value = "SELECT column_Name," +
            " data_Type, column_Type, column_Comment   " +
            "FROM information_schema.columns u WHERE u.table_schema = :schema " +
            "and u.table_name = :table", nativeQuery = true)
    List<ColumnList> findBySchemaAndTable(@Param("schema") String schema, @Param("table") String table);
}
