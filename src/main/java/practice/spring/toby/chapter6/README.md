# AOP
AOP는 IoC/DI, 서비스 추상화와 더불어 스프링의 3대 기반기술의 하나다. AOP를 바르게 이용하려면 OOP를 대체하려고 하는 것처럼 보이는 AOP라는 이름 뒤에 감춰진, 그 필연적인 등장배경과 스프링이 그것을 도입한 이유, 그 적용을 통해 얻을 수 있는 장점이 무엇인지에 대한 충분한 이해가 필요하다. 스프링에 적용된 가장 인기 있는 AOP의 적용대상은 선언적 트랜잭션 기능이다.

### 트랜잭션 코드의 분리
스프링이 제공하는 깔끔한 트랜잭션 인터페이스를 사용하였음에도 불구하고 비즈니스 로직의 주인이어야할 메서드 안에 트랜잭션 코드가 더 많은 자리를 차지하고 있다. 그렇다고해서 논리적으로 트랜잭션 코드를 다른 로직에서 처리하는 것은 맞지않다.

##### 메서드 분리
```JAVA
public void upgradeLevels() {
		// 트랜잭션 추상화 API를 적용
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); 
		
		// 비즈니스 로직 시작.
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (changeUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
		// 비즈니스 로직 끝.
		
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		} 
	}
```

비즈니스 로직 코드를 사이에 두고 트랜잭션 시작과 종료를 담당하는 코드가 앞뒤에 위치하고 있다. 또 이 코드의 특징은 트랜잭션 경계설정의 코드와 비즈니스로직 코드 간에 서로에게 요청하는 정보가 없다는 것이다. 비즈니스 로직에서 직접 DB를 사용하지 않기 때문에 트랜잭션 준비 과정에서 만들어진 DB 커넥션 정보 등을 직접 참조할 필요가 없다. 따라서 이 두 가지 코드는 성격이 다를 뿐 아니라 서로 주고받는 것도 없는 완벽하게 독립적인 코드다.

##### DI 적용을 이용한 트랜잭션 분리
구체적인 구현 클래스를 직접 참조하는 경우의 전형적인 단점이다. 직접 사용하는 것이 문제가 된다면 간접적으로 사용하면된다. DI의 기본 아이디어는 실제 사용할 오브젝트의 클래스 정체는 감춘 채 인터페이스를 통해 간접적으로 접근하는 것이다. 그 덕분에 구현 클래스는 얼마든지 외부에서 변결할 수 있다. 바로 이런 개념을 가진 DI가 지금 필요하다. 
(예제 practice.spring.toby.chapter6 : UserServiceTx, UserServiceImple )