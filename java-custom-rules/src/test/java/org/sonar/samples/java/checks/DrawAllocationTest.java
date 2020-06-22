package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class DrawAllocationTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/DrawAllocationCheck.java")
                .withCheck(new DrawAllocationRule())
                .verifyIssues(); //.verify("src/test/files/DrawAllocationCheck.java", new DrawAllocationRule());
    }
}
