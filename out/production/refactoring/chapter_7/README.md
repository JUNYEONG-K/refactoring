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


