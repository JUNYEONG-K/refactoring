# 비디오 대여점 프로그램

* 비디오 대여점에서 고객의 대여료 내역을 계산하고 출력하는 프로그램
* 방대한 프로그램의 일부라고 가정하자
* 고객이 대여한 비디오와 대여 기간을 표시한 후, 비디오 종류와 대여 기간을 토대로 대여료를 계산한다.
* 비디오 종류는 일반물과 아동물, 최신물 세 종류이다.
* 적립 포인트 계산 -> 비디오가 최신물인지 아닌지에 따라 달라진다.

## origin 프로그램

* 설계도 엉터리, 객체지향적이지도 않다.
* Customer 클래스의 내역 산출 루틴인 statement 메서드가 너무 길다. (지나치게 많은 기능)

하지만 요 코드는 문제 없이 잘 돌아간다. 컴파일러는 코드가 지저분하든 말든 개의치 않는다.

그럼 우리는 이 코드를 왜 뜯어고치고 싶을까? 뜯어고쳐야 할까?

코드 수정할 부분을 쉽게 찾고, 이로 인한 사이드 이펙트를 줄이고자! 수정할 부분과 영향을 미치는 부분 최소화!

> 수정할 위치를 찾기 힘들면 프로그래머가 실수할 가능성이 높아진다. 이는 버그 발생을 유발한다.

#### 만약 사용자의 대여 내역을 웹에서도 볼 수 있고, 다른 대중적 형식과도 호환되게끔 HTML로도 출력되길 원한다는 요구사항이 추가된다면?

* 현재의 statement 메서드의 어떠한 기능도 HTML 내역 출력에 재사용할 수 없다.
* 이에 대한 임시방편은 statement 메서드와 대부분의 기능이 같은 새 메서드를 작성하는 것이다. (많은 코드 중복 예상됨.)

#### 만약 대여료 적용 규칙을 수정하면 어떻게 될까?

* statement 메서드와 htmlStatement 메서드를 둘 다 똑같이 수정해야 한다.
* 달라지는 요구사항마다 매번 두 함수에 똑같은 수정을 복붙해야한다.

#### 비디오 분류를 변덕스럽게 자주 바꾼다면?

* 그에 따라 고객의 비디오 대여료와 적립 포인트 계산 방식도 바뀐다.
* 요청사항은 정말 자주 바뀐다. 그로 인한 사이드 이펙트를 요청자는 모른다. 코드를 작성하는 우리가 고생할 뿐이다.

위와 같은 여러 상황들에 대해, 현재 코드는 유연하게 대처하지 못한다.
비디오 종류별 대여료 계산 규칙을 바꾸려면 statement 메서드와 htmlStatement 메서드를 동일하게 수정해야 하고, 이미 복잡한 로직에서 어떤 부분을 수정해야할지 찾기 쉽지 않다.

복잡한 로직의 일부를 수정하고 나면 신뢰성도 떨어진다. (내 경험상, 그래서 테스트 코드가 중요한데, statement 메서드는 테스트 코드 짜기도 .. ;;)

## 리팩토링의 첫 단계

> 리팩토링할 코드 부분에 대한 신뢰도 높은 각종 테스트 작성

## statement 메서드 분해와 기능 재분배

너무 긴 메서드를 보면 누구나 쪼개고 싶은 생각이 드는 것 같다. 읽기도 힘들고...

우선 해당 메서드를 분해해서 각 부분을 알맞은 클래스로 옮겨보자. 이를 통해 중복을 줄이고 HTML로 내역을 출력하는 메서드를 좀 더 간편하게 작성할 수 있을 것이다. (코드 재활용)

### amountFor 메서드 추출 (메서드 추출 (Extract Method))

* 메서드 안에서만 효력이 있는 모든 지역변수와 매개변수에 해당하는 부분을 살펴봐야 한다.
  * each, thisAmount
  * each 변수는 코드로 인해 변경되지 않고, thisAmount는 변경된다.
  * ```변경되지 않는 변수는 매개변수로 전달할 수 있다. 변경되는 변수는 주의가 필요하다.```
* ```amountFor``` 이라는 함수로 분리를 했다. 또한 독립된 함수로 분리되었기 때문에 해당 함수내에서 변수명도 수정을 해주었다.
  * ```each -> rental```: 해당 함수는 enumable 하게 호출되는지 모른다. 알 필요가 없다. 인자로 들어오는 Rental 객체에 대해서 처리하면 된다.
  * ```thisAmount -> result```: this 라는 키워드로 강조할 필요 없다. 그저 함수의 결과가 중요할 뿐이다.
  * 변수명, 함수명은 중요하다. ```용도가 확실히 드러나야 한다.```

> 컴퓨터가 인식 가능한 코드는 바보라도 작성할 수 있지만, 인간이 이해할 수 있는 코드는 실력 있는 프로그래머만 작성할 수 있다.

