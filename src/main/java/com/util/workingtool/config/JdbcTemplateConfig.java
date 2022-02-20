package com.util.workingtool.config;

import com.util.workingtool.domain.table.Sample1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class JdbcTemplateConfig {

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
}
