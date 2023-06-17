package guru.qa.niffler.test.ui;

import guru.qa.niffler.jupiter.MyBeforeAllExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MyBeforeAllExtension.class)
public class SecondUnitTest {

    @AfterAll
    static void afterAll() {
        System.out.println("#### @AfterAll");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("#### @beforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("    #### @BeforeEach");
    }

    @AfterEach
    void afterEach() {
        System.out.println("    #### @AfterEach");
    }

    @Test
    void firstTest() {
        Assertions.assertTrue(3 > 2);
        System.out.println("        #### @Test firstTest()");
    }

    @Test
    void secondTest() {
//        Assertions.assertTrue(3 > 2);
        System.out.println("        #### @Test secondTest()");
        throw new IllegalStateException();
    }
}