```java
class Customer {
  {
    ...
    thisAmount = amountFor(each);
    ...
  }

  private double amountFor(Rental rental) {
    double result = 0;
    switch (rental.getMovie().getPriceCode()) {
      case Movie.REGULAR -> {
        result += 2;
        if (rental.getDaysRented() > 2)
          result += (rental.getDaysRented() - 2) * 1.5;
      }
      case Movie.NEW_RELEASE -> {
        result += rental.getDaysRented() * 3;
      }
      case Movie.CHILDREN -> {
        result += 1.5;
        if (rental.getDaysRented() > 3)
          result += (rental.getDaysRented() - 3) * 1.5;
      }
    }
    return result;
  }
}
```

### 대여료 계산 메서드(amountFor) 옮기기 (메서드 이동 (Move Method))

그런데 ```amountFor``` 메서드를 가만 보면, 인자로 받아온 Rental 클래스의 정보를 이용하지만, 정작 자신이 속한 Customer 클래스의 정보는 이용하지 않는다.

그럼 해당 메서드가 Customer 클래스에 남아있어야 할 이유가 있을까?

객체는 관련 필드와 메서드를 모아두기 위해 만들어졌다. 그런데 기능이 동떨어져있는 기분?

해당 메서드를 Rental 클래스로 옮겨보자. 그리고 해당 클래스에 맞게 수정해보자. (getCharge)

```java
class Customer {
    {
    ...
        thisAmount = each.getCharge();
    ...
    }
}

class Rental {
  public double getCharge() {
    double result = 0;
    switch (this.getMovie().getPriceCode()) {
      case Movie.REGULAR -> {
        result += 2;
        if (this.getDaysRented() > 2)
          result += (this.getDaysRented() - 2) * 1.5;
      }
      case Movie.NEW_RELEASE -> {
        result += this.getDaysRented() * 3;
      }
      case Movie.CHILDREN -> {
        result += 1.5;
        if (this.getDaysRented() > 3)
          result += (this.getDaysRented() - 3) * 1.5;
      }
    }
    return result;
  }
}
```

수정된 코드는 위와 같다.
이제는 Rental 객체의 기능으로 옮겨왔기 때문에, 인자를 받을 필요가 없어졌다. 함수명도 바뀌었다.
특정 객체에 대한 amount를 구하는 ```amountFor(Rental rental)``` 이 아니라, 특정 객체의 charge를 구하는 ```getCharge()```로 바뀌었다.

### thisAmount 임시변수 제거 (임시변수를 메서드 호출로 전환 (Replace Temp with Query))

statement 메서드를 마저 보면, ```thisAmount``` 변수는 무의미하게 선언되고 0으로 초기화되어 있다. 요거는 그냥 메서드를 호출하는 방식으로 변환할 수 있다.

이런 임시변수는 최대한 없애는 것이 좋다.

임시변수가 많으면 불필요하게 많은 매개변수를 전달하게 되는 문제가 흔히 발생한다. 또한 임시변수의 용도를 쉽게 잊어버린다. (ㅇㅈ)

임시변수는 특히 긴 메서드 안에서 알게 모르게 늘어나는데, 이는 당연히 성능을 떨어뜨리기도 한다.

### 적립 포인트 계산을 메서드로 빼기 (메서드 추출 (Extract Method))

적립 포인트 계산법 또한 비디오 종류에 따라 달라진다. 따라서 요것도 Rental 클래스 안으로 옮기자.

```java
class Customer {
  {
    ...
    frequentRenterPoints += each.getFrequentRenterPoints();
    ...
  }
}

class Rental {
  public int getFrequentRenterPoints() {
    // 최신물을 이틀 이상 대여하면 2포인트 지급하고 그 외엔 1포인트 지급하는 코드
    if (this.getMovie().getPriceCode() == Movie.NEW_RELEASE
            && this.getDaysRented() > 1) return 2;
    return 1;
  }
}
```

개인적으로 함수명이... 마음에 들지 않는다. Customer 클래스에서 frequentRenterPoints 를 인자로 받아, 1 혹은 2를 증가시킨 결과를 반환했으면 어떨까 싶다.
그래야 해당 함수명이 올바르게 사용된 것 아닌가 싶은?

like this... 근데 다시 생각해보니 인자로 넘기는게 좋은 것 같진 않다. 왜냐면 frequentRenterPonints는 애초에 임시변수이기 때문에 없애는게 제일 좋지 않을까?

```java
class Customer {
  {
    ...
    frequentRenterPoints = each.getFrequentRenterPoints(frequentRenterPoints);
    ...
  }
}

class Rental {
  public int getFrequentRenterPoints(int frequentRenterPoints) {
    // 최신물을 이틀 이상 대여하면 2포인트 지급하고 그 외엔 1포인트 지급하는 코드
    if (this.getMovie().getPriceCode() == Movie.NEW_RELEASE
            && this.getDaysRented() > 1) return frequentRenterPoints + 2;
    return frequentRenterPoints + 1;
  }
}
```

### totalAmount 임시변수와 frequentRenterPoints 임시변수 없애기 (임시변수를 메서드 호출로 전환 (Replace Temp with Query))

1. totalAmount 없애기 -> getTotalCharge()

