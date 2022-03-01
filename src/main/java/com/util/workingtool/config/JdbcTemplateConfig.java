package com.util.workingtool.config;

import com.util.workingtool.domain.ColumnList;
import com.util.workingtool.domain.ColumnListRepository;
import com.util.workingtool.domain.Sample1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Slf4j
@Component
public class JdbcTemplateConfig {

    @Autowired
    private ColumnListRepository columnListRepository;

    @Value("${meta.default-schema}")
    private String DEFAULT_SCHEMA;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Sample1> select(String query) {

        List<Sample1> result = jdbcTemplate.query("select * from sample", new RowMapper<Sample1>() {
            @Override
            public Sample1 mapRow(ResultSet rs, int rowNum) throws SQLException {
                Sample1 sample = Sample1.builder()
                        .col1(rs.getString("col1"))
                        .col2(rs.getString("col2"))
                        .col3(rs.getString("col3"))
                        .col4(rs.getString("col4"))
                        .col5(rs.getString("col5"))
                        .build();
                return sample;
            }
        });

        return result;
    }

    public boolean update(String col1, String col2) {
        Integer rtn = jdbcTemplate.update("Insert into sample ( col1, col2) values ( ?, ? )", col1, col2);
        log.debug( " update return {} ", rtn);
        return true;
    }

    public boolean updateList(List<Map<Object, Object>> uploadList, String tableName) {
        List<ColumnList> result = columnListRepository.findBySchemaAndTable(DEFAULT_SCHEMA, tableName);
        String query = makeInserQuery(tableName, result);

        log.debug("---------------------------------------------");
        log.debug("maked query stmt : {}", query);
        log.debug("---------------------------------------------");

        ListIterator itr = uploadList.listIterator();
        while (itr.hasNext()) {
            Map<Object, Object> uploadData = (Map<Object, Object>) itr.next();


            jdbcTemplate.update(query, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    for (int i = 0; i < result.size(); i++) {
                        ps.setString(i+1, (String) uploadData.get(result.get(i).getColumnName()));
                    }
                }
            });
        }
        return true;
    }

    public String makeInserQuery(String tableName, List<ColumnList> result) {
        String stmt = "Insert into " + tableName + " (";
        String afterStmt = " values ( ";
        for (int i = 0; i < result.size(); i++) {
            if (i + 1 == result.size()) {
                stmt = stmt + result.get(i).getColumnName() + ") " ;
                afterStmt = afterStmt + " ?)";
            } else {
                stmt = stmt + result.get(i).getColumnName() + "," ;
                afterStmt = afterStmt + " ?,";
            }
        }
        return stmt + afterStmt;
    }

    public int[] batchInsert(List<Map<Object, Object>> uploadList, String tableName) {
        List<ColumnList> result = columnListRepository.findBySchemaAndTable(DEFAULT_SCHEMA, tableName);
        String query = makeInserQuery(tableName, result);

        return jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<Object, Object> uploadData = uploadList.get(i);
                for (int j = 0; j < result.size(); j++) {
                     switch (result.get(j).getDataType()) {
                        case "varchar" :
                            ps.setString(j+1, (String) uploadData.get(result.get(j).getColumnName()));
                            break;
                        case "int":
                        case "double" :
                            log.debug("Double Casting : {}", (Double) uploadData.get(result.get(j).getColumnName()));
                            ps.setDouble(j+1, (Double) uploadData.get(result.get(j).getColumnName()));
                            break;
                        default:
                            ps.setString(j+1, (String) uploadData.get(result.get(j).getColumnName()));
                            break;
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return uploadList.size();
            }
        });
    }
}
