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

  /* Update the offset for every entry in the symbol table that has
     an offset less than paramOffset */
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

  /* Set offset for a specific identifier */
  public void setOffset(String id, Integer offset) {
    if (!offsetPerVar.containsKey(id))
      offsetPerVar.put(id, offset);
  }

  /* Get offset of a specific identifier from the symbol table or
     or it's enclosing symbol tables */
  public int getOffset(String id) {
    if (parentSymTable == null && !offsetPerVar.containsKey(id)) {
      return 0;
    }

    if (!offsetPerVar.containsKey(id)) {
      return parentSymTable.getOffset(id) + getVarsSize();
    }

    return offsetPerVar.get(id);
  }

  /* Get size of all variables stored in the symbol table */
  public int getVarsSize() {
    int totalSize = 0;
    for (Identifier identifier : dictionary.values()) {
      if (identifier instanceof VariableId) {
        totalSize += identifier.getSize();
      }
    }

    return totalSize;
  }

  /* Get offset of each variable stored in the symbol table */
  public Map<String, Integer> saveOffsetPerVar() {
    return new HashMap<>(offsetPerVar);
  }

  /* Set offset of each variable stored in the symbol table */
  public void setOffsetPerVar(Map<String, Integer> offsetPerVar) {
    this.offsetPerVar = offsetPerVar;
  }
}