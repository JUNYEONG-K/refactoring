# 데이터 체계화

## 필드 자체 캡슐화 Self Encapsulate Field
> 필드에 직접 접근하던 중 그 필드로의 결합에 문제가 생길 땐, 그 필드용 읽기/쓰기 메서드를 작성해서 두 메서드를 통해서만 필드에 접근하게 만들자.

easy peasy lemon squeezy 그 자체다.

```java
private int _low, _high;

boolean includes(int arg) {
    return arg >= _low && arg <= high;
}
```
위 코드는 필드에 직접 접근하고 있다. 메서드를 통해 접근하도록 수정하자. 왜냐고? private 선언한 필드를 다른 객체에서도 호출하게 하기 위해선 어차피 필요한 메서드다.
```java
private int _low, _high;

boolean includes(int arg) {
    return arg >= getLow() && arg <= getHigh();
}

int getLow() { return _low; }
int getHigh() { return _high; }
```
물론 이에 관해서는 의견이 나뉜다. 변수가 정의되어 있는 클래스 안에서는 변수에 자유롭게 접근할 수 있어야 한다는 `변수 직접 접근파`와
클래스 안에서라도 반드리 접근 메서드를 통해서만 접근 가능해야 한다고 주장하는 `변수 간접 접근파`이다. (난 사실 변수 직접 접근파에 가까운 듯, 클래스 내에선 그냥 접근해도 무방한 것 같음.)

필드 자체 캡슐화를 실시해야 할 가장 절실한 시점은 상위 클래스 안의 필드에 접근하되 이 변수 접근을 하위 클래스에서 계산된 값으로 재정의해야 할 때다.

## 데이터 값을 객체로 전환 Replace Data Value with Object
> 데이터 항목에 데이터나 기능을 더 추가해야 할 때는, 데이터 항목을 객체로 만들자.

예를 들어, 개발 초기에는 전화번호를 문자열로 단순히 표현해도 상관 없다. 하지만 시간이 더 흐르면 형식화, 지역번호 추출 등을 위한 특수한 기능이 필요해진다.
한두 항목 정도야 객체 안에 메서드를 넣어도 되곘지만, 금세 `중복 코드`나 `잘못된 소속`같은 코드 구린내를 풍길 것이다.

`replace_data_value_with_object/origin` 코드를 살펴보자. `Order 클래스`는 `customer`를 단순 String 문자열로 관리하고 있다.
하지만 시간이 지날수록 `cutomer`는 많은 속성과 기능을 갖게 될 것이다. 요거를 별도 클래스로 분리하자.

`replace_data_value_with_object/refactor` 패키지 안에 `Customer` 클래스를 만들었다. String 으로 관리되던 필드를 해당 클래스 안에 넣었다.
그리고 그 필드에 접근할 수 있는 getter 를 만들고, `Order` 클래스 내부에서는 메서드의 반환 타입을 수정하지 않은 채, 메서드명 변경을 통해 리팩토링을 완료했다.

```
ex. (기존)Order.getCustomer() -> Order 클래스 내부의 String _customer 를 반환했다.
Customer 클래스를 만들었으니, Order.getCustomerName() 이라는 함수명으로 변경해주었다.
```

분명히 알아두어야 할 것은, 우리는 지금 Customer 클래스를 값 객체로 취급하고 있다. 해당 객체에 주소나 신용등급 같은 속성을 추가하려면 Customer 에 값을 참조로 전환해서 한 고객의 모든 주문이 하나의 Customer 객체를 사용하게 해야 한다.

## 값을 참조로 전환 Change Value to Reference
> 클래스에 같은 인스턴스가 많이 들어 있어서 이것들을 하나의 객체로 바꿔야 할 땐, 그 객체를 참조 객체로 전환하자.

객체는 참조 객체와 값 객체로 분류할 수 있다. 참조 객체는 고객이나 계좌 같은 것이다. 각 객체는 현실에서의 한 객체에 대응하므로, 둘이 같은지 검사할 때는 객체 ID를 사용한다.
값 객체는 날짜나 돈 같은 것이다. 값 객체는 전적으로 데이터 값을 통해서만 정의된다. 사본이 있는지 신경 쓸 필요 없다. 시스템에 12/23/201 객체가 수백 개 있을 수도 있다. 두 객체가 같은지 판단할 때는 `equals`나 `hashCode` 메서드를 재정의하자.

자, 우리가 앞서 살펴봤던 Customer와 Order를 보자.

