package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class HashMapUsageTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/HashMapUsageCheck.java")
                .withCheck(new HashMapUsageRule())
                .verifyIssues(); //verify("src/test/files/HashMapUsageCheck.java", new HashMapUsageRule());
    }
}
