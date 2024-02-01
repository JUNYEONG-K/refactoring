# 객체 간의 기능 이동

객체 설계에서 가장 중요한 일 중 하나는 `기능을 어디에 넣을지 판단하는 것`이다. 첫 설계부터 고민하기보단, 리팩토링을 통해 적절한 위치를 찾는다고 생각하면 부담이 줄어든다.
`필드 이동(Move Field)`과 `메서드 이동(Move Method)`을 통해 기능을 넣을 적절한 위치를 찾을 수 있다.

클랫가 방대해지는 원인은 대개 기능이 너무 많기 떄문이다. 이럴 때는 `클래스 추출(Extract Class)`을 통해 많은 기능을 일부 분리해야 한다.
그런데 분리를 하다보니 클래스의 기능이 너무 적어지면 `클래스 내용 직접 삽입(Inline Class)`을 통해 다시 합칠 수 있다.
이 외에도 여러 방법들이 있으니 차차 시작해보자.

## 메서드 이동 Move Method

> 메서드가 자신이 속한 클래스보다 다른 클래스의 기능을 더 많이 이용할 땐, 그 메서드가 제일 많이 이용하는 클래스 안에서 비슷한 내용의 새 메서드를 작성하자.
> 기존 메서드는 간단한 대리 메서드로 전환하거나 아예 삭제하자.

클래스에 기능이 너무 많거나 클래스가 다른 클래스와 과하게 연동되어 의존성이 지나칠 때는 메서드를 옮기는 것이 좋다. 메서드를 옮기면 클래스가 간결해지고, 여러 기능을 더 명확히 구현할 수 있다.

메서드를 이동할 때는 일부 속성도 함께 이동할 때가 많다. 옮길만한 메서드를 발견하면 그 메서드를 호출하는 메서드와 그 메서드가 호출하는 메서드, 상속 계층에서 그 메서드를 재정의하는 메서드를 살펴보아야 한다.

```java
class Account {
    private AccountType _type;
    private int _daysOverdrawn;
    
    double overdraftCharge() {
        if (_type.isPremium()) {
            double result = 10;
            if (_daysOverdrawn > 7) result += (_daysOverdrawn - 7) * 0.85;
            return result;
        }
        return _daysOverdrawn * 1.75;
    }

    double bankCharge() {
        double result = 4.5;
        if (_daysOverdrawn > 0) result += overdraftCharge();
        return result;
    }
}
```
여기 몇 가지 새 계좌 유형을 추가할 예정이고, 각 계좌 유형마다 당좌대월 금액을 계산하는 공식이 다르다고 가정하자. 그럼 overdraftCharge() 메서드는 Account 클래스보다는 AccountType 클래스에 있는 것이 더 좋아보인다. 왜냐하면 계좌 유형에 영향을 받으니까!

`daysOverdrawn` 필드의 경우는 다른 메서드에서도 사용하고 있으니 Account 클래스에 그대로 두자. 우리는 `overdraftCharge()` 메서드만 옮기면 될 것 같다.

```java
class AccountType {
    double overdraftCharge(int daysOverdrawn) {
        if (isPremium()) {
            double result = 10;
            if (daysOverdrawn > 7) result += (daysOverdrawn - 7) * 0.85;
            return result;
        }
        return _daysOverdrawn * 1.75;
    }
}

class Account {
    private AccountType _type;
    private int _daysOverdrawn;
    
    double overdraftCharge() {
        return _type.overdraftCharge(_daysOverdrawn);
    }

    double bankCharge() {
        double result = 4.5;
        if (_daysOverdrawn > 0) result += overdraftCharge();
        return result;
    }
}
```
`overdraftCharge()` 메서드는 `Account 클래스`에서 호출할 것이기 때문에, 메서드 인자로 `daysOverdrawn` 필드를 받을 수 있다.
아마 유형별로 계산 로직이 다르다고 한 걸 보니 `AccountType 클래스`는 아마 추상화 상속 구조의 상위 클래스로 만들면 더 좋을 것이다. overdraftCharge()는 하위 클래스에서 구현!

이건 내 개인적인 코드인데 아래처럼 아래처럼 될 듯?

```java
interface AccountType {
    double overdraftCharge(int daysOverdrawn);
}

class AccountTypeA implements AccountType {
    @Override
    double overdraftCharge(int daysOverdrawn) {
        // 구현 코드
    }
}

class AccountTypeB implements AccountType {
    @Override
    double overdraftCharge(int daysOverdrawn) {
        // 구현 코드
    }
}

class Account {
    private AccountType _type;
    private int _daysOverdrawn;

    public Account(AccountType type, int daysOverdrawn) {
        this._type = type;
        this._daysOverdrawn = daysOverdrawn
    }

    double overdraftCharge() {
        return _type.overdraftCharge(_daysOverdrawn);
    }
}

class AccountMain {
    public static void main(String[] args) {
        Account accountA = new Account(new AccountTypeA(), 1);
        Account accountB = new Account(new AccountTypeB(), 2);
        
        double overdraftChargeA = accountA.overdraftCharge();
        double overdraftChargeB = accountB.overdraftCharge();
    }
}
```
위와 같이 작성하면 좋지 않을까? 하는 개인적인 생각이 담긴 코드 ㅋㅎ (요즘 상속과 추상화에 빠져서... 꽤나 재밌다;;)

