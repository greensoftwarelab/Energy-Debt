package org.sonar.samples.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import javax.annotation.CheckForNull;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

@Rule(
        key = "MemberIgnoringMethod",
        name = "Member Ignoring Method",
        description = "Non static method could be made static",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class MemberIgnoringMethodRule extends BaseTreeVisitor implements JavaFileScanner {
    private static final String JAVA_IO_SERIALIZABLE = "java.io.Serializable";
    private static final MethodMatchers EXCLUDED_SERIALIZABLE_METHODS = MethodMatchers.or(
            MethodMatchers.create().ofSubTypes(JAVA_IO_SERIALIZABLE).names("readObject").addParametersMatcher("java.io.ObjectInputStream").build(),
            MethodMatchers.create().ofSubTypes(JAVA_IO_SERIALIZABLE).names("writeObject").addParametersMatcher("java.io.ObjectOutputStream").build(),
            MethodMatchers.create().ofSubTypes(JAVA_IO_SERIALIZABLE).names("readObjectNoData").addWithoutParametersMatcher().build(),
            MethodMatchers.create().ofSubTypes(JAVA_IO_SERIALIZABLE).names("writeReplace").addWithoutParametersMatcher().build(),
            MethodMatchers.create().ofSubTypes(JAVA_IO_SERIALIZABLE).names("readResolve").addWithoutParametersMatcher().build()
                    );

    private JavaFileScannerContext context;
    private Deque<MethodReference> methodReferences = new LinkedList<>();

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        if (context.getSemanticModel() != null) {
            scan(context.getTree());
        }
    }

    @Override
    public void visitMethod(MethodTree tree) {
        if (isExcluded(tree)) {
            return;
        }
        Symbol.MethodSymbol symbol = tree.symbol();
        methodReferences.push(new MethodReference(symbol));
        scan(tree.parameters());
        scan(tree.block());
        MethodReference reference = methodReferences.pop();
        if (symbol.isPrivate() && !symbol.isStatic() && !reference.hasNonStaticReference()) {
            context.reportIssue(this, tree.simpleName(), "Make \"" + symbol.name() + "\" a \"static\" method.");
        }
    }

    private static boolean isExcluded(MethodTree tree) {
        return tree.is(Tree.Kind.CONSTRUCTOR) || EXCLUDED_SERIALIZABLE_METHODS.matches(tree);
    }

    @Override
    public void visitIdentifier(IdentifierTree tree) {
        super.visitIdentifier(tree);
        if ("class".equals(tree.name()) || methodReferences.isEmpty()) {
            return;
        }
        if (parentIs(tree, Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree parent = (MemberSelectExpressionTree) tree.parent();
            // Exclude identifiers used in member select, except for instance creation
            // New class may use member select to denote an inner class
            if (tree.equals(parent.identifier()) && !parentIs(parent, Tree.Kind.NEW_CLASS) && !refToEnclosingClass(tree)) {
                return;
            }
        }
        visitTerminalIdentifier(tree);
    }

    private static boolean refToEnclosingClass(IdentifierTree tree) {
        String identifier = tree.name();
        return "this".equals(identifier) || "super".equals(identifier);
    }

    private void visitTerminalIdentifier(IdentifierTree tree) {
        Symbol symbol = tree.symbol();
        MethodReference currentMethod = methodReferences.peek();
        if (symbol.isUnknown()) {
            currentMethod.setNonStaticReference();
            return;
        }
        for (MethodReference methodReference : methodReferences) {
            methodReference.checkSymbol(symbol);
        }
    }

    private static boolean parentIs(Tree tree, Tree.Kind kind) {
        return tree.parent() != null && tree.parent().is(kind);
    }

    @Override
    public void visitMemberSelectExpression(MemberSelectExpressionTree tree) {
        if (tree.expression().is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifier = (IdentifierTree) tree.expression();
            Symbol owner = identifier.symbol().owner();
            if (owner != null && owner.isMethodSymbol()) {
                // No need to investigate selection on local symbols
                return;
            }
        }
        super.visitMemberSelectExpression(tree);
    }

    private static class MethodReference {

        private final Symbol.MethodSymbol methodSymbol;
        private final Symbol methodScopeOwner;
        private boolean nonStaticReference = false;

        MethodReference(Symbol.MethodSymbol symbol) {
            methodSymbol = symbol;
            methodScopeOwner = methodSymbol.owner();
            if (methodScopeOwner != null && methodScopeOwner.isTypeSymbol()) {
                nonStaticReference = !methodScopeOwner.isStatic() && !methodScopeOwner.owner().isPackageSymbol();
            }
        }

        @CheckForNull
        private static Symbol getPackage(Symbol symbol) {
            Symbol owner = symbol.owner();
            while (owner != null) {
                if (owner.isPackageSymbol()) {
                    break;
                }
                owner = owner.owner();
            }
            return owner;
        }

        void setNonStaticReference() {
            nonStaticReference = true;
        }

        boolean hasNonStaticReference() {
            return nonStaticReference;
        }

        void checkSymbol(Symbol symbol) {
            if (nonStaticReference || methodSymbol.equals(symbol) || symbol.isStatic()) {
                return;
            }
            Symbol scopeOwner = symbol.owner();
            if (isConstructor(symbol)) {
                checkConstructor(scopeOwner);
            } else if (scopeOwner != null) {
                checkNonConstructor(scopeOwner);
            }
        }

        private void checkConstructor(Symbol constructorClass) {
            if (!constructorClass.isStatic()) {
                Symbol methodPackage = getPackage(methodScopeOwner);
                Symbol constructorPackage = getPackage(constructorClass);
                if (Objects.equals(methodPackage, constructorPackage) && !constructorClass.owner().isPackageSymbol()) {
                    setNonStaticReference();
                }
            }
        }

        private void checkNonConstructor(Symbol scopeOwner) {
            if (scopeOwner.isMethodSymbol()) {
                return;
            }
            if (hasLocalAccess(methodScopeOwner, scopeOwner)) {
                setNonStaticReference();
            }
        }

        private static boolean isConstructor(Symbol symbol) {
            return "<init>".equals(symbol.name());
        }

        private static boolean hasLocalAccess(Symbol scope, Symbol symbol) {
            if (scope.equals(symbol)) {
                return true;
            }
            if (scope.isTypeSymbol() && symbol.isTypeSymbol()) {
                Type scopeType = scope.type().erasure();
                Type symbolType = symbol.type().erasure();
                if (scopeType.isSubtypeOf(symbolType)) {
                    return true;
                }
            }
            return false;
        }
    }

}
