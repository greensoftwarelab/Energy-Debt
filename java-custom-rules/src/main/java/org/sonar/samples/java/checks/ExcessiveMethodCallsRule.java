package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

import java.util.HashMap;


@Rule(
        key = "ExcessiveMethodCalls",
        name = "Excessive Method Calls",
        description = "Extractable method call",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class ExcessiveMethodCallsRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private String view = new String();
    private int loops = 0;
    HashMap<String, Integer> params = new HashMap<>();
    HashMap<String, String> returns = new HashMap<>();

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tEXCESSIVE METHOD CALLS START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitMethod(MethodTree tree) {
        this.params.put(tree.simpleName().name(), tree.parameters().size());

        if(tree.returnType() != null)
            this.returns.put(tree.simpleName().name(), tree.returnType().symbolType().fullyQualifiedName());

        super.visitMethod(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        if(this.loops > 0 && this.params.getOrDefault(tree.symbol().name(), -1) == 0 && !this.returns.getOrDefault(tree.symbol().name(), "void").equals("void")){
            context.reportIssue(this, tree, "Excessive Method Calls");
        }

        super.visitMethodInvocation(tree);
    }

    @Override
    public void visitForStatement(ForStatementTree tree) {
        this.loops += 1;

        super.visitForStatement(tree);

        this.loops -= 1;
    }

    @Override
    public void visitForEachStatement(ForEachStatement tree) {
        this.loops += 1;

        super.visitForEachStatement(tree);

        this.loops -= 1;
    }

    @Override
    public void visitWhileStatement(WhileStatementTree tree) {
        this.loops += 1;

        super.visitWhileStatement(tree);

        this.loops -= 1;
    }

    @Override
    public void visitDoWhileStatement(DoWhileStatementTree tree) {
        this.loops += 1;

        super.visitDoWhileStatement(tree);

        this.loops -= 1;
    }

}
