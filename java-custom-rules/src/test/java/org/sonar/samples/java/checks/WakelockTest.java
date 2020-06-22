package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class WakelockTest {
    @Test
    public void test() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/WakelockCheck.java")
                .withCheck(new WakelockRule())
                .verifyIssues(); //.verify("src/test/files/WakelockCheck.java", new WakelockRule());
    }
}
