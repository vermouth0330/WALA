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
/*
 * Created on Sep 28, 2005
 */
package com.ibm.wala.cast.java.translator.polyglot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.impl.CAstTypeDictionaryImpl;
import com.ibm.wala.util.debug.Assertions;

public class PolyglotTypeDictionary extends CAstTypeDictionaryImpl {
  private final class PolyglotJavaArrayType implements CAstType.Array {
    private final Type fEltPolyglotType;

    private final CAstType fEltCAstType;

    private PolyglotJavaArrayType(ArrayType arrayType) {
      super();
      fEltPolyglotType = arrayType.base();
      fEltCAstType = getCAstTypeFor(fEltPolyglotType);
    }

    public int getNumDimensions() {
      return 1; // always 1 for Java
    }

    public CAstType getElementType() {
      return fEltCAstType;
    }

    public String getName() {
      return "[" + fEltCAstType.getName();
    }

    public Collection getSupertypes() {
      if (fEltPolyglotType.isPrimitive())
        return Collections.singleton(getCAstTypeFor(fTypeSystem.Object()));
      Assertions._assert(fEltPolyglotType.isReference(), "Non-primitive, non-reference array element type!");
      ReferenceType baseRefType = (ReferenceType) fEltPolyglotType;
      Collection<CAstType> supers = new ArrayList<CAstType>();
      for (Iterator superIter = baseRefType.interfaces().iterator(); superIter.hasNext();) {
        supers.add(getCAstTypeFor(superIter.next()));
      }
      if (baseRefType.superType() != null)
        supers.add(getCAstTypeFor(baseRefType.superType()));
      return supers;
    }
  }

  protected final TypeSystem fTypeSystem;

  private final PolyglotJava2CAstTranslator fTranslator;

  public PolyglotTypeDictionary(TypeSystem typeSystem, PolyglotJava2CAstTranslator translator) {
    fTypeSystem = typeSystem;
    fTranslator = translator;
  }

  public CAstType getCAstTypeFor(Object astType) {
    CAstType type = super.getCAstTypeFor(astType);
    // Handle the case where we haven't seen an AST decl for some type before
    // processing a reference. This can certainly happen with classes in byte-
    // code libraries, for which we never see an AST decl.
    // In this case, just create a new type and return that.
    if (type == null) {
      final Type polyglotType = (Type) astType;

      if (polyglotType.isClass())
        type = fTranslator.new PolyglotJavaType((ClassType) astType, this, fTypeSystem);
      else if (polyglotType.isPrimitive()) {
        type = JavaPrimitiveTypeMap.lookupType(((PrimitiveType) polyglotType).name());
      } else if (polyglotType.isArray()) {
        type = new PolyglotJavaArrayType((ArrayType) polyglotType);
      } else
        Assertions.UNREACHABLE("getCAstTypeFor() passed type that is not primitive, array, or class?");
      super.map(astType, type);
    }
    return type;
  }
}