```java
public class Order {
    private final Customer _customer;

    public Order(String customerName) {
        _customer = new Customer(customerName);
    }

    public String getCustomerName() { return _customer.getName(); }

    public void setCustomerName(String customerName) {
        new Customer(customerName);
    }
}
```
Order 객체가 만들어질 때마다 Customer 객체도 새로 생성된다. 그럼 이 클래스를 사용하는 메인 코드를 보자.

```java
private static int numberOfOrdersFor(Collection orders, String customerName) {
        int result = 0;
        Iterator iter = orders.iterator();
        while (iter.hasNext()) {
            Order each = (Order) iter.next();
            if (each.getCustomerName().equals(customerName)) result++;
        }
        return result;
    }
```
얘네는 customerName 으로만 비교를 하고 있다. Customer 객체가 몇개든지 상관없이 값만 일치하면 되는 것이다.
값객체로써 활용하고 있기 때문이다. 여기에 주소나 신용등급 등의 정보를 넣기엔 곤란하다.

우리는 참조로 수정하기 위해, `고객 이름 하나당 한 개의 Customer 객체만` 만들어지도록 수정해야한다.

우선 `Customer 클래스`의 생성자를 팩토리 메서드로 전환하자. 이렇게 하면 생성 절차를 제어할 수 있다.
```java
class Customer {
    public static Customer create(String name) {
        return new Customer(name);
    }
}
```
그 다음 생성자 호출을 팩터리 메서드 호출로 수정하자.
```java
class Order {
    public Order(String customerName) {
        _customer = Customer.create(customerName);
    }
}
```

음... 나 이 다음부턴 잘 모르겠당 ㅋㅎㅋㅅ 나중에 다시 보자.

## 배열을 객체로 전환 Replace Array with Object
> 배열을 구성하는 특정 원소가 별의별 의미를 지닐 땐, 그 배열을 각 원소마다 필드가 하나씩 든 객체로 전환하자.

나는 책 내용을 먼저 읽고 요약해서 여기 작성하는데, 이 챕터는 걍 미친놈이다. 아래 코드를 살펴보자.

```java
String[] row = new String[3];
row[0] = "Liverpool";
row[1] = "15";
row[2] = "Clopp";
```
싯발 어떤 미친놈이 코드를 저따구로 적냐.

```java
Performance performance = new Performance();
performance.setName("Liverpool");
performance.setWins("15");
performance.setManager("Clopp");
```
당연히 이거지. 이 챕터는 그냥 넘어간다.
오래된 책이라 그런거겠지? 지금 난 자바 21을 쓰고 있는데, 책이 집필될 당시에는 자바 stable 이 1.1이란다. 그땐 이렇게 코드를 적는 사람이 있었을라나?
하 지금 좀 예민해진 듯, 휴식 취하고 다음 챕터 ㅎㅋ

## 관측 데이터 복제 Duplicate Observed Data
GUI 관련, 스킵하겠다.

## 클래스의 단방향 연결을 양방향으로 전환 Change Unidirectional Association to Bidirectional
> 두 클래스가 서로의 기능을 사용해야 하는데 한 방향으로만 연결되어 있을 땐, 역 포인터를 추가하고 두 클래스를 모두 업데이트할 수 있게 접근 한정자를 수정하자.

나는 단방향 연결을 선호한다. 하지만 양방향 연결이 필요할 때가 있다. 그럴 때 나는 중간에 새로운 클래스를 만드는게 좋지 않나 싶긴 한데, 그래도 어쩔 수 없이 양방향 연결이 필요한 경우가 있ㄷ.
그럴 때에는 반드시 두 클래스를 모두 업데이트 할 수 있도록 하는 메서드가 필요하다.

포인터 자체는 단방향 연결이다. 그래서 역방향으로의 포인터 참조를 추가해야한다.
단방향 포인터가 둘인 것이다.

양방향 관계를 맺은 클래스 중 하나는, 연결을 제어하는 메서드를 갖고 있어야 한다.
연결을 조작하는 로직을 전부 한 곳에 두기 위해 연결 제어 로직은 하나의 클래스에 넣는 것이 좋다.

* 일대다 -> 다 클래스를 연결 제어 클래스로 결정 (Order 와 Customer 중 Order)
* 포함 관계 -> 포함하는 객체를 연결 제어로 결정
* 다대다 -> 어디에 하든 무관

