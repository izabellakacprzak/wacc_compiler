package ic.doc.semantic_analysis;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final SymbolTable parentSymTable;
    private Map<String, Identifier> dictionary;

    public SymbolTable(SymbolTable parentSymTable) {
        this.parentSymTable = parentSymTable;
        dictionary = new HashMap<>();
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
    private Identifier lookupAll(String name) {
        Identifier currentObject = this.lookup(name);

        if (currentObject == null && this.parentSymTable != null) {
            return parentSymTable.lookupAll(name);
        }
        return currentObject;
    }
}