package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ViewHolderTest {
    @Test
    public void test() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/ViewHolderCheck.java")
                .withCheck(new ViewHolderRule())
                .verifyIssues(); //.verify("src/test/files/ViewHolderCheck.java", new ViewHolderRule());
    }
}

