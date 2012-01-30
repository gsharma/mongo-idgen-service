package com.github.mongoidgen.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.mongoidgen.conf.AppContext;
import com.github.mongoidgen.service.IdGeneratorService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/META-INF/core.xml", "/META-INF/mongo.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class IdGeneratorServiceTest {
    @BeforeClass
    public static void useTestDB() {
        System.setProperty("db.name", "test");
        System.setProperty("db.user", "");
        System.setProperty("db.password", "");
        System.setProperty("datastore.replicas", "");
        System.setProperty("spring.profiles.active", "mongo.master");
        // BasicConfigurator.configure();
    }

    @Test
    public void testOneMongoId() {
        assertNotNull(getMongoService().generate());
    }

    @Test
    public void test1000MongoIds() throws Exception {
        IdGeneratorService<String> service = getMongoService();
        Thread.sleep(500L);
        long start = System.currentTimeMillis();
        int counter = 1000;
        while (counter-- > 0) {
            assertNotNull("counter=" + counter, service.generate());
        }
        System.out.println("1000 mongo ids in millis: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void test1000MongoIdsUniqueness() throws Exception {
        IdGeneratorService<String> service = getMongoService();
        Set<String> ids = new HashSet<String>();
        int counter = 1000;
        while (counter-- > 0) {
            String id = service.generate();
            assertNotNull("counter=" + counter, id);
            assertTrue(ids.add(id));
        }
        assertEquals(1000, ids.size());
    }

    @Test
    public void testOneMemoryId() {
        assertNotNull(getInMemoryService().generate());
    }

    @Test
    public void test1000MemoryIds() {
        IdGeneratorService<String> service = getInMemoryService();
        long start = System.currentTimeMillis();
        int counter = 1000;
        while (counter-- > 0) {
            assertNotNull(service.generate());
        }
        System.out.println("1000 memory ids in millis: " + (System.currentTimeMillis() - start));
    }

    @SuppressWarnings("unchecked")
    private IdGeneratorService<String> getInMemoryService() {
        return (IdGeneratorService<String>) AppContext.getApplicationContext().getBean("inMemoryIdgen");
    }

    @SuppressWarnings("unchecked")
    private IdGeneratorService<String> getMongoService() {
        return (IdGeneratorService<String>) AppContext.getApplicationContext().getBean("mongoIdgen");
    }
}
