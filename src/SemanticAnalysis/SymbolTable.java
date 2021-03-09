package SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final SymbolTable parentSymTable;
  private final Map<String, Identifier> dictionary = new HashMap<>();
  private Map<String, Integer> offsetPerVar = new HashMap<>();
  /* Reserved variables size of the current scope */
  private int paramOffset = 0;
  /* Declared variables size of the current scope */
  private int declaredVariableSize = 0;

  public SymbolTable(SymbolTable parentSymTable) {
    this.parentSymTable = parentSymTable;
  }

  public SymbolTable getParentSymTable() {
    return parentSymTable;
  }

  /* Add a name and object to the symbol table */
  public void add(String name, Identifier object) {
    dictionary.put(name, object);
  }

  /* Get an identifier object from the symbol table */
  public Identifier lookup(String name) {
    return dictionary.get(name);
  }

  /* Get an identifier object from the symbol table or
      its enclosing symbol tables */
  public Identifier lookupAll(String name) {
    Identifier currentObject = this.lookup(name);

    if (currentObject == null && this.parentSymTable != null) {
      return parentSymTable.lookupAll(name);
    }
    return currentObject;
  }

  public void resetVariableOffset() {
    this.paramOffset = 0;
  }

  /* Update the offset for every entry in the symbol table that has
     an offset less than paramOffset */
  public void updateOffsetPerVar(int newOffset, int paramOffset) {
    for (String key : offsetPerVar.keySet()) {
      Integer value = offsetPerVar.get(key);
      if (paramOffset > value) {
        offsetPerVar.put(key, value + newOffset);
      }
    }
  }

  public boolean isTopSymTable() {
    return parentSymTable == null;
  }

  /* Set offset for a specific identifier */
  public void setOffset(String id, int offset) {
    offsetPerVar.put(id, offset);
  }

  /* Get offset of a specific identifier from the symbol table or
     or it's enclosing symbol tables */
  public int getOffset(String id) {
    if(!dictionary.containsKey(id)) {
      return parentSymTable.getOffset(id) + getVarsSize();
    }

    return offsetPerVar.get(id);
  }

  /* Get size of all variables stored in the symbol table */
  public int getVarsSize() {
    int size = 0;
    for(Identifier id : dictionary.values()) {
      if (id instanceof VariableId) {
        size += id.getSize();
      }
    }
    return size;
  }

  public void incrementParamOffset(int size) {
    this.paramOffset += size;
  }

  public int getParamOffset() {
    return paramOffset;
  }

//  /* Get offset of each variable stored in the symbol table */
//  public Map<String, Integer> saveOffsetPerVar() {
//    return new HashMap<>(offsetPerVar);
//  }
//
//  /* Set offset of each variable stored in the symbol table */
//  public void setOffsetPerVar(Map<String, Integer> offsetPerVar) {
//    this.offsetPerVar = offsetPerVar;
//  }
}