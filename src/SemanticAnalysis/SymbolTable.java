package SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final SymbolTable parentSymTable;
  private final Map<String, Identifier> dictionary = new HashMap<>();
  int argStackOffset = 0;

  public SymbolTable(SymbolTable parentSymTable) {
    this.parentSymTable = parentSymTable;
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

  public boolean isTopSymTable() {
    return parentSymTable == null;
  }

  public void incrementArgStackOffset(int argSize) {
    argStackOffset += argSize;
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
}