/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static com.sonar.sslr.test.lexer.MockHelper.mockTokenBuilder;
import static org.fest.assertions.Assertions.assertThat;

public class AstXmlPrinterTest {

  @Test
  public void testPrintRuleAstNode() {
    AstNode root = new AstNode(RuleDefinition.newRuleBuilder("expr"), "expr", mockTokenBuilder(new WordTokenType(), "word").setLine(34)
        .setColumn(12).build());

    assertThat(AstXmlPrinter.print(root)).isEqualTo("<expr tokenValue=\"word\" tokenLine=\"34\" tokenColumn=\"12\"/>");
  }

  @Test
  public void testPrintWordAstNode() {
    AstNode root = new AstNode(mockToken(new WordTokenType(), "myword"));
    assertThat(AstXmlPrinter.print(root)).isEqualTo("<WORD tokenValue=\"myword\" tokenLine=\"1\" tokenColumn=\"1\"/>");
  }

  @Test
  public void testPrintFullAstNode() {
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder("expr"), "expr", null);
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "x")));
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "=")));
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "4")));

    String expectedResult = new StringBuilder()
        .append("<expr>\n")
        .append("  <WORD tokenValue=\"x\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
        .append("  <WORD tokenValue=\"=\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
        .append("  <WORD tokenValue=\"4\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
        .append("</expr>")
        .toString();
    assertThat(AstXmlPrinter.print(astNode)).isEqualTo(expectedResult);
  }

  private static class WordTokenType implements TokenType {

    public String getName() {
      return "WORD";
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

    public String getValue() {
      return "WORDS";
    }

  }

}