아무튼 메서드 이동을 마저 해보자. `overdraftCharge()`라는 메서드는 `AccountType 클래스`로 이동했고, `Account 클래스`의 overdraftCharge() 메서드는 삭제를 해도 무방할 것 같다.
왜냐하면 bankCharge() 메서드에서 바로 `_type.overdraftCharge()`와 같이 호출할 수 있기 때문이다.

```java
class AccountType {
    double overdraftCharge(int daysOverdrawn) {
        if (isPremium()) {
            double result = 10;
            if (daysOverdrawn > 7) result += (daysOverdrawn - 7) * 0.85;
            return result;
        }
        return _daysOverdrawn * 1.75;
    }
}

class Account {
    private AccountType _type;
    private int _daysOverdrawn;

    double bankCharge() {
        double result = 4.5;
        if (_daysOverdrawn > 0) result += _type.overdraftCharge(_daysOverdrawn);
        return result;
    }
}
```
지금은 우리가 `_daysOverdrawn`이라는 필드만 넘기면 되지만, 만약 `Account 클래스`의 여러 필드를 필요로 했다면? 그럴 땐 객체를 직접 넘기는 것도 방법이다.

```java
class AccountType {
    double overdraftCharge(Account account) {
        if (isPremium()) {
            double result = 10;
            if (account.getDaysOverdrawn() > 7) result += (account.getDaysOverdrawn() - 7) * 0.85;
            return result;
        }
        return account.getDaysOverdrawn() * 1.75;
    }
}

class Account {
    private AccountType _type;
    private int _daysOverdrawn;

    double bankCharge() {
        double result = 4.5;
        if (_daysOverdrawn > 0) result += _type.overdraftCharge(this);
        return result;
    }
}
```

## 필드 이동 Move Field
> 어떤 필드가 자신이 속한 클래스보다 다른 클래스에서 더 많이 사용될 때는, 대상 클래스 안에 새 필드를 선언하고 그 필드 참조 부분을 전부 새 필드 참조로 수정하자.

시스템이 발전할수록 새 클래스가 필요해지고 기능을 여기저기로 옮겨야 할 필요성이 생긴다. 지금은 합리적이고 올바르다고 판단되는 설계라도 나중에는 그렇지 않을 수 있다.
문제는 그게 아니라, 그러한 상황 변화에 아무런 대처도 하지 않는 것이다.

어떤 필드가 자신이 속한 클래스보다 다른 클래스에 있는 메서드를 더 많이 참조해서 정보를 이용한다면 그 필드를 옮기는 것을 고려해보자.

```java
class Account {
    private AccountType _type;
    private double _interestRate;
    
    double interestForAmount_days (double amount, int days) {
        return _interestRate * amount * days / 365;
    }
}
```
`_interestRate` 필드를 `AccountType 클래스`로 옮기려 한다. 해당 필드를 참조하는 많은 메서드 중 하나가 `interestForAmount_days()`이다.

```java
class AccountType {
    private double _interestRate;
    
    double getInterestRate() {
        return _interestRate;
    }
    
    void setInterestRate(double interestRate) {
        _interestRate = interestRate;
    }
}

class Account {
    private AccountType _type;

    double interestForAmount_days (double amount, int days) {
        return _type.getInterestRate() * amount * days / 365;
    }
}
```

그리고 필드 이동을 하지 않더라도, public 으로 선언된 필드는 자체 캡슐화를 해서 사용하는 것이 좋다.

```java
class Account {
    private AccountType _type;
    private double _interestRate;
    
    double getInterestRate() {
        return this._interestRate;
    }
    double interestForAmount_days (double amount, int days) {
        return this.getInterestRate() * amount * days / 365;
    }
}
```

## 클래스 추출 Extract Class
> 두 클래스가 처리해야 할 기능이 하나의 클래스에 들어 있을 땐, 새 클래스를 만들고 기존 클래스의 관련 필드와 메서드를 새 클래스로 옮기자.

클래스는 확실하게 추상화되어야 하며, 두세 가지의 명확한 기능을 담당해야 한다. 클래스는 시간이 갈수록 방대해지기 마련이다.
별도의 클래스로 만들기엔 사소한 기능이 점진적으로 추가되지만, 그런 사소한 기능들이 점점 추가되다면 클래스가 상당히 복잡해진다.

그런 클래스엔 보통 많은 메서드와 데이터가 들어있고 방대해 이해하기도 어렵고, 분리하기도 어렵다.
또한 클래스를 분리하는 경우, 양방향 링크는 최대한 피하자.

요건 `extract_class 패키지 내부 코드`들을 참고하자.
간단히 요약하자면, `OriginPerson`이라는 클래스에는 `name, officeArea, officeNumber`라는 정보가 3개 있었고, `telephoneNumber`를 구하는데에는 `officeArea와 officeNumber` 정보가 필요하다.
그래서 `Telephone`이라는 새로운 클래스를 만들고, 필요한 정보와 메서드를 옮겼다. `Person` 클래스는 깔끔해졌고, 전화번호 정보는 `Telephone` 클래스에 위임하도록 구현되었다.

