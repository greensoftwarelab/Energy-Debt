package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

@Rule(
        key = "SensorResourceLeak",
        name = "Sensor Resource Leak",
        description = "Failure to properly release a Sensor lock mechanism",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class SensorResourceLeakRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private String sensorManager = new String();
    private boolean unregisterListener = false;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tSENSOR RESOURCE START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitClass(ClassTree tree) {
        boolean extendsActivity = tree.superClass() != null && tree.superClass().firstToken().text().equals("Activity");
        boolean implementsSensorEventListener = tree.superInterfaces().stream().anyMatch(interf -> interf.firstToken().text().equals("SensorEventListener"));

        if(extendsActivity && implementsSensorEventListener){
            super.visitClass(tree);
            this.sensorManager = new String();
        }

    }

    @Override
    public void visitVariable(VariableTree tree) {
        String varName = tree.simpleName().name();
        String type = tree.type().firstToken().text();


        if(type.equals("SensorManager"))
            this.sensorManager = varName;

        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        String varName = tree.methodSelect().firstToken().text();
        String methodName = tree.methodSelect().lastToken().text();
        if(this.sensorManager.equals(varName) && methodName.equals("unregisterListener"))
            this.unregisterListener = true;

        super.visitMethodInvocation(tree);
    }

    @Override
    public void visitMethod(MethodTree tree) {
        boolean isOnPause = tree.isOverriding() != null && tree.isOverriding() && tree.symbol().name().equals("onPause");

        if(isOnPause){
            super.visitMethod(tree);

            if(!this.unregisterListener){
                System.out.println("\t\t\tISSUE FOUND");
                context.reportIssue(this, tree, "Sensor Resource Leak");
            }

            this.unregisterListener = false;
        }

    }
}

