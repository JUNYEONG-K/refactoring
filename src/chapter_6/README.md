# 메서드 정리

리팩토링의 주된 작업은 코드를 포장하는 메서드를 적절히 정리하는 것이다. 거의 모든 문제점은 장황한 메서드로 인해 생긴다.
장황한 메서드에는 많은 정보가 들어 있는데, 마구 얽힌 복잡한 로직에 이 정보들이 묻혀버린다.

* `메서드 추출 Extract Method`: 코드 뭉치를 별도의 메서드로 빼낸다.
* `메서드 내용 직접 삽입 Inline Method`: 호출되는 메서드의 내용을 호출하는 메서드에 직접 넣는다. 너무 쪼개놓은 경우라거나, 인자를 넘기기 어려운 경우 등에 사용하면 좋을 것 같다.
* `임시변수를 메서드 호출로 전환 Replace Temp with Query`: 메서드 추출에서 가장 힘든 작업이 임시 지역변수를 처리하는 것인데, 이를 처리하기 위함. 이를 통해 임시변수 제거!
* `임시변수 분리 Split Temporary Variable`: 임시변수가 여러 부분에 사용될 때 먼저 실시, 이후 메서드 호출로 전환
* `메서드를 메서드 객체로 전환 Replace Method with Method Object`: 임시변수가 너무 얽혀있어서 메서드 호출로 전환할 수 없을 때, 새 클래스를 만들어서 심각하게 얽혀 있는 메서드를 분리해보자.
* `매개변수로의 값 대입 제거 Remove Assignments to Parameters`
* `알고리즘 전환 Substitute Algorithm`: 메서드를 잘게 쪼개었는데, 동일한 기능을 하지만 알고리즘이 다른 메서드를 발견했을 때, 간단한 것으로 통일 혹은 대체

## 메서드 추출 Extract Method

> 어떤 코드를 그룹으로 묶어도 되겠다고 판단될 땐, 그 코드를 빼내어 목적을 잘 나타내는 직관적 이름의 메서드로 만들자.

직관적인 이름의 간결한 메서드를 여러개 만들자.
* 메서드가 적절히 잘게 쪼개져 있ㅇ면 다른 메서드에서 쉽게 사용할 수 있다.
* 상위 계층의 메서드에서 주석 같은 더 많은 정보를 함수명으로 읽어들일 수 있다.
* 재정의하기도 훨씬 수월하다.

메서드의 길이가 중요한 것이 아니다. 메서드명과 메서드 내용이 1:1로 일치해야한다. `메서드는 하나의 일만 잘하면 된다.`

```java
import java.util.Enumeration;

void printOwning() {
    Enumeration e = orders.elements();
    double outstanding = 0.0;
    
    // 배너 출력
    System.out.println("*******************");
    System.out.println("******고객 외상******");
    System.out.println("*******************");
    
    // 외상액 계산
    while(e.hasMoreElements()) {
        Order each = (Order) e.nextElement();
        outstanding += each.getAmount();
    }
    
    // 세부 내열 출력
    System.out.println("name = " + name);
    System.out.println("outstanding = " + outstanding);
}
```

최초 코드이다. 문제점을 찾아보자.

1. 주석으로 나열된 기능들
2. 임시변수 outstanding

1번부터 리팩토링 해보자. 각 기능들을 함수명으로만 알 수 있게 메서드로 추출하자.

```java
import java.util.Enumeration;

void printOwning() {
    Enumeration e = orders.elements();
    double outstanding = 0.0;
    
    printBanner();
    outstanding = getOutstanding(outstanding);
    printDetails(outstanding);
}

void printBanner() {
    System.out.println("*******************");
    System.out.println("******고객 외상******");
    System.out.println("*******************");
}

void getOutstanding(double outstanding) {
    while(e.hasMoreElements()) {
        Order each = (Order) e.nextElement();
        outstanding += each.getAmount();
    }
}

void printDetails(double outstanding) {
    System.out.println("name = " + name);
    System.out.println("outstanding = " + outstanding);
}
```
이렇게 메서드를 기능별로 잘게 쪼개두었다. 메서드명만으로 해당 기능을 파악할 수 있기 때문에 주석도 필요 없다.

자 이번엔 `outstanding` 임시변수를 제거해보자. e라는 변수도 제거할 수 있다. 해당 변수가 다른 곳에서 사용되지 않기 때문. (orders는 아마 클래스의 속성으로 선언되어있을 것이다.)

