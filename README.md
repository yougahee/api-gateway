# :feet: API GATEWAY

</br>

## :pushpin: API Gateway란? 
Microservice Architecture에서 사용하며 모든 클라이언트의 요청의 End Point를 통합하는 서버이다. 

API Gateway에서는 Authentication, Monitoring, load balancing, security 등의 역할을 한다.

클라이언트의 입장에서는 포트번호가 각기 다른 서버에게 요청을 보내는 일은 참으로 번거롭다. 이를 하나로 엮어서 API Gateway에서 처리해준다. ( 서비스의 엔드포인트 대신 API Gateway로 요청한다. )

</br>
</br>

## :pushpin: API Gateway 오픈소스 종류
1. Netflix의 Zuul
2. AWS의 API gateway
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
2. API Gateway에서 권한 여부와 서비스 서버로의 라우팅 기능만 사용할 예정이었기 때문에 예제와 레퍼런스가 많은 것을 선택했다. 
3. Netflix사의 깔끔한 Blog 정리 및 GitHub 꾸준한 Update
4. 어떻게 구성되어있는지 찾아봤을 때 이해하기도 쉬웠고 PreFilter를 사용해서 간단하게 구현할 수 있을 것 같았다.


</br>
</br>


## :pushpin: 기능 구현
1. Zuul Filter의 Pre Filter를 사용하여 header에 token이 존재하면 만료, 변조 여부 파악한다.

2. token에 담겨있는 user의 email, nickname, userIdx를 decode한 후, header에 포함하여 보낸다.
	- 이유   
		거의 모든 서비스 서버에서 유저의 정보를 얻기 위해 token이 필요하다. 
		각 서비스 서버마다 token의 값을 decode한 후 유저의 정보를 얻을 수 있다.
		API Gateway에서 token을 decode해서 header에 추가하면 다른 서버에서 동일한 코드를 반복해서 사용할 필요가 없다. 
	- header에 담아서 가면 유저의 개인정보에 대한 보안 이슈는 없나?    
		- 모든 서비스는 API Gateway를 거쳐서 들어와야하고, 그 뒤에 있는 서버들은 하나의 클라우드 상에 있기 때문에 내부망 사이의 데이터 교환이기 때문에 보안이슈는 없을 것이라 판단된다.
		- token에 보안 이슈가 일어날만한 정보를 넣는 것 자체도 말이 안된다고 생각한다. ( 언제든 탈취당할 수 있기 때문에 )

3. Zuul이 가지고 있는 로드 밸런싱, 데이터 통계 등 여러 기능은 추가하지 않았다.

</br>
</br>
</br>

## :pushpin: 개발하면서 겪은 일 
1. token에 담겨있는 정보들을 헤더에 추가하면서 "x-forward-userIdx"라고 명명해놓은 아이에 null값이 들어갔다.      
request header의 로그를 찍어보았는데 "x-forward-useridx"라고 적혀서 들어가졌다.      
어라? 난 분명 userIdx라고 적었는데 왜 useridx라고 적혀있지?해서 찾아보았더니 HTTP 헤더는 *대소문자를 구분하지 않는다*고 한다.     
그래서 userIdx -> useridx라고 적고 테스트를 해보았다. 하지만, 여전히 null값!!       
사실 문제는 header에 있지 않았다. token에 useridx값을 long으로 집어넣었는데 decode할 때는 string으로 빼내고 있었던 것! long으로 바꿔주니 바로 해결되었다.       
++ userIdx, useridx 라고 적어도 값은 정상적으로 잘 들어가고 빼낼 수 있다.

<br>

2. API Gateway에서 ZuulException 이 떴다. 
	- 상황 : Auth의 회원가입 시, 이메일 인증코드 확인까지 완료한 후, 또 요청을 했을 경우 "이미 인증했던 이력이 있습니다" 이런 식에 메세지가 가도록 처리해놓았다.    
	이 메세지를 모낸 것은 오류? 느낌은 아니었고 굳이 귀찮은 일을 2번 할 필요가 없다는 메세지로 보내기 위함이었다.    
	그래서 StatusCode를 100번 HTTP.Continue로 줬다.
	Auth에서는 잘 넘어가는데, API Gateway에서 Error Filter로 넘어가서 에러를 내뱉었다.
	- 이유가 뭘까??
		- ㅇ


3. HTTP header에 닉네임을 넣고 싶다!
- 한글은 깨져서 보내지는 문제
</br>
</br>
</br>

## :pushpin: Zuul Core Architecture

<img src=https://miro.medium.com/max/1250/1*j9iGkeQ7bPK2nC1a7BgFOw.png>


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
	implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.7.0'
	implementation 'com.auth0:java-jwt:3.4.0'

	//gson
	compile group: 'com.google.code.gson', name: 'gson', version: '2.7'
}
```

</br>
<br>

:pushpin: 참고문서/출처
1. https://netflixtechblog.com/announcing-zuul-edge-service-in-the-cloud-ab3af5be08ee
2. https://github.com/Netflix/zuul

