package test;

import com.aobei.cas.util.UserPwdUtil;
import com.aobei.cas.util.UserPwdUtil.UserPwd;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static void main(String[] args) {
		UserPwd pwd = UserPwdUtil.userPwd("123456");
		ObjectMapper om  = new ObjectMapper();
		try {
			System.out.println(om.writeValueAsString(pwd));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