```java
import java.util.Enumeration;

void printOwning() {
    printBanner();
    double outstanding = getOutstanding();
    printDetails(outstanding);
}

void printBanner() {
    System.out.println("*******************");
    System.out.println("******고객 외상******");
    System.out.println("*******************");
}

double getOutstanding(double outstanding) {
    Enumeration e = orders.elements();
    double result = 0.0;
    while(e.hasMoreElements()) {
        Order each = (Order) e.nextElement();
        result += each.getAmount();
    }
    return result;
}

void printDetails(double outstanding) {
    System.out.println("name = " + name);
    System.out.println("outstanding = " + outstanding);
}
```
printOwning 메서드가 매우 깔끔해졌다. 오 그런데 outstanding 변수를 굳이 선언해야할까싶기도 하다.
그냥 `printDetails(getOutstanding())`과 같이 수정할 수도 있을 것 같다. `임시변수를 메서드 호출로 전환(Replace Temp with Query)`하는 것이다.

## 메서드 내용 직접 삽입 Inline Method

> 메서드 기능이 너무 단순해서 메서드명만 봐도 너무 뻔할 땐, 그 메서드의 기능을 호출하는 메서드에 넣어버리고 그 메서드는 삭제하자.

얜 간단하다. 그냥 너무 투머치하게 잘게 쪼개져서 오히려 가독성을 떨어트리는 경우에 하면 된다.
아래 코드로 살펴보자.

```java
int getRating() {
    return (moreThanFiveLateDeliveries()) ? 2 : 1;
}

private boolean moreThanFiveLateDeliveries() {
    return numberOfLateDeliveries > 5;
}
```

이건 뭐... 다른 메서드에서도 `moreThanFiveLateDeliveries()` 메서드를 사용하는 것이 아니라면 굳이 싶다 ㅎㅎ.

아래처럼 수정하자.

```java
int getRating() {
    return (numberOfLateDeliveries > 5) ? 2 : 1;
}
```

과다한 인다이렉션과 동시에 모든 메서드가 다른 메서드에 단순히 위임을 하고 있어서 코드가 지나치게 복잡할 때 주로 실시한다. 다만 해당 메서드가 재정의되어 있지는 않은지, 호출하는 부분이 더 없는지 등을 확인하자.

## 임시변수 내용 직접 삽입 Inline Temp

> 간단한 수식을 대입받는 임시변수로 인해 다른 리팩토링 기법 적용이 힘들 땐, 그 임시변수를 참조하는ㄴ 부분을 전부 수식으로 치환하자.

임시변수는 없앨 수 있으면 없애는 것이 좋다.

```java
double basePrice = anOrder.basePrice();
return (basePrice > 1000);
```

너무 명확하게 없애고 싶다.

```java
return (anOrder.basePrice() > 1000);
```

## 임시변수를 메서드 호출로 전환 Replace Temp with Query

> 쉭의 결과를 저장하는 임시변수가 있을 땐, 그 수식을 빼내어 메서드로 만든 후, 임시변수 참조 부분을 전부 수식으로 교체하자. 새로 만든 메서드는 다른 메서드에서도 호출 가능하다.

```java
double basePrice = quantity * itemPrice;

if (basePrice > 1000) return basePrice * 0.95;
return basePrice * 0.98;
```

basePrice 임시변수를 없애자. 함수 호출로 대체!

```java
if (basePrice() > 1000) return basePrice() * 0.95;
return basePrice() * 0.98;

double basePrice() {
    return quantity * itemPrice;
}
```

임시변수는 일시적이고 적용이 국소적 범위로 제한된다는 단점이 있다. 임시변수는 자신이 속한 메서드의 안에서만 인식되므로, 그 임시변수에 접근을 한다거나 같은 임시변수를 다른 메서드에서 활용하려면 코드가 길어지고 중복이 생긴다.
그래서 임시변수를 메서드 호출로 수정하면, 클래스 안의 모든 메서드가 그 정보에 접근할 수 있어, 코드가 훨씬 깔끔해진다.

지역변수가 많을수록 메서드 추출이 힘들어진다. 그래서 메서드 추출 이전에 임시변수를 미리 메서드 호출로 전환해두는 것이 좋다.

간단한 예시를 살펴보자.
```java
double getPrice() {
    int basePrice = quantity * itemPrice;
    double discountFactor;
    if (basePrice > 1000) discountFactor = 0.95;
    else discountFactor = 0.98;
    return basePrice * discountFactor;
}
```

