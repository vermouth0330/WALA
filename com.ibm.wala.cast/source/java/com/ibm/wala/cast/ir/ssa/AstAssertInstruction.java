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

import java.util.Collection;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.ssa.ValueDecorator;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

/**
 *  An assert statement, as found in a variety of languages.  It has a
 * use which is the value being asserted to be true.  Additionally,
 * there is flag which denotes whether the assertion is from a
 * specification (the usual case) or is an assertion introduced by
 * "compilation" of whatever sort (e.g. to add assertions regarding
 * loop conditions needed by bounded model checking).
 *
 * @author Julian Dolby (dolby@us.ibm.com)
 */
public class AstAssertInstruction extends SSAInstruction {
  private final int value;

  private final boolean fromSpecification;

  public AstAssertInstruction(int value, boolean fromSpecification) {
    this.value = value;
    this.fromSpecification = fromSpecification;
  }

  public int getNumberOfUses() {
    return 1;
  }

  public int getUse(int i) {
    Assertions._assert(i == 0);
    return value;
  }

  public SSAInstruction copyForSSA(int[] defs, int[] uses) {
    return new AstAssertInstruction(uses == null ? value : uses[0], fromSpecification);
  }

  public String toString(SymbolTable symbolTable, ValueDecorator d) {
    return "assert " + getValueString(symbolTable, d, value) + " (fromSpec: " + fromSpecification + ")";
  }

  public void visit(IVisitor v) {
    ((AstInstructionVisitor) v).visitAssert(this);
  }

  public int hashCode() {
    return 2177 * value;
  }

  public Collection<TypeReference> getExceptionTypes() {
    return null;
  }

  public boolean isFallThrough() {
    return true;
  }

  public boolean isFromSpecification() {
    return fromSpecification;
  }
}
