package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class MemberIgnoringMethodTest {
    @Test
    public void test(){
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/MemberIgnoringMethodCheck.java")
                .withCheck(new MemberIgnoringMethodRule())
                .verifyIssues();  //.verify("src/test/files/MemberIgnoringMethodCheck.java", new MemberIgnoringMethodRule());
    }
}

