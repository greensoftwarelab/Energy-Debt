package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

@Rule(
        key = "Wakelock",
        name = "Wakelock",
        description = "Failure to properly release a wake lock mechanism",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class WakelockRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private String lock = new String();
    private boolean hasRelease = false;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tWAKELOCK START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitClass(ClassTree tree) {
        if(tree.superClass() != null && tree.superClass().firstToken().text().equals("Activity")){
            System.out.println("SUPER CLASS: " + tree.superClass().firstToken().text());
            super.visitClass(tree);
            this.lock = new String();
        }

    }

    @Override
    public void visitVariable(VariableTree tree) {
        String varName = tree.simpleName().name();
        String type = tree.type().firstToken().text();

        if(type.equals("WakeLock"))
            this.lock = varName;

        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        String varName = tree.methodSelect().firstToken().text();
        String methodName = tree.methodSelect().lastToken().text();
        if(this.lock.equals(varName) && methodName.equals("release"))
            this.hasRelease = true;

        super.visitMethodInvocation(tree);
    }

    @Override
    public void visitMethod(MethodTree tree) {
        boolean isOnPause = tree.isOverriding() != null && tree.isOverriding() && tree.symbol().name().equals("onPause");

        if(isOnPause){
            super.visitMethod(tree);

            if(isOnPause && !this.hasRelease){
                System.out.println("\t\t\tISSUE FOUND");
                context.reportIssue(this, tree, "Wakelock");
            }

            this.hasRelease = false;
        }

    }
}
