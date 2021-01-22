# :feet: API GATEWAY

</br>

## :pushpin: API Gateway란? 
Microservice Architecture에서 언급되며 모든 클라이언트의 요청의 End Point를 통합하는 서버이다. 

API Gateway에서는 보통 권한, 모니터링, 로드밸런싱 등의 역할을 한다.

클라이언트의 입장에서는 포트번호가 각기 다른 서버에게 요청을 보내는 일은 참으로 번거롭다. 이를 하나로 엮어서 API Gateway에서 처리해준다. ( 서비스의 엔드포인트 대신 API Gateway로 요청을 한다. )



</br>
</br>

## :pushpin: API Gateway 오픈소스 종류
1. Netflix의 Zuul
2. Kong
3. API Umbrella
4. tyk.io


</br>
</br>
</br>

## :pushpin: 선택한 오픈소스
- Netfilx의 zuul

<img src = https://camo.githubusercontent.com/11d70535a8f8d18b5450bbeb37330880abd20d2e359ea839796023c7398245a1/68747470733a2f2f692e696d6775722e636f6d2f6d52536f7345702e706e67>

</br>


## :pushpin: 선택한 이유
1. JAVA 언어로 되어있다.
2. API Gateway에서 많은 기능을 처리하지 않을 것이었기 때문에 간단하고 사용하기 편한 것을 선택했다.
3. Netflix가 만들었다는 신뢰감
4. 어떻게 구성되어있는지 찾아봤을 때 이해하기도 쉬웠고 PreFilter를 사용해서 간단하게 구현할 수 있을 것 같았다.


</br>
</br>

## :pushpin: 기능 구현
1. Zuul Filter의 Pre Filter를 사용하여 token이 들어오면 만료, 변조 여부 파악
2. token을 email로 바꿔서 header에 포함하여 보냄 	
	- 이유 : 유저의 정보를 알기 위해 token을 요청하는데, 이 정보를 사용하는 서비스는 항상 token을 까서 유저의 정보를 찾아야한다. 이 과정에서 동일한 코드를 사용하는 일을 줄이기 위해
	- header에 담아서 가면 유저의 개인정보에 대한 보안 이슈는 없나? 라고 했을 때, 모든 서비스는 API Gateway를 거쳐서 들어와야하고, 그 뒤에 있는 서버들은 하나의 클라우드 상에 있기 때문에 내부망 사이의 데이터 교환이기 때문에 보안이슈는 없을 것이라 판단된다.

3. Zuul이 가지고 있는 로드 밸런싱, 데이터 통계 등 여러기능은 추가하지 않았다.

</br>
</br>
</br>

## :pushpin: Netflix - zuul
<img src =https://blog.kakaocdn.net/dn/cBrBfn/btqBlz6XzgX/C4DRCKBGRtuccpXCSov2r0/img.png>

1. Pre Filter
- 실제 서비스 서버로 넘어가기 전에 token의 만료, 변조 여부 등을 확인하는 Filter

2. Routing Filter
- Http로 들어온 요청을 해당 서비스 서버에게 보내준다. 

3. POST Filter
- 서비스 서버에서 응답을 받아온 후 Response에 추가할 것이 있다면 추가하는 곳

4. Error Filter
- 에러를 처리하는 Filter

</br></br>
</br>

# :pushpin: dependencies
```
dependencies {
	implementation 'org.springframework boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-devtools'

	//zuul
	implementation 'org.springframework.cloud:spring-cloud-starter-netflix-zuul'

	//lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	//jwt
	implementation 'com.auth0:java-jwt:3.4.0'
}
```