임시변수가 `basePrice`와 `discountFactor`가 있는 것으로 보인다. 쟤네 둘 다 메서드 호출로 대체할 수 있을 것 같다.
이걸 잘 알 수 있는 방법은, 의심되는 변수에 `final` 키워드를 붙여봐라. `final` 키워드가 붙으면 값을 한 번 대입하고 이후 수정할 수 없다.

최종 코드는 아래와 같다.
```java
double getPrice() {
    return basePrice() * discountFactor();
}

private double discountFactor() {
    return basePrice() > 1000 ? 0.95 : 0.98;
}

private int basePrice() {
    return quantity * itemPrice;
}
```

오우 너무 깔끔하다. 변수 선언도 덜 하니 코드도 줄고, 성능에서도 미세하게나마 ㅎㅎ 개선이 있을 것이다. 변수가 차지하는 메모리 공간이 줄어들었을 듯

## 직관적 임시변수 사용 Introduce Explaining Variable

> 사용된 수식이 복잡할 땐, 수식의 결과나 수식의 일부분을 용도에 부합하는 직관적 이름의 임시변수에 대입하자.

영문으로 Introduce `Explaining` Variable 이다. `Explaining`, 설명할 수 있어야 한다는 것이다. 이름만 보고 '아~' 싶어야할 정도로 직관적인 이름을 사용하자.

```java
if((platform.toUpperCase().indexOf("MAC") > -1) &&
    (browser.toUpperCase().indexOf("IE") > -1) &&
    wasInitialized() && resize > 0) {
        // 실행 코드
}
```

오우 벌써 너무 복잡하다. 이 코드를 적는 것부터가 복잡했다. 그리고 각 조건이 뭘 의미하는지도 모르겠다. 무수한 괄호 행진... 다 변수로 선언해버리자.

```java
final boolean isMacOs = platform.toUpperCase().indexOf("MAC") > -1;
final boolean isIEBrowser = browser.toUpperCase().indexOf("ID") > -1;
final boolean wasResized = resize > 0;

if (isMacOs && isIEBrowser && wasInitialized() && wasResized()) {
        // 실행 코드
}
```

좋다... 각 변수명만 봐도 뭘 의미하는지 알 수 있다. 조건을 파악하기가 너무 쉽다. 저 변수의 로직은 우리가 굳이 보지 않아도 된다.

물론 직관적 임시변수보다는 메서드로 추출해서 하는 것이 조금은 더 권장된다. 하지만 그렇게 하지 못할 케이스들이 충분히 많다.

```java
double price() {
    // 결제액(price) = 총 구매액(base price) - 대량 구매 할인(quantity discount) + 배송비(shipping)
    return quantity * itemPrice;
            - Math.max(0, quantity - 500) * itemPrice * 0.05
            + Math.min(quantity * itemPrice * 0.1, 100.0);
}
```

위 코드들을 보면 각 항목들이 뭘 나타내는지 알기가 쉽지가 않다. 나중에 뭔가 수정하려해도 어디를 수정해야할 지 알기 쉽지 않다.
모두 의미를 알 수 있도록 변수명을 붙여서 의미를 나타내보자.

```java
double price() {
    final double basePrice = quantity * itemPrice;
    final double quantityDiscount = Math.max(0, quantity - 500) * itemPrice * 0.05;
    final double shipping = Math.min(basePrice * 0.1, 100.0);
    
    return basePrice - quantityDiscount + shipping;
}
```
위와 같은 작업을 통해 우리는 `basePrice` 변수를 재활용할 수 있고, 함수가 반환하는 것의 의미를 더 잘 나타낼 수 있다. 주석이 필요가 없다!

그런데 위처럼 basePrice, quantityDiscount, shipping 같은 것들은 해당 클래스에서 재사용될 가능성들이 높다. 만약 다른 함수에서 해당 변수 중 하나라도 활용을 하고 싶다면 코드 중복이 발생할 것이다.
직관적 임시변수 사용이 아닌 메서드 추출로 해결해보자. 그러면 클래스 내의 다른 메서드들에서 사용하기 편해질 것이다.

```java
double price() {
    return basePrice() - quantityDiscount() + shipping();
}

private double basePrice() {
    return quantiity * itemPrice;
}
private double quantityDiscount() {
    return Math.max(0, quantity - 500) * itemPrice * 0.05;
}

private double shipping() {
    Math.min(basePrice * 0.1, 100.0);
}
```
이렇게만 보면 위 코드보다 코드도 길고 효과가 미미해보이지만, 코드 재활용성이 높아졌다는 것과 클래스 내의 모든 곳에서 해당 데이터에 접근할 수 있다는 장점을 취할 수 있다.

