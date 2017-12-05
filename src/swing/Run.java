package swing;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.AlgExceptionI;
import edu.wis.jtlv.lib.mc.CTL.CTLModelCheckAlg;
import edu.wis.jtlv.lib.mc.LTL.LTLModelCheckAlg;
import edu.wis.jtlv.lib.mc.RTCTLK.RTCTLKModelCheckAlg;
import edu.wis.jtlv.lib.mc.RTCTLK.TextRTCTLModelCheckAlg;
import edu.wis.jtlv.lib.mc.RTLTLK.RTLTLKModelCheckAlg;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Run {

	public void smvRun(TextEditor currentText) throws AlgExceptionI {
		String src=currentText.contralPanel.fileOperation.getPath();
		String name=currentText.contralPanel.fileOperation.getFileName();
		String	url=src+name+".smv";
		Env.resetEnv();
		try {
			Env.loadModule(url);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		SMVModule main = (SMVModule) Env.getModule("main");
		main.setFullPrintingMode(true);
		System.out.println("========= DONE Loading Modules ============");

		String  parse=GetProperty(currentText.text.getText().toString());
		System.out.println("parse---"+parse);
		Spec[] all_specs = Env.loadSpecString(parse);
		System.out.println("========= DONE Loading Specs ============");

		for (int i = 0; i < all_specs.length; i++)
			if (all_specs[i].isCTLSpec()||all_specs[i].isRealTimeCTLSpec()||all_specs[i].isCTLKSpec()||all_specs[i].isRealTimeCTLKSpec())
			{
				    TextRTCTLModelCheckAlg checker = new TextRTCTLModelCheckAlg(main,all_specs[i]);
					checker.preAlgorithm();
					System.out.println(i+checker.doAlgorithm().resultString());
			}
			else if (all_specs[i].isLTLSpec()||all_specs[i].isRealTimeLTLSpec()||all_specs[i].isRealTimeLTLKSpec())
			{
				    LTLModelCheckAlg checker = new LTLModelCheckAlg(main,all_specs[i]);
					checker.preAlgorithm();
					System.out.println(i+checker.doAlgorithm().resultString());
			}
	}

	public String GetProperty(String spec) {
		String ms=spec.replaceAll("[-]+[\\s\\S&&[^\n]]*[\r\n]+", "");
		String regEx = "(CTLSPEC|LTLSPEC|SPEC)[\\s\\S&&[^\n]]*\\;[\\s]*$";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx,Pattern.MULTILINE);  ;
		Matcher matcher = pattern.matcher(ms);
		ArrayList<String> tempList = new ArrayList<String>();
		while(matcher.find()){
			tempList.add(matcher.group());
		}
		String s="";
		for(String temp : tempList){
			s=s + temp;
		}
		return s;
	}
}
