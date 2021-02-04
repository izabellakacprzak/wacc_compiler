import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;


public class DummyTest {

  ASTVisitor visitor = new ASTVisitor();

  @Test
  public void printsHello() {
    assertThat(visitor.hello(), containsString("Hello"));
  }
}