우리는 Order 클래스에 연결 제어 기능을 구현할 것이다. 이때에 헬퍼 메서드가 필요한데, 헬퍼 메서드는 주문 컬렉션으로 직접 접근할 수 있게 하는 Customer 클래스에 넣어야 한다.
Order 의 변경자는 이 헬퍼 메서드를 통해서 양측의 포인터 세트를 동시화한다.

코드는 `uni_to_bidirectional` 패키지를 참고하자.

## 클래스의 양방향 연결을 단방향으로 전환 Change Bidirectional Association to Unidirectional
> 두 클래스가 양방향으로 연결되어 있는데 한 클래스가 다른 클래스의 기능을 더 이상 사용하지 않게 됐을 때, 불필요한 양방향 연결을 끊자.

양방향 연결은 쓸모가 많지만 대가가 따른다. 즉, 양방향 연결을 유지하고 객체가 적절히 생성되고 제거되는지 확인하는 복잡함이 더해진다. 이 과정에서 익숙치 않은 사람들은 에러를 발생시킨다.

양방향 연결이 많으면 좀비 객체가 발생하기도 쉽다.
좀비 객체란 참조가 삭제되지 않아 제거되어야 함에도 남아서 떠도는 객체를 뜻한다.

양방향 연결로 인해 두 클래스는 서로 종속된다. 한 클래스를 수정하면 다른 클래스도 변경된다. 
종속성이 많으면 시스템의 결합력이 강해져서 사소한 수정에도 예기치 못한 각종 문제가 발생한다.

그래서 양방향 연결은 꼭 필요할 때만 사용해야 한다.

위 `uni_to_bidirectional` 패키지 코드를 살펴보면, Customer 가 먼저 있어야만 Order 가 있음을 알 수 있다. 따라서 Order 에서 Customer 로 가는 연결을 끊어야 한다.

## 마법 숫자를 기호 상수로 전환 Replace Magic Number with Symbolic Constant
> 특수 의미를 지닌 리터럴 숫자가 있을 땐, 의미를 살린 이름의 상수를 작성한 후 리터럴 숫자를 그 상수로 교체하자.

```java
double potentialEnergy(double mass, double height) {
    return mass * 9.81 * height;
}
```
9.81? 뭘 의미하는거지? 왜 들어갔지?
```java
static final double GRAVITATIONAL_CONSTANT = 9.81;

double potentialEnergy(double mass, double height) {
    return mass * GRAVITATIONAL_CONSTANT * height;
}
```
아하, 중력 가속도!

## 필드 캡슐화 Encapsulate Field
> public 필드가 있을 땐, 그 필드를 private 으로 만들고 필드용 읽기/쓰기 메서드를 작성하자.

객체지향의 주요 원칙 중 하나는 캡슐화이다. 캡슐화를 '데이터 은닉'이라고 부르기도 한다.
그래서 데이터는 웬만하면 public 으로 선언하지 않는 것이 좋다.
public 으로 선언되면 자기 객체도 모르는 사이에 데이터가 변경되거나 읽어질 수 있다.
데이터와 기능이 분리되는 것인데, 이건 객체를 무용지물하게 만든다.

데이터와 데이터를 사용하는 기능이 한 곳에 모여있도록 하자. 필드 캡슐화는 중요하다. 기본이다.

## 컬렉션 캡슐화 Encapsulate Collection
> 메서드가 컬렉션을 반환할 땐, 그 메서드가 읽기전용 뷰를 반환하게 수정하고 추가 메서드와 삭제 메서드를 작성하자.

클래스에 여러 인스턴스로 구성된 컬렉션이 들어 있는 경우를 흔히 볼 수 있다. 그 컬렉션은 배열, 리스트, 세트, 벡터 중 하나일 것이다.
그럴 땐 십중팔구 컬렉션을 읽고 쓸 수 있는 평범한 읽기 메서드와 쓰기 메서드가 있기 마련이다.

그러나 컬렉션은 다른 종류의 데이터와는 약간 다른 읽기/쓰기 방식을 사용해야 한다. 읽기 메서드는 컬렉션 객체 자체를 반환해선 안 된다.
왜냐하면 컬렉션 참조 부분이 컬렉션의 내용을 조작해도 그 컬렉션이 든 클래스는 무슨 일이 일어나는지 모르기 때문이다.
(ex. 다른 클래스에서 컬렉션 읽어와서 직접 그 컬렉션의 메서드로 수정할 수 있기 때문!) 
데이터 구조가 지나치게 노출된다는 것이 문제이다. 그래서 읽기 메서드는 컬렉션 조작이 불가능한 형식을 반환하고 불필요하게 자세한 컬렉션 구조 정보는 감춰야 한다.

