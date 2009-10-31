/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.compiler;

import java.util.List;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import org.spockframework.compiler.model.Spec;
import org.spockframework.util.SyntaxException;

/**
 * AST transformation performing most of the work of rewriting Spock
 * specifications. In contrast to EarlyTransform, this transform
 * runs in phase SEMANTIC_ANALYSIS, which provides a more meaningful
 * AST that is also decorated with reflection information. On the
 * flip side, because types and variables have already been resolved,
 * program elements like import statements and variable definitions
 * can no longer be manipulated at will.
 *
 * @author Peter Niederwieser
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class MainTransform implements ASTTransformation {
  private static final AstNodeCache nodeCache = new AstNodeCache();

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
    SourceLookup lookup = null;

    try {
      ModuleNode module = (ModuleNode)nodes[0];

      lookup = new SourceLookup(sourceUnit, new Janitor());
      List<ClassNode> classes = module.getClasses();

      for (ClassNode clazz : classes) {
        if (!isSpec(clazz)) continue;
        
        Spec spec = new SpecParser().build(clazz);
        spec.accept(new SpecRewriter(nodeCache, lookup));
        spec.accept(new SpecAnnotator(nodeCache));
      }
    } catch (SyntaxException e) {
      sourceUnit.getErrorCollector().addError(e.toSpockSyntaxException(), sourceUnit);
    } catch (Exception e) {
      // NOTE: this produces an uninformative error message in IDEA
      // ("an unknown error has occurred")
      sourceUnit.getErrorCollector().addError(
          new TransformErrorMessage(sourceUnit, e, true));
    } finally {
      if (lookup != null) lookup.close();
    }
  }

  private boolean isSpec(ClassNode clazz) {
    return AstUtil.hasAnnotation(clazz, spock.lang.Speck.class)
        || clazz.isDerivedFrom(nodeCache.Specification);
  }
}
