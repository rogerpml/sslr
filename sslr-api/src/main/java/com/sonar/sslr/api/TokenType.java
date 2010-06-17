/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;


public interface TokenType extends AstNodeSkippingPolicy {

  public String getName();

  public String getValue();

  public boolean hasToBeSkippedFromAst(AstNode node);

}
