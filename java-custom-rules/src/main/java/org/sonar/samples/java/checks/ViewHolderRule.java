package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

import java.util.LinkedList;


@Rule(
        key = "ViewHolder",
        name = "View Holder",
        description = "Object allocation within the onDraw method of a class which extends a View Android component",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class ViewHolderRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private String view = new String();
    private int conditions = 0;
    private boolean method = false;
    private boolean inflatesView = false;


    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tVIEW HOLDER START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitIfStatement(IfStatementTree tree) {
        this.conditions += 1;

        super.visitIfStatement(tree);

        this.conditions -= 1;
    }

    @Override
    public void visitMethod(MethodTree tree) {
        boolean returnsView = tree.returnType() != null && tree.returnType().symbolType().name().equals("View");

        this.method = true;

        super.visitMethod(tree);

        if(this.inflatesView){
            System.out.println("\t\t\t\tISSUE:  " + this.conditions + "; " + tree.parent());
            context.reportIssue(this, tree, "View Holder");
        }

        this.method = false;
        this.inflatesView = false;

    }

    @Override
    public void visitVariable(VariableTree tree) {
        String varName = tree.simpleName().name();
        String type = tree.type().firstToken().text();
        

        if(type.equals("WakeLock"))
            this.view = varName;

        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        String varName = tree.methodSelect().firstToken().text();
        String methodName = tree.methodSelect().lastToken().text();

        if(this.method && this.conditions == 0 && methodName.equals("findViewById")) {
            this.inflatesView = true;
        }

        super.visitMethodInvocation(tree);
    }

}
