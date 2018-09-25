package beans;


import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @Project training
 * @Author Henri Joel SEDJAME
 * @Date 25/09/2018
 * @Class purposes : .......
 */

public class SampleTest {

  public static final String HELLO_WORLD = "Hello world from cglib fixed value";
  public static final String MESSAGE = "Do not know what to do";

   Enhancer enhancer;

  @BeforeEach
  public void setUp() throws Exception {
    enhancer = new Enhancer();
    enhancer.setSuperclass(Sample.class);
  }

 @Test
  public void test_EnhancerFixedValue() {
    enhancer.setCallback(new FixedValue() {
      public Object loadObject() throws Exception {
        return HELLO_WORLD;
      }
    });
    final Sample proxy = (Sample) enhancer.create();
    assertEquals(HELLO_WORLD, proxy.test("hello"));
    assertEquals(HELLO_WORLD, proxy.toString());
  }

  @Test
  public void test_EnhancerInvocationHandler() {
      enhancer.setCallback(new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
          System.out.println(proxy.getClass().getSimpleName());
          final Class<?> declaringClass = method.getDeclaringClass();
          final Class<?> returnType = method.getReturnType();
          final int parameterCount = method.getParameterCount();

          if (declaringClass.equals(Sample.class)
               && returnType.equals(String.class)
                && parameterCount ==1){
            return HELLO_WORLD;
          }else{
            throw new RuntimeException(MESSAGE);
          }
        }
      });

    final Sample proxy = (Sample) enhancer.create();

    assertEquals(HELLO_WORLD, proxy.test("hello"));
    assertThrows(RuntimeException.class, () -> proxy.toString());
  }

  @Test
  void test_EnhancerMethodInterceptor() {
    enhancer.setCallback(new MethodInterceptor() {
      @Override
      public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println(proxy.getClass().getSimpleName());
        System.out.println(method.getName());
        System.out.println(methodProxy.getSuperName());
        final Class<?> declaringClass = method.getDeclaringClass();
        final Class<?> returnType = method.getReturnType();
        final int parameterCount = method.getParameterCount();

        if (declaringClass.equals(Sample.class)
          && returnType.equals(String.class)
          && parameterCount ==1){
          return HELLO_WORLD;
        }else{
          return methodProxy.invokeSuper(proxy, args);
        }
      }
    });
    final Sample proxy = (Sample) enhancer.create();
    assertEquals(HELLO_WORLD, proxy.test("hello"));
    proxy.hashCode();
  }
}
