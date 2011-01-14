/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class Parser<GRAMMAR extends Grammar> {

  private RuleImpl rootRule;
  private ParsingState parsingState;
  private LexerOutput lexerOutput;
  private Lexer lexer;
  private GRAMMAR grammar;

  public Parser(GRAMMAR grammar, Lexer lexer, List<GrammarDecorator<GRAMMAR>> decorators) {
    this.grammar = grammar;
    setDecorators(decorators);
    this.lexer = lexer;
  }

  public Parser(GRAMMAR grammar, Lexer lexer, GrammarDecorator<GRAMMAR>... decorators) {
    this(grammar, lexer, Arrays.asList(decorators));
  }

  public Parser(GRAMMAR grammar, Rule rootRule, Lexer lexer, GrammarDecorator<GRAMMAR>... decorators) {
    this(grammar, rootRule, lexer, Arrays.asList(decorators));
  }

  public Parser(GRAMMAR grammar, Rule rootRule, Lexer lexer, List<GrammarDecorator<GRAMMAR>> decorators) {
    this(grammar, lexer, decorators);
    this.rootRule = (RuleImpl) rootRule;
  }

  public void setDecorators(List<GrammarDecorator<GRAMMAR>> decorators) {
    for (GrammarDecorator<GRAMMAR> decorator : decorators) {
      decorator.decorate(grammar);
    }
    rootRule = (RuleImpl) grammar.getRootRule();
  }

  public void setDecorators(GrammarDecorator<GRAMMAR>... decorators) {
    setDecorators(Arrays.asList(decorators));
  }

  public AstNode parse(File file) {
    lexerOutput = lexer.lex(file);
    return parse(lexerOutput.getTokens());
  }

  public AstNode parse(String source) {
    lexerOutput = lexer.lex(source);
    return parse(lexerOutput.getTokens());
  }

  public AstNode parse(List<Token> tokens) {
    parsingState = null;
    beforeEachFile();
    try {
      parsingState = new ParsingState(tokens);
      return rootRule.parse(parsingState);
    } catch (RecognitionExceptionImpl e) {
      if (parsingState != null) {
        throw new RecognitionExceptionImpl(parsingState);
      } else {
        throw e;
      }
    } catch (StackOverflowError e) {
      throw new RecognitionExceptionImpl("The grammar seems to contain a left recursion which is not compatible with LL(*) parser.",
          parsingState, e);
    } finally {
      GrammarRuleLifeCycleManager.notifyEndParsing(grammar);
      afterEachFile();
    }
  }

  public void beforeEachFile() {
  }

  public void afterEachFile() {
  }

  public final ParsingState getParsingState() {
    return parsingState;
  }

  public final GRAMMAR getGrammar() {
    return grammar;
  }

  public final LexerOutput getLexerOutput() {
    return lexerOutput;
  }

  public final RuleImpl getRootRule() {
    return rootRule;
  }

  public final void setRootRule(Rule rootRule) {
    this.rootRule = (RuleImpl) rootRule;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Root rule is : " + rootRule.toEBNFNotation() + "\n");
    result.append("and : " + lexerOutput.toString());
    return result.toString();
  }
}
