package java.ic.doc;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;


public class DummyTest {

  DummyClass dummyObj = new DummyClass();

  @Test
  public void printsHello() {
  assertThat(dummyObj.hello(), containsString("Apple"));
}

}
