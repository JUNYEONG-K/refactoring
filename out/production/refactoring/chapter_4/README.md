# 테스트 작성

> 리팩토링을 실시하기 위한 필수 전제조건은 견고한 테스트 코드가 작성되어 있어야 한다는 것이다.

## 자가 테스트 코드의 가치

테스트 코드를 작성하지 않는다면, 프로그래밍의 속도는 빠를 수 있겠지만 디버깅하는데 온종일 시간을 쓰게 된다. 버그를 찾기 어렵기 때문이다.

코드를 잘게 쪼개고, 잘게 쪼개진 곳마다 테스트를 붙이면 세밀하고 빠르게 자주 테스트할 수 있고, 버그를 찾기도 쉽다.

테스트를 먼저 작성하자. 
테스트를 작성하면 그 기능을 추가하려고 해야 할 작업이 무엇인지 자문하게 된다. 
그리고 테스트를 작성하면 구현부가 아니라 인터페이스에 집중하게 된다. 
인터페이스에 집중하는 것은 예외없이 너무나도 바람직하다. 
또한 코딩을 완료하는 시점도 테스트가 성공하는 시점으로 분명해진다.

## 테스트 코드 작성

`Junit 사용`

테스트를 작성할 때에는 일부러 통과하지 않아야 하는 케이스도 넣어보는 것이 좋다. 진짜 테스트가 잘 동작하고 있는 것인지 확인하기 위해

```java
public void testRead() throws IOException {
        char ch = '&';
        for (int i = 0; i < 4; i++) {
            ch = (char) input.read();
        }
        assert('d' == ch);
    }
```
위 코드는 통과를 해야하는 테스트 코드이다.

```java
public void testRead() throws IOException {
        char ch = '&';
        for (int i = 0; i < 4; i++) {
            ch = (char) input.read();
        }
        assert('2' == ch);
    }
```
위 코드는 통과를 하면 안되는, 즉 실패해야 하는 테스트 코드이다. 이게 정상적으로 실패를 하는지도 확인해보자. 만약 성공한다면 문제가 있는 것이다.

```java
public void testRead() throws IOException {
        char ch = '&';
        input.close();
        for (int i = 0; i < 4; i++) {
            ch = (char) input.read();
        }
        assert('d' == ch);
    }
```
이 코드는 에러를 뱉어야 한다. 에러를 뱉는지도 확인해보자. 실패와 에러를 뱉는 것은 다르다.

```java
public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new FileReaderTest("testRead"));
        suite.addTest(new FileReaderTest("testReadAtEnd"));
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
```
main 메서드를 실행하면 suite() 메서드 안에 작성된 테스트 케이스가 실행된다. 각 테스트 케이스는 `setUp()`, `테스트 코드`, `tearDown()`을 순차적으로 실행한다.
테스트가 서로 독립적으로 실행되게 하려면 `setUp 메서드`와 `tearDown 메서드`를 반드시 실행해야 한다.

하지만 suite() 메서드 안에 테스트 케이스를 매번 나열하기는 쉽지 않다. 분명 까먹을 일이 생길 것이다.
그래서 아래와 같이 main 메서드를 수정하면 suite() 메서드 필요 없이, test 로 시작하는 모든 메서드의 테스트 케이스가 실행된다.
```java
//    public static Test suite() {
//        TestSuite suite = new TestSuite();
//        suite.addTest(new FileReaderTest("testRead"));
//        suite.addTest(new FileReaderTest("testReadAtEnd"));
//        return suite;
//    }

    public static void main(String[] args) {
//        junit.textui.TestRunner.run(suite());
        junit.textui.TestRunner.run(new TestSuite(FileReaderTest.class));
    }
```
TestSuite 생성자가 test 로 시작하는 이름의 모든 메서드의 테스트 케이스가 든 테스트 suite 을 생성한다.

## 단위 테스트와 기능 테스트

Junit은 단위 테스트(unit test)용 프레임워크이다.

단위테스트의 목적은 `프로그래밍 생산성 향상`이다. 프로그래밍 생산성이 높아지면 부수적으로 품질 보증 부서의 업무 효율도 향상된다.
단위 테스트는 매우 국소적이어서, 각 테스트 클래스는 하나의 패키지 안에서만 효력이 있다.

기능 테스트는 단위 테스트와 전혀 다르다. 기능 테스트의 목적은 소프트웨어 전반이 제대로 돌아가는지 확인하는 것이다.
이는 고객에게 품질 보증만 할 뿐 프로그래머의 생산성과는 무관하다. 따라서 기능 테스트 코드는 별도의 버그 발견 전문 팀이 개발해야 한다.
기능 테스트를 위해선 별도의 강력한 도구들을 활용해야 한다.

## 뭘 어떻게 얼마나 테스트 해야할까?

모든 public 메서드 테스트하기는 지양한다. `테스트는 위험을 위주로 작성해야 한다.` 읽기/쓰기 같은 간단한 메서드에 대한 테스트는 생략을 해도 좋다는 말이다.

테스트 작성이 부담으로 느껴지는 수준에선 테스트 코드를 작성할 필요가 없다.

테스트를 실시할 때는 성공 케이스도 물론 중요하지만, 예상한 에러가 제대로 발생하는지 검사하는 것도 중요하다.

다시 말하지만 테스트는 우선 위험이 있는 곳에만 집중시켜야 한다.
코드에서 복잡해지는 부분이 어딘지를 파악하고, 함수를 살펴보면서 에러 가능성이 높은 부분들을 생각해보자. 혹은 비즈니스적으로 중요한 부분!

객체의 한가지 단점은 상속과 재정의로 인해 테스트하 조합이 너무 많아져서 테스트가 어려워질 수 있다는 점이다.

테스트는 언제 중단할까? 언제까지 할까? 적당히.. 적당히 버그를 잡을 수 있을 정도까지만...
