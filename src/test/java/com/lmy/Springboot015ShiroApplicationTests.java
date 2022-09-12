package com.lmy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lmy.dao.UserDao;
import com.lmy.entity.Perm;
import com.lmy.entity.User;
import com.lmy.shiro.realm.CustomerRealm;
import com.lmy.shiro.realm.TestRealm;
import com.lmy.utils.ApplicationContextUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@SpringBootTest
class Springboot015ShiroApplicationTests {
	@Autowired
	private UserDao userDao;

	private final String salt = "!@#$%";	//JWT密钥
	private String str = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6ImUyNTdmYjE3YmNkYTQ4YjU0YzkxZDJmOTE5ODA5OWFkIiwidXNlck5hbWUiOiJsdW9tdXlvdSIsImV4cCI6MTYzNzIxMjEzMCwicGFzc3dvcmROb3RTYWx0IjoibHVvbXV5b3UifQ.5qarQjeKNVy3A93NNQHHwdb4pA4W5GbbBU-wzss_1BE";

	@Test
	public void ttt(){
		RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
		System.out.println(redisTemplate);
	}
	@DisplayName("模拟第一次登录JWTtoken生成")
	@Test
	public void test01(){
		//JWT生成
		Map<String,Object> map = new HashMap<>();   //
		Calendar instance = Calendar.getInstance();
		System.out.println(instance.getTime());
		instance.add(Calendar.DATE,7);   //过期时间，Calendar.SECOND表示秒数，7天
		System.out.println(instance.getTime());

		String token = JWT.create()
				//.withHeader(map)   //header头，加不加这个header不影响
				.withClaim("password","e257fb17bcda48b54c91d2f9198099ad")//原始值
				.withClaim("passwordNotSalt","luomuyou")	//准备经过shiro处理
				.withClaim("userName","luomuyou")        //payload负载
				.withExpiresAt(instance.getTime())
				.sign(Algorithm.HMAC256(salt));  //秘钥签名,HMAC256加密
		System.out.println("token=\n"+token);
	}
	@DisplayName("JWT验证,模拟二次请求携带第一次生成的token操作")
	@Test
	public void test02(){
		//JWT验证
		JWTVerifier build = JWT.require(Algorithm.HMAC256(salt)).build();
		DecodedJWT verify = build.verify(str);
		String header = verify.getHeader();
		String payload = verify.getPayload();
		String token = verify.getToken();
		String signature = verify.getSignature();
		System.out.println("header:"+header);
		System.out.println("payload:"+payload);
		System.out.println("signature:"+signature);
		System.out.println("token:"+token);
		//获取token中的payload部分，然后进行解码，再输出指定的片断值
		String userName = verify.getClaim("userName").asString();
		String password = verify.getClaim("password").asString();
		System.out.println("password="+password);
		System.out.println("userName="+userName);
		System.out.println("过期时间="+verify.getExpiresAt());
		//System.out.println("sssssss="+verify.getClaims());

		User user = new User();
		user.setUsername(userName);
		user.setPassword(password);

		User login = userDao.login(user);//用户名和密码登录，然后获得全部信息
		System.out.println("用户名和密码登录，然后获得全部信息\n"+login);
	}
	@DisplayName("JWT验证,模拟二次请求携带第一次生成的token操作,配合shiro")
	@Test
	public void test022(){
		//JWT验证
		JWTVerifier build = JWT.require(Algorithm.HMAC256(salt)).build();
		DecodedJWT verify = build.verify(str);
		//获取token中的payload部分，然后进行解码，再输出指定的片断值
		String userName = verify.getClaim("userName").asString();
		String password = verify.getClaim("passwordNotSalt").asString();
		String password2 = verify.getClaim("passwordNotSalt").asString();
		System.out.println("passwordNotSalt："+password);
		System.out.println("userName："+userName);
		//System.out.println("sssssss="+verify.getClaims());

		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");   //默认用md5来处理密文
		hashedCredentialsMatcher.setHashIterations(1024);    //再设置散列的次数
		CustomerRealm realm = new CustomerRealm();
		realm.setCredentialsMatcher(hashedCredentialsMatcher);
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm);
		//全局安全工具类设置安全管理器
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		//类似把用户名和密码包装起来准备拿去认证
		UsernamePasswordToken token = new UsernamePasswordToken(userName,password);
		try {
			//1、用户认证，会调用CustomerRealm中设置得认证规则【1】
			subject.login(token);
			//shiro认证成功之后，
			User user = new User();
			user.setUsername(userName);
			user.setPassword(password2);

			User login = userDao.login(user);//用户名和密码登录，然后获得全部信息
			System.out.println("用户名和密码登录，然后获得全部信息\n"+login);

		} catch (UnknownAccountException e) {
			e.printStackTrace();
			System.out.println("用户名错误");
		} catch (IncorrectCredentialsException e) {
			e.printStackTrace();
			System.out.println("密码错误");
		}

	}



	@DisplayName("通过MD5加密之后的认证流程")
	@Test
	public void test03(){

		/*修改凭证校验匹配器
		用户登录使用的密码明文与数据库中保存的密码密文不匹配，需要先把输入的密码明文转化为md5密文之后再匹配*/
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		//设置realm使用的hash凭证
		hashedCredentialsMatcher.setHashAlgorithmName("md5");   //默认用md5来处理密文
		hashedCredentialsMatcher.setHashIterations(1024);    //再设置散列的次数

		TestRealm testRealm = new TestRealm();
		//使用创建出来的凭证校验匹配器,设置经过md5处理在对比，而不是默认的直接equals对比
		testRealm.setCredentialsMatcher(hashedCredentialsMatcher);

		//1.安全管理器
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(testRealm);
		//全局安全工具类设置安全管理器
		SecurityUtils.setSecurityManager(securityManager);
		
		//全局安全工具类获得主体Subject
		Subject subject = SecurityUtils.getSubject();
		//token令牌进行用户认证
		UsernamePasswordToken token = new UsernamePasswordToken("lq","lq");
		try {
			System.out.println("认证状态："+subject.isAuthenticated());
			//用户认证,认证不通过会报异常，调转到我们自定义实现AuthorizingRealm类认证和授权方法中
			subject.login(token);
			System.out.println("认证状态："+subject.isAuthenticated());
		}catch (UnknownAccountException e) {	//捕捉异常判定错误
			e.printStackTrace();
			System.out.println("用户名错误");
		} catch (IncorrectCredentialsException e) {
			e.printStackTrace();
			System.out.println("密码错误");
		};


	}

	@DisplayName("shiro认证流程")
	@Test
	public void test04(){
		//安全管理器
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

		//注册一个realm，shiro.ini文件用来测试阶段不用假设用户设置
		securityManager.setRealm(new IniRealm("classpath:shiro.ini"));	//略

		//全局安全工具类设置安全管理器
		SecurityUtils.setSecurityManager(securityManager);


		//全局安全工具类获得主体Subject
		Subject subject = SecurityUtils.getSubject();
		//token令牌进行用户认证
		UsernamePasswordToken token = new UsernamePasswordToken("lmy","123");
		try {
			System.out.println("认证状态："+subject.isAuthenticated());
			//用户认证,认证不通过会报异常，调转到我们自定义实现AuthorizingRealm类认证和授权方法中
			subject.login(token);
			System.out.println("认证状态："+subject.isAuthenticated());
		}catch (Exception e){
			e.printStackTrace();
		}


	}


	@DisplayName("MD5加密测试")
	@Test
	public void test05(){
		Md5Hash md5Hash = new Md5Hash("luomuyou", "ZIC%uQ6#",1024);
		System.out.println("MD5加密之后的密文:"+md5Hash.toHex());
		User lq = userDao.findRolesByUserName("lq");
		System.out.println("========="+lq);
		List<Perm> permsByRoleId = userDao.findPermsByRoleId("1");
		System.out.println("========="+permsByRoleId);

		UserDao userDao1 = (UserDao)ApplicationContextUtils.getBean("userDao");
		List<Perm> permsByRoleId1 = userDao1.findPermsByRoleId("1");
		System.out.println("=====sssssssssss===="+permsByRoleId1);
	}
	@DisplayName("授权")
	@Test
	public void test06(){
		/*修改凭证校验匹配器
		用户登录使用的密码明文与数据库中保存的密码密文不匹配，需要先把输入的密码明文转化为md5密文之后再匹配*/
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		//设置realm使用的hash凭证
		hashedCredentialsMatcher.setHashAlgorithmName("md5");   //默认用md5来处理密文
		hashedCredentialsMatcher.setHashIterations(1024);    //再设置散列的次数

		TestRealm testRealm = new TestRealm();
		//使用创建出来的凭证校验匹配器,设置经过md5处理在对比，而不是默认的直接equals对比
		testRealm.setCredentialsMatcher(hashedCredentialsMatcher);

		//安全管理器
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(testRealm);
		//全局安全工具类设置安全管理器
		SecurityUtils.setSecurityManager(securityManager);

		//全局安全工具类获得主体Subject
		Subject subject = SecurityUtils.getSubject();
		//token令牌进行用户认证
		UsernamePasswordToken token = new UsernamePasswordToken("lq","lq");
		try {
			System.out.println("认证状态："+subject.isAuthenticated());
			//用户认证,认证不通过会报异常，调转到我们自定义实现AuthorizingRealm类认证和授权方法中
			subject.login(token);
			System.out.println("认证状态："+subject.isAuthenticated());
		}catch (UnknownAccountException e) {	//捕捉异常判定错误
			e.printStackTrace();
			System.out.println("用户名错误");
		} catch (IncorrectCredentialsException e) {
			e.printStackTrace();
			System.out.println("密码错误");
		};

		//这里是代码响应式授权，另外啊还有注解授权和页面标签判定授权
		if(subject.isAuthenticated()){
			System.out.println("当前用户是否有admin角色："+subject.hasRole("admin"));
			System.out.println("当前用户是否有user角色："+subject.hasRole("user"));  //有没有这两个角色
			System.out.println("同时有多个权限："+subject.hasRoles(Arrays.asList("admin","user")));

			System.out.println("当前用户是权限"+subject.isPermitted("user:find:01"));
			System.out.println("当前用："+subject.isPermitted("user:find:*"));
			System.out.println("当前r角色："+subject.isPermitted("user:update:*"));

			System.out.println("同时有多个权限："+subject.isPermittedAll("user:update:*","user:update:02"));

		}
	}
}
