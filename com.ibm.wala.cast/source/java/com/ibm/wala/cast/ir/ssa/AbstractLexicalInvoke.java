/******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.wala.cast.ir.ssa;


import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.*;
import com.ibm.wala.util.debug.Assertions;

/**
 *  This abstract class adds to invoke instructions the ability to
 * handle lexical uses and definitions during call graph construction.
 * The lexical uses and definitions of these objects are initially
 * empty, and get filled in by the AstSSAPropagationCallGraphBuilder,
 * particularly its LexicalOperator objects.  This class is still
 * abstract since the lexical scoping functionality is used by
 * multiple languages, each of which has further specializations of
 * invoke instructions.
 *
 * @author Julian Dolby (dolby@us.ibm.com)
 *
 */
public abstract class AbstractLexicalInvoke 
  extends SSAAbstractInvokeInstruction
{

  protected Access[] lexicalReads = null;

  protected Access[] lexicalWrites = null;

  protected AbstractLexicalInvoke(int result, 
				  int exception,
				  CallSiteReference site) 
  {
    super(result, exception, site);
  }

  protected AbstractLexicalInvoke(int result,
				  int exception,
				  CallSiteReference site,
				  Access[] lexicalReads,
				  Access[] lexicalWrites)
  {
    this(result, exception, site);
    this.lexicalReads = lexicalReads;
    this.lexicalWrites = lexicalWrites;
  }

  public int getNumberOfUses() {
    if (lexicalReads == null)
      return getNumberOfParameters();
    else
      return getNumberOfParameters() + lexicalReads.length;
  }

  public int getUse(int j) {
    Assertions._assert(j >= getNumberOfParameters());
    Assertions._assert(lexicalReads != null);
    Assertions._assert(lexicalReads[j - getNumberOfParameters()] != null);
    return lexicalReads[j - getNumberOfParameters()].valueNumber;
  }

  public int getNumberOfDefs() {
    if (lexicalWrites == null)
      return super.getNumberOfDefs();
    else
      return super.getNumberOfDefs() + lexicalWrites.length;
  }
	
  public int getDef(int j) {
    if (j < super.getNumberOfDefs())
      return super.getDef(j);
    else
      return lexicalWrites[j-super.getNumberOfDefs()].valueNumber;
  }

  private Access[] addAccess(Access[] array, Access access) {
    if (array == null)
      return new Access[]{ access };
    else {
      Access[] result = new Access[ array.length + 1 ];
      System.arraycopy(array, 0, result, 0, array.length);
      result[ array.length ] = access;
      return result;
    }
  }

  public boolean isLexicalUse(int use) {
    return use >= super.getNumberOfUses();
  }

  public void addLexicalUse(Access use) {
    lexicalReads = addAccess(lexicalReads, use);
  }

  public Access getLexicalUse(int i) {
    return lexicalReads[i-getNumberOfParameters()];
  }

  public boolean isLexicalDef(int def) {
    return def >= super.getNumberOfDefs();
  }

  public void addLexicalDef(Access def) {
    lexicalWrites = addAccess(lexicalWrites, def);
  }

  public Access getLexicalDef(int i) {
    return lexicalWrites[i-super.getNumberOfDefs()];
  }

  public int hashCode() {
    return site.hashCode() * 7529;
  }

  public boolean equals(Object obj) {
    if (obj != null && getClass().equals(obj.getClass())) {
      SSAAbstractInvokeInstruction other = (SSAAbstractInvokeInstruction)obj;
      return site.equals(other.getCallSite());
    } else {
      return false;
    }
  }

  public String toString(SymbolTable symbolTable, ValueDecorator d) {
    StringBuffer s = new StringBuffer(super.toString(symbolTable, d));

    if (lexicalReads != null) {
      s.append(" (reads:");
      for(int i = 0; i < lexicalReads.length; i++) {
	s.append(" ").append(lexicalReads[i].variableName).append(":").append( getValueString(symbolTable, d, lexicalReads[i].valueNumber) );
      }
      s.append(")");
    }

    if (lexicalWrites != null) {
      s.append(" (writes:");
      for(int i = 0; i < lexicalWrites.length; i++) {
	s.append(" ").append(lexicalWrites[i].variableName).append(":").append( getValueString(symbolTable, d, lexicalWrites[i].valueNumber) );
      }
      s.append(")");
    }

    return s.toString();
  }
}
