package SemanticAnalysis;

import SemanticAnalysis.DataTypes.ClassType;

import java.util.*;

public class SymbolTable {

  private final SymbolTable parentSymTable;
  private final Map<String, Identifier> dictionary = new HashMap<>();
  private SortedMap<String, Integer> offsetPerVar = new TreeMap<>();
  private int declaredParamsOffset = 0;
  private int declaredVarsOffset = 0;
  private int argsOffset = 0;

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

  public void remove(String name) {
    dictionary.remove(name);
  }

  /* Get an identifier object from the symbol table */
  public Identifier lookup(String name) {
    return dictionary.get(name);
  }

  public String findClass() {
    if (parentSymTable == null) {
      return "";
    }

    for (String key : dictionary.keySet()) {
      if (key.contains("class*") && dictionary.get(key) instanceof ClassType) {
        return key;
      }
    }

    return parentSymTable.findClass();
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
  public void setVarsOffset(String id, int size) {
    declaredVarsOffset -= size;
    offsetPerVar.put(id, declaredVarsOffset);
  }

  public void setParamsOffset(String id, int size) {
    offsetPerVar.put(id, declaredParamsOffset);
    declaredParamsOffset += size;
  }
  /* Get offset of a specific identifier from the symbol table or
  or it's enclosing symbol tables */
  public int getOffset(String id) {
    if (!offsetPerVar.containsKey(id)) {
      return parentSymTable.getOffset(id) + getVarsSize();
    }

    return offsetPerVar.get(id) + argsOffset;
  }

  /* Get size of all variables stored in the symbol table */
  public int getVarsSize() {
    int size = 0;
    for (Identifier id : dictionary.values()) {
      if (id instanceof VariableId) {
        size += id.getSize();
      }
    }
    return size;
  }

  public void incrementDeclaredVarsOffset(int size) {
    this.declaredVarsOffset += size;
  }

  public void incrementDeclaredParamsOffset(int size) {
    this.declaredParamsOffset += size;
  }

  public void incrementArgsOffset(int size) {
    this.argsOffset += size;
  }

  public void resetArgsOffset() {
    this.argsOffset = 0;
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
