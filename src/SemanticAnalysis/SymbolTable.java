package SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final SymbolTable parentSymTable;
  private final Map<String, Identifier> dictionary = new HashMap<>();

  public SymbolTable(SymbolTable parentSymTable) {
    this.parentSymTable = parentSymTable;
  }

  /* Add a name and object to the symbol table */
  public void add(String name, Identifier object) {
    dictionary.put(name, object);
  }

  /* Get an identifier object from the symbol table */
  private Identifier lookup(String name) {
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

  public boolean isTopSymTable(){
    return parentSymTable == null;
  }
}