package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

import java.util.ArrayList;

@Rule(
        key = "Recycle",
        name = "Recycle",
        description = "Failure to close or recycle collections or database related objects",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class RecycleRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private ArrayList<String> collections = new ArrayList<>();

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tRECYCLE START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitVariable(VariableTree tree) {

        String type = tree.firstToken().text();
        if(type.equals("TypedArray") || type.equals("Cursor"))
            this.collections.add(tree.simpleName().name());

        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        String varName = tree.methodSelect().firstToken().text();
        String methodName = tree.methodSelect().lastToken().text();
        if(this.collections.contains(varName) && (methodName.equals("recycle") || methodName.equals("close")))
            this.collections.remove(varName);

        super.visitMethodInvocation(tree);
    }

    @Override
    public void visitMethod(MethodTree tree) {
        super.visitMethod(tree);

        if(!this.collections.isEmpty()) {
            System.out.println("\t\t\tISSUE FOUND");
            context.reportIssue(this, tree, "Recycle");
        }

        this.collections.clear();
    }
}
