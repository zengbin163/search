package com.gaojie.gsearch.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GSearchSqlDaoTest {

    @Autowired
    private GSearchSqlDao gSearchSqlDao;

    @Test
    public void test1() {
        String sql = "select * from t_app_user";
        this.gSearchSqlDao.executeDynamicSql(sql, 0L, 2000L);
        this.gSearchSqlDao.executeCountDynamicSql(sql);
    }
}
