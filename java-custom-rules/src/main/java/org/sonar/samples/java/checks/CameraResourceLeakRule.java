package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

@Rule(
        key = "CameraResourceLeak",
        name = "Camera Resource Leak",
        description = "Failure to properly release a camera lock (and possible media recorder) mechanism",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class CameraResourceLeakRule extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;
    private String camera = new String();
    private boolean cameraRelease = false;
    private String mediaRecorder = new String();
    private boolean mediaRecorderRelease = false;

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        System.out.println("\t\tCAMERA RESOURCE START");
        // System.out.println(PrinterVisitor.print(context.getTree())); // Display AST 
        scan(context.getTree());

        System.out.println("\t\tSCAN OVER");

    }

    @Override
    public void visitClass(ClassTree tree) {
        if(tree.superClass() != null && tree.superClass().firstToken().text().equals("Activity")){
            super.visitClass(tree);
            this.camera = new String(); this.mediaRecorder = new String();
        }

    }

    @Override
    public void visitVariable(VariableTree tree) {
        String varName = tree.simpleName().name();
        String type = tree.type().firstToken().text();

        if(type.equals("Camera"))
            this.camera = varName;

        if(type.equals("MediaRecorder"))
            this.mediaRecorder = varName;

        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        String varName = tree.methodSelect().firstToken().text();
        String methodName = tree.methodSelect().lastToken().text();
        if(this.camera.equals(varName) && methodName.equals("release"))
            this.cameraRelease = true;

        if(this.mediaRecorder.equals(varName) && methodName.equals("release"))
            this.mediaRecorderRelease = true;

        super.visitMethodInvocation(tree);
    }

    @Override
    public void visitMethod(MethodTree tree) {
        boolean isOnPause = tree.isOverriding() != null && tree.isOverriding() && tree.simpleName().name().equals("onPause");

        if(isOnPause){
            super.visitMethod(tree);

            if(!this.mediaRecorder.isEmpty() && !this.mediaRecorderRelease){
                System.out.println("\t\t\tISSUE FOUND: MEDIA RECORDER");
                context.reportIssue(this, tree, "Camera Resource Leak");
            }
            else if(!this.cameraRelease){
                System.out.println("\t\t\tISSUE FOUND: CAMERA");
                context.reportIssue(this, tree, "Camera Resource Leak");
            }

            this.cameraRelease = false;
            this.mediaRecorderRelease = false;
        }

    }
}