컬렉션 쓰기 메서드는 절대 있으면 안된다. 쓰기 메서드는 메모리 주소를 바꿔버린다. 원소 추가와 삭제 메서드를 활용하자.
이를 통해 컬렉션을 적절히 캡슐화하여 참조 부분에 대한 종속성을 줄일 수 있다.

```java
import junit.framework.Assert;

import java.util.HashSet;
import java.util.Iterator;

class Course {
    public Course(String name, boolean isAdvanced) { ... };
    public boolean isAdvanced() { ... };
}

class Person {
    private Set courses;

    public Set getCourses() { return courses; }

    public void setCourses(Set arg) { courses = arg; }
}

class Main {
    public static void main(String[] args) {
        Person kent = new Person();
        Set s = new HashSet();
        s.add(new Course("스몰토크 프로그래밍", false));
        s.add(new Course("싱글몰트 위스키 음미하기", true);
        kent.setCourses(s);
        Assert.equals(2, kent.getCourses().size()));
        Course refact = new Course("리팩토링", false);
        kent.getCourses().add(refact);
        kent.getCourses().add(new Course("지독한 빈정거림", false));
        Assert.equals(4, kent.getCourses().size());
        kent.getCourses().remove(refact);
        Assert.equals(3, kent.getCourses().size());

        Iterator iter = kent.getCourses().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Course each = (Course) iter.next();
            if (each.isAdvanced()) count++;
        }
    }
}
```
자 위와 같이 코드를 초기에 작성했다. 문제가 무엇일까?
우선 `getCourses()` 메서드를 통해 알아온 객체가 또 수정이 가능하다는 것이다. add 혹은 remove 를 할 수가 있다.
또한 `setCourses()` 메서드를 통해 누군가 실수로 `new Course()`를 추가하면, 그 이전 데이터는 다 날라간다. 찾을 수가 없고, 향후 GC에 의해서 사라질 것이다.
문제가 많다 이건... 리팩토링 해보자.

add 와 remove 동작을 하는 메서드를 따로 만들고, setCourse 도 initializeCourses 라는 메서드명으로 변경해서 의미를 확실히 하자.
```java
import junit.framework.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

class Person {
    private Set courses = new HashSet();
    
    public void addCourse(Course course) { courses.add(course); }
    public void removeCourse(Course course) { courses.remove(course); }
    public void initializeCourses(Set arg) {
        Assert.isTrue(courses.isEmpty());
        courses.addAll(arg);
    }
    public Set getCourses() { return Collections.unmodifiableSet(courses); }
    
    int numberOfAdvancedCourses() {
        Iterator iter = kent.getCourses().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Course each = (Course) iter.next();
            if (each.isAdvanced()) count++;
        }
        return count;
    }
}
```
addCourse() 를 통해 new Course() 를 추가할 수 있게 되었다. 이제 우리는 course 를 읽어오고 거기다가 추가할 필요가 없다.
또한 advancedCourse 의 수를 알아내는 코드는 메서드 추출과 이동을 통해 Person 클래스에 이동하자. 그럼 최종 메인 클래스는 아래와 같다.

```java
class Main {
    public static void main(String[] args) {
        Person kent = new Person();
        kent.addCourse(new Course("스몰토크 프로그래밍", false));
        kent.addCourse(new Course("싱글몰트 위스키 음미하기", true);
        kent.initializeCourses(s);
        Assert.equals(2, kent.getCourses().size()));
        Course refact = new Course("리팩토링", false);
        kent.addCourse(refact);
        kent.addCourse(new Course("지독한 빈정거림", false));
        Assert.equals(4, kent.getCourses().size());
        kent.removeCourse(refact);
        Assert.equals(3, kent.getCourses().size());

        kent.numberOfAdvancedCourse();
    }
}
```
더 뭔가 이해가 간다. 코드가.

## 레코드를 데이터 클래스로 전환 Replace Record ith Data Class
> 전통적인 프로그래밍 환경에서 레코드 구조를 이용한 인터페이스를 제공해야할 땐, 레코드 구조를 저장할 덤 데이터 객체를 작성하자.

덤 데이터 객체(dumb data object): 데이터가 거의 들어 있지 않은 객체로, 기능 추가 없이 데이터에 public 속성이나 읽기/쓰기 메서드로 접근할 수 있다.
프로그래머 입장에선 객체지향 프로그래밍 개념(주로 기능과 데이터를 클래스 안ㄴ에 모아놔야 한다는 개념)의 정통적 개념에 위배된다. 패턴/안티패턴의 사용은 캡슐화에 완전히 위배된다.

