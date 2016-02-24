package edu.uci.eecs.wukong.framework.jetty.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.uci.eecs.wukong.framework.SystemStates;
import com.google.gson.Gson;

@Path("/adjust")
public class SettingAdjusmentService {
	private static final long serialVersionUID = -5886924291811450614L;
	private SystemStates states;
	private Gson gson = new Gson();
	private static final int SUCCESS = 200;
	private static final int FAILURE = 500;

	public SettingAdjusmentService() {
		this.states = SystemStates.getInstance();
	}

	@GET
	@Path("monitor/adjust")
	@Produces(MediaType.APPLICATION_JSON)
	public String adjust(@QueryParam("period") Long period) {
		if (period != null) {
			this.states.updateMonitorInterval(period.longValue());
			return new Result(SUCCESS, "Successfully adjust the monitroing period to "
					+ period + " millisecond.").toGson(gson);
		}
		
		return new Result(FAILURE, "No required period parameter.").toGson(gson);
	}
	
	@GET
	@Path("monitor/on")
	@Produces(MediaType.APPLICATION_JSON)
	public String turnMonitorOn() {
		this.states.updateMonitoringState(true);
		return new Result(SUCCESS, "Successfully turn on monitor service at period of "
				+ this.states.getCurrentMonitorPeriod()  + " millisecond.").toGson(gson);
	}
	
	@GET
	@Path("monitor/off")
	@Produces(MediaType.APPLICATION_JSON)
	public String turnMonitorOff() {
		this.states.updateMonitoringState(false);
		return new Result(SUCCESS, "Successfully turn off monitor service.").toGson(gson);
	}
	
	static class Result {
		private int code;
		private String info;
		
		public Result(int code, String info) {
			this.code = code;
			this.info = info;
		}
		
		public String toGson(Gson gson) {
			return gson.toJson(this);
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}
	}
}
