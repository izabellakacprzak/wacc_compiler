package InternalRepresentation;

import InternalRepresentation.Enums.Condition;

public class ConditionCode {
  private final Condition condition;

  public ConditionCode(Condition condition) {
    this.condition = condition;
  }

  public String getCondName() {
    return condition.name();
  }

}

