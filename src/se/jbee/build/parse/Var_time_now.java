package se.jbee.build.parse;

public class Var_time_now implements Var {

	@Override
	public String resolve(String var, Var env) {
		return "time:now".equals(var) ? String.valueOf(System.currentTimeMillis()) : "";
	}

}
