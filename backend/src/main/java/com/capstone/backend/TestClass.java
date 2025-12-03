package com.capstone.backend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClass {

    private static final Logger log = LoggerFactory.getLogger(TestClass.class);

    public static void main(String[] args) {
        TestClass tester = new TestClass();
        tester.testLog();
    }

    public void testLog() {
        log.info("INFO 로그");
        log.debug("DEBUG 로그");
        log.error("ERROR 로그");
    }
}
