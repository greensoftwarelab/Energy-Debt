package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SensorResourceLeakTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/SensorResourceLeakCheck.java")
                .withCheck(new SensorResourceLeakRule())
                .verifyIssues(); //.verify("src/test/files/SensorResourceLeakCheck.java", new SensorResourceLeakRule());
    }
}