## 임시변수 분리 Split Temporary Variable

> 루프 변수나 값 누적용 임시변수(collecting temporary variable)가 아닌 임시변수에 여러 번 값이 대입될 땐, 각 대입마다 다른 임시변수를 사용하자.

```java
double temp = 2 * (heigt + width);
System.out.println(tmp);
temp = height * width;
System.out.println(tmp);
```
위 코드를 살펴보면, `temp`라는 임시변수를 선언하고 그 변수에 계속 값을 덮어 쓰고 있다. 덮어 쓰는 값의 계산식이 다르기 때문에 매번 의도가 다름에도 하나의 변수로 처리하고 있다.
메모리 입장에선 좋으려나? 좋겠지. 그치만 우리는 이걸 볼 때 얘가 뭘 의미하는지 추적을 하기가 어렵다. 첫 번째 temp는 둘레를, 두 번째 temp는 넓이를, 그 뒤 나중 temp는 어떤 용도로 쓰일까...
그래서 임시변수는 그 의미를 직관적으로 나타내야한다.

```java
final double perimeter = 2 * (height + width);
System.out.println(perimeter);
final double area = height * width;
System.out.println(area);
```
임시변수의 선언과 할당이 그 쓰임에 맞게 알맞게 되었다.

아래 해기스(haggis) 이동거리를 계산하는 함수를 리팩토링 해보자.
```java
double getDistanceTravelled(int time) {
    double result;
    double acc = _primaryForce / _mass; // f = ma
    int primaryTime = Math.min(time, _delay);
    result = 0.5 * acc * primaryTime * primaryTime;
    int secondaryTime = time - _delay;
    if (secondaryTime > 0) {
        double primaryVel = acc * _delay;
        acc = (_primaryForce + _secondaryForce) / _mass;
        result += primaryVel * secondaryTime + 0.5 * acc * secondaryTime * secondaryTime;
    }
    return result;
}
```
요거는 함수를 쪼개기엔 무리가 있다. 이미 충분히 쪼개진 함수의 결과물이라 생각이 든다.

* `acc` 변수가 두 번 대입되는데, 그 두 번의 용도가 조금 다르다. 첫번째는 초기 가속도를, 두번째는 추가 가속도를 저장한다. 변수를 분리하자!

```java
double getDistanceTravelled(int time) {
    double result;
    final double primaryAcc = _primaryForce / _mass; // f = ma
    int primaryTime = Math.min(time, _delay);
    result = 0.5 * primaryAcc * primaryTime * primaryTime;
    int secondaryTime = time - _delay;
    if (secondaryTime > 0) {
        double primaryVel = primaryAcc * _delay;
        final double secondaryAcc = (_primaryForce + _secondaryForce) / _mass;
        result += primaryVel * secondaryTime + 0.5 * secondaryAcc * secondaryTime * secondaryTime;
    }
    return result;
}
```
`acc`라는 하나의 변수로 관리되던 것을 `primaryAcc`와 `secondaryAcc`로 분리했다.

더 리팩토링 해볼까? 책은 우선 여기까지만 안내한다.

```java
double getDistanceTravelled(int time) {
    final double primaryAcc = acc(_primaryForce, _mass);
    final int primaryTime = Math.min(time, _delay);
    final double primaryDistance = distance(primaryAcc, primaryTime);
    
    final int secondaryTime = time - _delay;
    if (secondaryTime <= 0) return primaryDistance;
    
    final double primaryVel = primaryAcc * _delay;
    final double additionalDistance = primaryVel * secondaryTime;
    
    final double secondaryAcc = acc((_primaryForce + _secondaryForce), _mass);
    final double secondaryDistance = distance(secondaryAcc, secondaryTime);
    
    return primaryDistance + additionalDistance + secondaryDistance;
}

private double acc(double force, double mass) {
    return force / mass;
}

private double distance(double acc, int time) {
    return 0.5 * acc * time * time;
}
```
요게 그나마 최선...? 헤헹, 더 좋아진지는 모르겠다 ㅎㅎ

## 매개변수로의 값 대입 제거 Remove Assignments to Parameters

> 매개변수로 값을 대입하는 코드가 있을 땐, 매개변수 대신 임시변수를 사용하게 수정하자.