레코드..? 뭔지 잘 모름.. ㅎ 딱히 예시 코드도 없음.

## 분류 부호를 클래스로 전환 Replace Type Code with Class
> 기능에 영향을 미치는 숫자형 분류 부호가 든 클래스가 있을 땐, 그 숫자를 새 클래스로 바꾸자.

숫자형 분류 부호(type code), 즉 열거(Enumeration) 타입은 C 언어를 기반으로 하는 각종 언어에 공통적으로 존재한다.
분류 부호 이름을 상징적인 것으로 정하면 코드가 상당히 이해하기 쉬워진다. 문제는 상징적 이름은 단지 별명에 불과하다는 점이다.
컴파일러는 어차피 내부의 숫자를 읽는다. 컴파일러는 분류 부호의 종류를 상징적 이름이 아닌 숫자를 보고 판단한다.
분류 부호를 인자로 받는 모든 메서드는 숫자만을 인자로 받으며, 상징적 이름을 전달할 방법은 없다. 그래서 코드를 이해하기 힘들어질 수 있고 버그가 생길 수도 있다.

숫자형 분류 부호를 클래스로 빼내면 컴파일러는 그 클래스 안에서 종류 판단을 수행할 수 있다.
그 클래스 안에 팩토리 메서드를 작성하면 유효한 인스턴스만 생성되는지와 그런 인스턴스가 적절한 객체로 전달되는지를 정적으로 검사할 수 있다.

분류 부호를 클래스로 만드는 건 분류 부호가 순수한 데이터일 때만 실시해야 한다. 다시 말해, 분류 부호가 switch 문 안에 사용되어
다른 기능을 수행하거나 메서드를 호출할 때는 클래스로 전환하면 안 된다.
자바의 switch 문에는 임의로 클래스를 사용할 수 없으며 오직 정수 타입만 사용 가능하기 때문이다.(지금도 그런가?)
물론 중요한 건 switch 문은 조건문 재정의 전환을 통해 전부 없애야 한다.

예제를 보는 것이 이해에 도움이 될 것 같다.

Main 클래스만 좀 비교를 해보겠다. 원본 Main 클래스는 아래와 같다.
```java
public static void main(String[] args) {
        Person personA = new Person(Person.A);
        int bloodGroup = personA.getBloodGroup();
        personA.setBloodGroup(Person.AB);
    }
```
보면 사람의 혈액형을 설정하는데, 혈액형의 값을 Person 클래스의 필드로 설정하고 있다. 아래 코드를 보자.
```java
public static void main(String[] args) {
        Person personA = new Person(BloodGroup.A);
        int bloodGroupCode = personA.getBloodGroupCode();
        personA.setBloodGroup(BloodGroup.AB);
    }
```
훨씬 자연스럽다. 각 클래스 세부 코드는 알아서 보자.

## 분류 부호를 하위클래스로 전환 Replace Type Code with Subclasses
> 클래스 기능에 영향을 주는 변경불가 분류 부호가 있을 땐, 분류 부호를 하위클래스로 만들자.'

`Employee`라는 클래스에 `ENGINEER`와 `SALESMAN`이라는 두 개 분류가 있었다 가정하자. 그럼 이걸 그냥 하위클래스로 만들어 상속 관계를 만들자는 것이다.
하위클래스로 만드는 이유는 클래스 기능에 영향을 주기 때문이다. 기능에 영향을 주지 않는다면 앞서 설명한 방법을 활용하자.

분류 부호가 클래스 기능에 영향을 미치는 현상은 case 문 같은 조건문이 있을 때 주로 나타난다. 요고 한 번 예시코드로 보자.

```java
public class OriginEmployee {
    private int type;

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    OriginEmployee(int type) {
        this.type = type;
    }
}
```
`OriginEmployee` 클래스이다. 타입 분류를 enum 으로 관리한다. 
그리고 각 역할별로 기능이 분명 다를 것 같다.
이럴 땐 하위 클래스로 만드는 것이 좋다.

```java
public abstract class Employee {
    private int type;

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    static Employee create(int type) {
        if (type == ENGINEER) return new Engineer();
        if (type == SALESMAN) return new SalesMan();
        if (type == MANAGER) return new Manager();
        throw new IllegalArgumentException("분류 부호 값이 잘못됨.");
    }
    
    Employee() {
        this.type = this.getType();
    }

    abstract int getType();
}
```
우선, 기존에 생성자가 분류부호를 인자로 받고 있었기 때문에 팩토리 메서드로 수정했다.
그리고 각 `Engineer, Salesman, Manager` 클래스가 `Employee` 클래스를 상속 받도록 구성했다.

