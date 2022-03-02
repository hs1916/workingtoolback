package com.util.workingtool.repository;

import com.util.workingtool.entity.TableList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TableListRepository extends JpaRepository<TableList, String> {
    @Query(value = "SELECT table_name " +
                   "  FROM information_schema.tables u " +
                   " WHERE u.table_schema = :schema" +
                   "   AND table_name not in ('user', 'user_authority', 'authority') ", nativeQuery = true)
    List<TableList> findBySchema(@Param("schema") String schema);
}