뭔 개소리일까, 코드로 살펴보자.
```java
int discount(int inputVal, int quantity, int yearToDate) {
    if (inputVal > 50) inputVal -= 2;
}
```

```java
int discount(int inputVal, int quantity, int yearToDate) {
    int result = inputVal;
    if (inputVal > 50) result -= 2;
}
```

아하 코드를 보니 이제야 이해가 간다. 자바는 변수에 값을 할당할 때, 값을 복사해서 할당한다. 처음 작성된 코드를 보면, 매개변수로 넘어온 `inputVal` 인자에 직접 2를 감소한다.
그럼 처음 넘어온 값을 우리가 직접 건드리게 되어, 함수의 밑부분에서는 우리가 original 값을 활용할 수 없다.
그래서 임시변수 `result`를 선언해서 `inputVal` 변수의 값을 복사해서 넣어두고 그걸 조작하자. `original 값은 보존하자.`

자바는 `값을 통한 전달`만 사용한다. (`참조를 통한 전달` 도 있지만, 자바엔 없다.)

```java
void aMethod(Object foo) {
    foo.modifyInSomeWay(); // 괜찮다.
    foo = anotherObject;   // 고통과 절망을 안겨줄 것이다.
}
```
객체는 사실 그 객체가 위치한 메모리 저장소의 주소값을 담고 있다. 그런데 다른 객체를 대입한다는 것은, 객체가 가리키는 메모리 주소를 바꿔버리는 것이다. 엉뚱한 객체를 가지고 우린 놀고 있게 될 것이다.

그래서 매개변수를 직접 건드리는 코드를 보면, 그러지 말고 임시변수를 선언해서 사용하자.
간단한 예시는 아래와 같다.
```java
int discount1(int inputVal, int quantity, int yearToDate) {
    if (inputVal > 50) inputVal -= 2;
    if (quantity > 100) inputVal -= 1;
    if (yearToDate > 10000) inputVal -= 4;
    return inputVal;
}

int discount2(int inputVal, int quantity, int yearToDate) {
    int result = inputVal;
    if (inputVal > 50) result -= 2;
    if (quantity > 100) result -= 1;
    if (yearToDate > 10000) result -= 4;
    return result;
}

int discount2(final int inputVal, final int quantity, final int yearToDate) {
    int result = inputVal;
    if (inputVal > 50) result -= 2;
    if (quantity > 100) result -= 1;
    if (yearToDate > 10000) result -= 4;
    return result;
}
```
물론 지금 코드에서 당장 discount1() 메서드가 큰 문제를 일으키진 않는다. 메서드가 작으면 작을수록 문제를 일으킬 것 같진 않지만, 그래도 이건 늘 염두에 두고 작업하는게 좋겠다. 특히 객체를 건드릴 때!!

요고를 강제하는 방법도 있다. 함수의 매개변수에 모두 `final ` 키워드를 붙이는 것이다. 근데 이건 뭐 굳이... 싶긴 하다. ㅎㅎ 그래도 그런 방법이 있다 정도 알아두면 됨!

객체 관련 대표적인 예시는 아래와 같다. 설명은 생략

```java
import java.util.Date;

class Param {
    public static void main(String[] args) {
        Date d1 = new Date("1 Apr 98");
        nextDateUpdate(d1);
        System.out.println("nextDate 메서드 실행 후 d1 값: " + d1);
        
        Date d2 = new Date("1 Apr 98");
        nextDateUpdateReplace(d2);
        System.out.println("nextDate 메서드 실행 후 d2 값: " + d2);
    }
    
    private static void nextDateUpdate(Date arg) {
        arg.setDate(arg.getDate() + 1);
        System.out.println("nextDate 메서드 안의 arg 값: " + arg);
    }

    private static void nextDateUpdateReplace(Date arg) {
        arg = new Date(arg.getYear(), arg.getMonth(), arg.getDate() + 1);
        System.out.println("nextDate 메서드 안의 arg 값: " + arg);
    }
}
```

```
실행결과:
    nextDate 메서드 안의 arg 값: Thu Apr 02 00:00:00 EST 1998
    nextDate 메서드 실행 후 d1 값: Thu Apr 02 00:00:00 EST 1998 // 함수 실행 후 1 증가했다.
    nextDate 메서드 안의 arg 값: Thu Apr 02 00:00:00 EST 1998
    nextDate 메서드 실행 후 d2 값: Wed Apr 01 00:00:00 EST 1998 // 함수 실행 후에도 1일 증가하지 않았다.
```
PersonMain 파일을 한 번 찬찬히 읽어보면 이해에 도움이 될 것!

