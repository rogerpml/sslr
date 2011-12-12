/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.ParsingState;

public class RuleMatcher extends MemoizedMatcher {

  protected String name;
  private AstListener listener;
  private boolean recoveryRule = false;
  private AstNodeType astNodeType;

  protected RuleMatcher(String name) {
    this.name = name;
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    if (super.children.length == 0) {
      throw new IllegalStateException("The rule '" + name + "' hasn't beed defined.");
    }

    if (recoveryRule) {
      RecognitionException recognitionException = parsingState.extendedStackTrace == null ?
          new RecognitionException(parsingState, false) : new RecognitionException(parsingState.extendedStackTrace, false);

      if (super.children[0].isMatching(parsingState)) {
        parsingState.notifyListeners(recognitionException);
      }
    }

    AstNode childNode = super.children[0].match(parsingState);

    AstNode astNode = new AstNode(astNodeType, name, parsingState.peekTokenIfExists(startIndex, super.children[0]));
    astNode.setAstNodeListener(listener);
    astNode.addChild(childNode);
    return astNode;
  }

  protected void setMatcher(Matcher matcher) {
    super.children = new Matcher[] { matcher };
  }

  public void setListener(AstListener listener) {
    this.listener = listener;
  }

  public void setNodeType(AstNodeType astNodeType) {
    this.astNodeType = astNodeType;
  }

  public String getName() {
    return name;
  }

  public void recoveryRule() {
    recoveryRule = true;
  }

  @Override
  public String toString() {
    return getName();
  }

}
