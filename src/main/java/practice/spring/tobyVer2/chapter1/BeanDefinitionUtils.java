package practice.spring.tobyVer2.chapter1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Class : 빈 등록정보 조회 유틸리티 클래스
 * */
public class BeanDefinitionUtils {
	public static void printBeanDefinitions(GenericApplicationContext gac) {
		List<List<String>> roleBeanInfos = new ArrayList<>();
		roleBeanInfos.add(new ArrayList<>());
		roleBeanInfos.add(new ArrayList<>());
		roleBeanInfos.add(new ArrayList<>());
		
		for (String name : gac.getBeanDefinitionNames()) {
			int role = gac.getBeanDefinition(name).getRole();
			List<String> beanInfos = roleBeanInfos.get(role);
			beanInfos.add(role + "\t" + name + "\t" + gac.getBean(name).getClass().getName());
		}
		
		for(List<String> beanInfos : roleBeanInfos) {
			for (String beanInfo : beanInfos) {
				System.out.println(beanInfo);
			}
		}
	}
	
	public static void main (String[] args) {
		GenericApplicationContext gac = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(gac);
		reader.loadBeanDefinitions("/ver2Chapter1/StringPrinter.xml");
		gac.refresh();
		
		BeanDefinitionUtils.printBeanDefinitions(gac);
	}
}
