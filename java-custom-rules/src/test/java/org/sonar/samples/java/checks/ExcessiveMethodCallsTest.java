package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ExcessiveMethodCallsTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/ExcessiveMethodCallsCheck.java")
                .withCheck(new ExcessiveMethodCallsRule())
                .verifyIssues(); //verify("src/test/files/ExcessiveMethodCallsCheck.java", new ExcessiveMethodCallsRule());
    }
}
