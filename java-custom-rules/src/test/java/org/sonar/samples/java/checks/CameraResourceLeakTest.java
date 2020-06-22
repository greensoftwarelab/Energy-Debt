package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class CameraResourceLeakTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/CameraResourceLeakCheck.java")
                .withCheck(new CameraResourceLeakRule())
                .verifyIssues(); //verify("src/test/files/CameraResourceLeakCheck.java", new CameraResourceLeakRule());
    }
}
