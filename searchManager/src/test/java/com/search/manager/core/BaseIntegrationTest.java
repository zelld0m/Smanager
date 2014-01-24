package com.search.manager.core;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "classpath:spring/spring-context-test.xml", "classpath:spring/spring-solr-context-test.xml",
    "classpath:spring/spring-datasource-test.xml"
})
public abstract class BaseIntegrationTest {

}
