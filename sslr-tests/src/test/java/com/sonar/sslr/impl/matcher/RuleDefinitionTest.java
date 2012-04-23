/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RuleDefinitionTest {

  @Test(expected = IllegalStateException.class)
  public void testEmptyIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is();
  }

  @Test(expected = IllegalStateException.class)
  public void testMoreThanOneDefinitionForASigleRuleWithIs() {
    RuleDefinition javaClassDefinition = RuleDefinition.newRuleBuilder("JavaClassDefinition");
    javaClassDefinition.is("option1");
    javaClassDefinition.is("option2");
  }

  @Test
  public void testIs() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
  }

  @Test
  public void testOverride() {
    RuleDefinition myRule = RuleDefinition.newRuleBuilder("MyRule");
    myRule.is("option1");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option1\")"));
    myRule.override("option2");
    assertThat(MatcherTreePrinter.print(myRule.getRule()), is("MyRule.is(\"option2\")"));
  }

  @Test
  public void testSkipFromAst() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(false));

    ruleBuilder.skip();
    assertThat(ruleBuilder.hasToBeSkippedFromAst(null), is(true));
  }

  @Test
  public void testSkipFromAstIf() {
    RuleDefinition ruleBuilder = RuleDefinition.newRuleBuilder("MyRule");
    ruleBuilder.skipIfOneChild();

    AstNode parent = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "parent"));
    AstNode child1 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child1"));
    AstNode child2 = new AstNode(mockToken(GenericTokenType.IDENTIFIER, "child2"));
    parent.addChild(child1);
    parent.addChild(child2);
    child1.addChild(child2);

    assertThat(ruleBuilder.hasToBeSkippedFromAst(parent), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child2), is(false));
    assertThat(ruleBuilder.hasToBeSkippedFromAst(child1), is(true));
  }
}