```java
class Customer {
  private double getTotalCharge() {
    double result = 0;
    Enumeration rentals = this.rentals.elements();
    while (rentals.hasMoreElements()) {
      Rental each = (Rental) rentals.nextElement();
      result += each.getCharge();
    }
    return result;
  }

  private int getTotalFrequentRenterPoints() {
    int result = 0;
    Enumeration rentals = this.rentals.elements();
    while (rentals.hasMoreElements()) {
      Rental each = (Rental) rentals.nextElement();
      result += each.getFrequentRenterPoints();
    }
    return result;
  }
}
```

## 잠시 정지

지금까지의 코드 리팩토링을 생각해보자.

우선 방금 전 단계에서, 코드의 양은 오히려 많아졌다. statement 메서드 내의 while문 하나로 해결했던 것을, 각 개별 분리된 메서드에서도 while문을 호출해야하니, while문도 총 3번이나 호출된다.
이는 성능저하를 유발한다.

그럼 이런 이유로 이 리팩토링은 의미가 없는가? 그렇지 않다. while문 리팩토링은 겁낼 필요가 없고, 향후 최적화로 해결할 수 있다.

이제 잠시 리팩토링을 멈추고 기능을 추가해보자. htmlStatement()

우리는 계산 부분을 독립 함수로 빼두었기 때문에, 코드 중복을 최소한으로 하여 htmlStatement() 메서드를 작성할 수 있다.

> 새로운 요구사항이 생겼다. 비디오 분류를 완전히 다른 방식으로 변경할거란다. 지금의 코드로는 대응이 힘들다.

우선 이 요구사항을 위해 리팩토링을 마저 진행하자. 대여료 메서드와 적립 포인트 메서드를 마무리 짓고 조건문 코드를 수정해서 비디오 분류를 변경해두어야 한다.

### 가격 책정 부분의 조건문을 재정의로 교체

제일 먼저 고칠 부분은 switch 문이다.

> 타 객체의 속성을 switch 문의 인자로 하는 것은 나쁜 방법이다. switch 문의 인자로는 타 객체 데이터를 사용하지 말고 자신의 데이터를 사용해야 한다.

아래 코드를 보면 switch 문의 인자가 Movie 객체의 priceCode 타입이다. 그럼 이건 Rental 클래스가 아니라 Movie 클래스로 옮겨야한다.
```java
class Rental {
  public double getCharge() {
    double result = 0;
    switch (this.getMovie().getPriceCode()) {
      case Movie.REGULAR -> {
        result += 2;
        if (this.getDaysRented() > 2)
          result += (this.getDaysRented() - 2) * 1.5;
      }
      case Movie.NEW_RELEASE -> {
        result += this.getDaysRented() * 3;
      }
      case Movie.CHILDREN -> {
        result += 1.5;
        if (this.getDaysRented() > 3)
          result += (this.getDaysRented() - 3) * 1.5;
      }
    }
    return result;
  }
}
```
함수를 옮길 때는 그 클래스에 맞게 변화해야 한다. (매개변수가 추가 혹은 삭제, 변경될 수 있고 메서드명이 수정될 수도 있다.)

```java
class Movie {
  public double getCharge(int daysRented) {
    double result = 0;
    switch (this.priceCode) {
      case Movie.REGULAR -> {
        result += 2;
        if (daysRented > 2)
          result += (daysRented - 2) * 1.5;
      }
      case Movie.NEW_RELEASE -> result += daysRented * 3;
      case Movie.CHILDREN -> {
        result += 1.5;
        if (daysRented > 3)
          result += (daysRented - 3) * 1.5;
      }
    }
    return result;
  }
}
```
Movie는 daysRented 정보를 알 수 없기 때문에 (Rental 클래스에 있음.), 이를 인자로 받도록 메서드를 구성했다.

다음으로는 적립 포인트 계산 메서드도 옮겨보자.

```java
class Rental {
  public int getFrequentRenterPoints() {
    // 최신물을 이틀 이상 대여하면 2포인트 지급하고 그 외엔 1포인트 지급하는 코드
    if (this.getMovie().getPriceCode() == Movie.NEW_RELEASE
            && this.getDaysRented() > 1) return 2;
    return 1;
  }
}
```

위의 기존 메서드를 보면, 조건문의 인자가 Movie 클래스의 속성이다. 이건 뭐 앞서 말한 것과 같은 이유로 Movie 클래스로 옮길 필요가 있겠다.

### 상속 구조 만들기

Movie 클래스는 비디오 종류에 따라 같은 메서드를 호출해도 각기 다른 값을 반환한다.
하지만 이것은 하위클래스에서 처리할 일이다.

> Movie <- RegularMovie(getCharge), ChildrenMovie(getCharge), NewReleaseMovie(getCharge)

위와 같은 상속 구조를 통해 하위 클래스에서 구현하자.
여기에 4인방의 상태 패턴을 추가로 적용하자. (이유는 잘 모르겠다. 왜?? 요 부분은 나 살짝 이해가 안되려하네... ㅎ)

> 요 부분은 복습 한 번 하셈. 근데 코드가 아름답게 수정되었다. 이게 추상화지 씨발ㅋㅋ