## 메서드를 메서드 객체로 전환 Replace Method with Method Object

> 지역변수 때문에 메서드 추출을 할 수 없는 긴 메서드가 있을 땐, 그 메서드 자체를 객체로 전환해서 모든 지역변수를 객체의 필드로 만들자. 그런 다음 그 메서드를 객체 안의 여러 메서드로 쪼개면 된다.

리팩토링 책이 강조하는 핵심은 `간결한 메서드의 아름다움`이다. 장황한 메서드에서 각 부분을 간결한 메서드로 빼내면 코드가 훨씬 이해하기가 쉬워진다.

메서드 분해를 어렵게 만드는 것은 `지역변수`이다. 지역변수가 많으면 메서드를 쪼개기 힘들 수 있다. 임시변수를 메서드 호출로 전환하면 이런 어려움이 어느 정도 해소는 되지만, 메서드로 분해할 수 없을 때도 있다.
이럴 때 우리는 메서드 객체를 활용해야 한다.

메서드를 메서드 객체로 전환하면, 메서드의 모든 지역변수가 객체의 속성이 된다.

```java
class Account {
    int gamma(int inputVal, int quantity, int yearToDate) {
        int importantValue1 = (inputVal * quantity) + delta();
        int importantValue2 = (inputVal * yearToDate) + 100;
        if ((yearToDate - importantValue1) > 100) importantValue2 -= 20;
        int importantValue3 = importantValue2 * 7;
        // 기타 작업
        return importantValue3 - 2 * importantValue1;
    }
}
```
위의 요 `gamma` 메서드를 메서드 객체로 전환하자. 

1. 메서드명으로 새 클래스를 생성하자.
2. 원본 객체를 나타내는 final 필드를 작성하고, 원본 메서드 안의 매개변수와 임시변수를 필드로 작성하자.
3. 원본 객체와 기존 매개변수를 인자로 받는 생성자를 만들자.
4. 원본 메서드를 옮겨오자. (주로 `compute`라는 메서드명으로 옮겨옴.)
5. 원본 메서드가 이 메서드 객체로 위임하게 작성하자.

```java
class Gamma {
    private final Account _account;
    
    private int inputVal;
    private int quantity;
    private int yearToDate;
    
    private int importantValue1;
    private int importantValue2;
    private int importantValue3;
    
    Gamma(Account account, int inputVal, int quantity, int yearToDate) {
        _account = account;
        this.inputVal = inputVal;
        this.quantity = quantity;
        this.yearToDate = yearToDate;
    }
    
    int compute() {
        importantValue1 = (inputVal * quantity) + _account.delta();
        importantValue2 = (inputVal * yearToDate) + 100;
        if ((yearToDate - importantValue1) > 100) importantValue2 -= 20;
        importantValue3 = importantValue2 * 7;
        // 기타 작업
        return importantValue3 - 2 * importantValue1;
    }
}

class Account {
    int gamma(int inputVal, int quantity, int yearToDate) {
        return new Gamma(this, inputVal, quantity, yearToDate);
    }
}
```

## 알고리즘 전환
> 알고리즘을 더 분명한 것으로 교체해야 할 땐, 해당 메서드의 내용을 새 알고리즘으로 바꾸자.

```java
import java.util.Arrays;
import java.util.List;

String foundPerson1(String[] people) {
    for (int i = 0; i < people.length; i++) {
        if (people[i].equals("Don")) return "Don";
        if (people[i].equals("John")) return "John";
        if (people[i].equals("Kent")) return "Kent";
    }
    return "";
}

String foundPerson2(String[] people) {
    for (String person : people) {
        if (person.equals("Don")) return "Don";
        if (person.equals("John")) return "John";
        if (person.equals("Kent")) return "Kent";
    }
    return "";
}

String foundPerson3(String[] people) {
    List<String> candidates = Arrays.asList("Don", "John", "Kent");
    for (int i = 0; i < people.length; i++) {
        if (candidates.contains(people[i])) return people[i];
    }
    return "";
}

String foundPerson4(String[] people) {
    List<String> candidates = Arrays.asList("Don", "John", "Kent");
    for (String person : people) {
        if (candidates.contains(person)) return person;
    }
    return "";
}
```
점진적으로 코드를 수정해보았다.