Main 클래스를 보자.
```java
public static void main(String[] args) {
        Employee engineer = Employee.create(Employee.ENGINEER);
        SalesMan salesman = (SalesMan) Employee.create(Employee.SALESMAN);
        Employee manager = Employee.create(Employee.MANAGER);
    }
```
이렇게 각 분류부호에 따른 객체를 생성할 수 있는데, 타입 캐스팅도 가능하다.

분류 부호를 하위 클래스로 전환하는 것의 장점은 클래스 사용 부분에 있던 다형적인 기능 곤련 데이터가 클래스 자체로 이동한다는 데 있다.
변형된 새 기능을 추가할 땐 하위 클래스만 하나 추가하면 되기 때문이다.
다형성, 즉 재정의를 이용하지 않는다면 조건문을 전부 찾아서 일일이 수정해야 한다. 이 리팩토링 기법의 가치는 다형적인 기능이 수시로 변할 때 특히 빛난다.

## 분류 부호를 상태/전략 패턴으로 전환 Replace Type Code with State/Strategy
> 분류 부호가 클래스의 기능에 영향을 주지만 하위 클래스로 전환할 수 없을 땐, 그 분류 부호를 상태 객체로 만들자.

분류 부호를 하위클래스로 전환하는 방법과 비슷하지만, 분류 부호가 객체 수명주기동안 변할 때나 다른 이유로 하위클래스로 만들 수 없을 때 사용한다.

위 예제를 다시 활용할 것인데, 조건이 조금 다르다. 직급이 언제든 변할 수가 있다. 승진도 가능하고 강등도 가능하다.

```java
int payAmount() {
        if (type == ENGINEER) return monthlySalary;
        if (type == SALESMAN) return monthlySalary + commission;
        if (type == MANAGER) return monthlySalary + bonus;
        throw new IllegalArgumentException("분류 부호 값이 잘못됨.");
    }
```
`Employee` 클래스에 위와 같은 메서드가 추가되었다.

상태 클래스 `EmployeeType` 클래스를 선언해보자. 상태 클래스는 `abstract`.
그리고 그 하위클래스로 `Salesman, Manager, Engineer` 클래스를 만들자.

기존에 Employee 클래스에 int type 으로 선언했던 필드를 EmployeeType type 으로 수정하자.
그리고 분류부호도 EmployeeType 클래스로 밀어넣자.

```java
package chapter_8.replace_type_code_with_state_strategy;

public class Employee {
    private EmployeeType type;
    private int monthlySalary;
    private int commission;
    private int bonus;

    void setType(int arg) {
        type = EmployeeType.newType(arg);
    }

    int getType() {
        return type.getTypeCode();
    };

    public Employee(int type) {
        setType(type);
    }

    int payAmount() {
        if (getType() == EmployeeType.ENGINEER) return monthlySalary;
        if (getType() == EmployeeType.SALESMAN) return monthlySalary + commission;
        if (getType() == EmployeeType.MANAGER) return monthlySalary + bonus;
        throw new IllegalArgumentException("분류 부호 값이 잘못됨.");
    }
}

```

```java
package chapter_8.replace_type_code_with_state_strategy;

public abstract class EmployeeType {
    abstract int getTypeCode();

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    static EmployeeType newType(int code) {
        if (code == EmployeeType.ENGINEER) return new Engineer();
        if (code == EmployeeType.SALESMAN) return new Salesman();
        if (code == EmployeeType.MANAGER) return new Manager();
        throw new IllegalArgumentException("분류 부호가 잘못됨.");
    }
}
```

```java
public static void main(String[] args) {
        Employee engineer = new Employee(EmployeeType.ENGINEER);
        Employee salesman = new Employee(EmployeeType.SALESMAN);
        Employee manger = new Employee(EmployeeType.MANAGER);
    }
```
위와 같이 수정할 수 있다.

## 하위클래스를 필드로 전환 Replace Subclass with Fields
> 여러 하위클래스가 상수 데이터를 반환하는 메서드만 다룰 땐, 각 하위클래스의 메서드를 상위 클래스의 필드로 전환하고 하위클래스는 전부 삭제하자.

위에서 다룬 기법들의 반대 과정이라고 보면 될 것 같다. 굳이 따로 다루진 않겠다.
