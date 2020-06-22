package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class RecycleTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/RecycleCheck.java")
                .withCheck(new RecycleRule())
                .verifyIssues(); //.verify("src/test/files/RecycleCheck.java", new RecycleRule());
    }
}
