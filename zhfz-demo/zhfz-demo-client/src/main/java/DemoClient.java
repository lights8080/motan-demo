/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */



import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zhfz.demo.service.DemoService;

public class DemoClient {

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath*:zhfz_client.xml"});
        System.out.println("zhfz demo is begin.");
        DemoService service = (DemoService) ctx.getBean("demoReferer");
        
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
        	long b = System.currentTimeMillis();
        	String hello = null;
			try {
				hello = service.hello("hello:"+i);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	if(hello==null){
        		hello="";
        	}
            System.out.println("spendtime:"+(System.currentTimeMillis()-b)+",result:"+hello);
            Thread.sleep(1000);
        }
        System.out.println("zhfz demo is finish.");
        System.exit(0);
        
    }

}
