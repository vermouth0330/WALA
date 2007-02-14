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


import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.ssa.ValueDecorator;
import com.ibm.wala.util.debug.Assertions;

/**
 *  A simple assignment statement.  Only appears in the IR before SSA
 * conversion, and temporarily when needed to undo copy propagation
 * during processing of new lexical definitions and uses.
 *
 * @author Julian Dolby (dolby@us.ibm.com)
 *
 */
public class AssignInstruction extends SSAUnaryOpInstruction {

  /**
   * create the assignment v_result := v_val
   * @param result
   * @param val
   */
  public AssignInstruction(int result, int val) {
    super(null, result, val);
    if (Assertions.verifyAssertions) {
      Assertions._assert(result != val);
      Assertions._assert(result != -1);
      Assertions._assert(val != -1);
    }
  }

  /* (non-Javadoc)
   * @see com.ibm.wala.ssa.SSAInstruction#copyForSSA(int[], int[])
   */
  public SSAInstruction copyForSSA(int[] defs, int[] uses) {
    return new AssignInstruction(defs == null ? getDef(0) : defs[0], uses == null ? getUse(0) : uses[0]);
  }

  /* (non-Javadoc)
   * @see com.ibm.wala.ssa.SSAInstruction#toString(com.ibm.wala.ssa.SymbolTable, com.ibm.wala.ssa.ValueDecorator)
   */
  public String toString(SymbolTable symbolTable, ValueDecorator d) {
    return getValueString(symbolTable, d, result) + " := " + getValueString(symbolTable, d, val);
  }

  /* (non-Javadoc)
   * @see com.ibm.wala.ssa.SSAInstruction#visit(com.ibm.wala.ssa.SSAInstruction.Visitor)
   */
  public void visit(IVisitor v) {
    ((AstPreInstructionVisitor) v).visitAssign(this);
  }

  /**
   * @return
   */
  public int getVal() {
    return getUse(0);
  }
}
