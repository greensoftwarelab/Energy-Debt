package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol.MethodSymbol;
import org.sonar.plugins.java.api.tree.ImportTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import java.util.List;

@Rule(
        key = "HashMapUsage",
        name = "HashMap Usage",
        description = "Usage of the HashMap collection over ArrayMap",
        priority = Priority.MAJOR,
        tags = {"energy-smell"})
public class HashMapUsageRule extends IssuableSubscriptionVisitor {
    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.VARIABLE, Kind.METHOD, Kind.IMPORT);
    }

    @Override
    public void visitNode(Tree tree) {
        System.out.println("\t HASHMAP USAGE");
        if(tree.is(Kind.METHOD)){
            MethodTree methodTree = (MethodTree) tree;
            MethodSymbol symbol = methodTree.symbol();
            symbol.parameterTypes().stream().forEach(type -> System.out.print(type.fullyQualifiedName() + ", "));

            if(symbol.parameterTypes().stream().anyMatch(type -> type.is("java.util.HashMap"))) {
                System.out.println("ISSUE: " + methodTree.simpleName() + " HAS HASHMAP PARAM");
                reportIssue(methodTree.simpleName(), "HashMap Usage");
            }

        }

        else if(tree.is(Kind.VARIABLE)){
            VariableTree variableTree = (VariableTree) tree;
            if(variableTree.symbol().type().is("java.util.HashMap")) {
                System.out.println("ISSUE: " + variableTree.simpleName() + " IS HASHMAP");
                reportIssue(variableTree.simpleName(), "HashMap Usage");
            }
        }
        else if(tree.is(Kind.IMPORT)){
            ImportTree importTree = (ImportTree) tree;
            if(importTree.qualifiedIdentifier().lastToken().text().equals("java.util.HashMap") || importTree.qualifiedIdentifier().lastToken().text().equals("HashMap"))
                reportIssue(importTree, "HashMap Usage");
        }
    }

}
