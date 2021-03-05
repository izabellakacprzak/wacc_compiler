package SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final SymbolTable parentSymTable;
  private final Map<String, Identifier> dictionary = new HashMap<>();
  private Map<String, Integer> offsetPerVar = new HashMap<>();

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

  public int updateOffsetPerVar(int newOffset, int paramOffset) {
  int newOff = 0;
    for (String key : offsetPerVar.keySet()) {
      Integer value = offsetPerVar.get(key);
      if (paramOffset > value) {
        offsetPerVar.put(key, value + newOffset);
        newOff += newOffset;
      }
    }
    return newOff;
  }

  public boolean isTopSymTable() {
    return parentSymTable == null;
  }

  public void setOffset(String id, Integer offset) {
    if (!offsetPerVar.containsKey(id))
      offsetPerVar.put(id, offset);
  }

  public int getOffset(String id) {
    if (parentSymTable == null && !offsetPerVar.containsKey(id)) {
      return 0;
    }

    if (!offsetPerVar.containsKey(id)) {
      return parentSymTable.getOffset(id) + getVarsSize();
    }

    return offsetPerVar.get(id);
  }

  //TODO in case size is not correct, check values() vs keys()
  // TODO size should include function calls params sizes ???????
  public int getVarsSize() {
    int totalSize = 0;
    for (Identifier identifier : dictionary.values()) {
      if (identifier instanceof VariableId) {
        totalSize += identifier.getSize();
      }
    }

    return totalSize;
  }

  public Map<String, Integer> saveOffsetPerVar() {
  Map<String, Integer> deepCopy = new HashMap<String, Integer>();
  deepCopy.putAll(offsetPerVar);
    return deepCopy;
  }


  public void setOffsetPerVar(Map<String, Integer> offsetPerVar) {
    this.offsetPerVar = offsetPerVar;
  }
}