package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.ArrayList;
import java.util.List;

@Rule(
        key = "DrawAllocation",
        name = "Draw Allocation",
        description = "Object allocation within the onDraw method of a class which extends a View Android component",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class DrawAllocationRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private boolean inMethod = false;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tDRAW ALLOCATION START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitMethod(MethodTree tree) {
        this.inMethod = true;

        super.visitMethod(tree);

        this.inMethod = false;
    }

    @Override
    public void visitVariable(VariableTree tree) {
        super.visitVariable(tree);

        String type = tree.firstToken().text();
        if(this.inMethod && type.equals("RectF"))
            context.reportIssue(this, tree, "Draw Allocation");

        super.visitVariable(tree);
    }
}