## 클래스 내용 직접 삽입 Inline Class
> 클래스 기능이 너무 적을 땐, 그 클래스의 모든 기능을 다른 클래스로 합쳐 놓고 원래의 클래스는 삭제하자.

`inline_class 패키지 내부 코드`들을 참고하자. 이건 클래스 추출의 반대 과정이다. 다만 예시 코드는 모두 옮기고 클래스나 메서드를 삭제하진 않을 것이다.

기존에는 아래와 같이 코드를 작성해야 했을 것이다. (클래스 추출의 결과)
```java
Person martin = new Person();
martin.getOfficeTelehone().setAreaCode("781");
```
이제는 요고를 아래처럼 바꿀 수 있다.
```java
Person martin = new Person();
martin.setAreaCode("781");
```

## 대리 객체 은폐 Hide Delegate
> 클라이언트가 객체의 대리 클래스를 호출할 땐, 대리 클래스를 감추는 메서드를 서버에 작성하자.

객체에서 핵심 개념 중 하나는 캡슐화이다. 캡슐화란 객체가 시스템의 다른 부분에 대한 정보의 일부만 알 수 있게 은폐하는 것을 뜻한다. 객체를 캡슐화하면 무언가를 변경할 때 그 변화를 전달해야 할 객체가 줄어들므로 변경하기 쉬워진다.

흠... 대리 객체 잘 모르겠다. 일단 따라가보자.

자 `hide_delegate/origin 패키지 내부 코드`를 살펴보자. 만약 클라이언트가 누군가의 부서장을 알고자한다면 어떻게 해야할까?
아래와 같이 코드를 작성해야 할 것이다.
```java
manager = john.getDepartment().getManager();
```
위 코드의 문제는 무엇일까?
클라이언트는 Person 뿐 아니라 Department 클래스의 원리를 알 수 있다. Department 의 기능에 팀장을 알아내는 것이 있구나!~

이런 의존성은 좋지 않다. 클라이언트가 Department 클래스의 원리를 알 필요는 없다.
```java
manager = john.getManager();
```
위와 같이 호출할 수 있도록 수정하자(Person 클래스 안에 getManager() 메서드 선언 & getDepartment() 메서드 삭제). Department 를 Person 뒤에 은닉화하는 것이다.

## 과잉 중개 메서드 제거 Remove Middle Man
> 클래스에 자잘한 위임이 너무 많을 땐, 대리 객체를 클라이언트가 직접 호출하게 하자.

대리 객체 은폐에 반대라고 볼 수 있다. 대리 객체 은폐를 통해 만들어진 중개 메서드가 투머치라고 생각하면 원복하면 된다.
은폐의 적절한 정도를 안다면 참 좋을텐데 그러기가 어렵다. 그래서 어떨 때는 은폐했다가, 어떨 때는 클라이언트가 직접 호출하게 하고, 필요할 때마다 리팩토링하면 된다.

리팩토링에서 후회는 불필요하다.

```java
manager = john.getDepartment().getManager();
```
다시 요롷게 하도록 하면 된다.

## 외래 클래스에 메서드 추가 Introduce Foreign Method
> 사용 중인 서버 클래스에 메서드를 추가해야 하는데 그 클래스를 수정할 수 없을 땐, 클라이언트 클래스 안에 서버 클래스의 인스턴스를 첫 번째 인자로 받는 메서드를 작성하자.

음 나는 이 부분은 건너 뛰겠다. 수정할 수 없는 코드는 없다. 성역은 없다는 의미이다. 과감히 수정하자. 수정할 수 없을 때 다시 와서 보자.

물론 기본 클래스들이 있다. 예를 들면, `Date` 클래스. 해당 클래스는 우리가 수정할 수 없다. `nextDay()` 라는 메서드가 필요하면 클라이언트에서 만들자. 너무 당연한 얘기다. 너무 당연한 얘기라 더 알지 않아도 될 것 같다.
그래도 코드를 볼까

```java
Date newStart = new Date(previousEnd.getYear(), previousEnd.getMonth(), previousEnd.getDate() + 1);
```
이렇게 하지 말고 아래처럼 하자는 말이다.
```java
Date newStart = nextDay(previousEnd);

private static Date nextDay(Date previousEnd) {
    return new Date(previousEnd.getYear(), previousEnd.getMonth(), previousEnd.getDate() + 1);
}
```

## 국소적 상속확장 클래스 사용 Introduce Local Extension
> 사용 중인 서버 클래스에 여러 개의 메서드를 추가해야 하는데 클래스를 수정할 수 없을 땐, 새 클래스를 작성하고 그 안에 필요한 여러 개의 메서드를 작성하자. 이 상속확장 클래스를 원본 클래스의 하위 클래스나 래퍼 클래스로 만들자.

외래 클래스에 메서드 추가의 확장판이다.

