package module.meta;

import org.json.JSONObject;

public class MetaMsg {

	public static final String ERR_MSG = "errMsg";
	public static final String SUCC_MSG = "succMsg";
	public static final String WARN_MSG = "warnMsg";

	private String msgStatus;
	private String causedByField;
	private String causedByRule;
	private String msgDev;

	public MetaMsg() {

	}

	public static MetaMsg read(String msgAsJSON) {
		return new JSON().fromJson(msgAsJSON, MetaMsg.class);
	}

	public MetaMsg error() {
		this.msgStatus = ERR_MSG;
		return this;
	}

	public MetaMsg success() {
		this.msgStatus = SUCC_MSG;
		return this;
	}

	public MetaMsg warning() {
		this.msgStatus = WARN_MSG;
		return this;
	}

	public MetaMsg causedByField(String field) {
		this.causedByField = field;
		return this;
	}

	public MetaMsg causedByRule(String rule) {
		this.causedByRule = rule;
		return this;
	}

	public MetaMsg msgDev(String msg) {
		this.msgDev = msg;
		return this;
	}

	public boolean isError() {
		if (msgStatus.equals(ERR_MSG)) {
			return true;
		}
		return false;
	}

	public boolean isSuccess() {
		if (msgStatus.equals(SUCC_MSG)) {
			return true;
		}
		return false;
	}

	public boolean isWarning() {
		if (msgStatus.equals(WARN_MSG)) {
			return true;
		}
		return false;
	}

	public String getCausedByField() {
		return causedByField;
	}

	public String getCausedByRule() {
		return causedByRule;
	}

	public String getMsgDev() {
		return msgDev;
	}

	public String create() {
		return new JSON().toJson(this);
	}

	public static boolean isMsg(JSONObject singleOldData) {
		return !singleOldData.isNull("msgStatus")
				&& !singleOldData.isNull("causedByRule")
				&& !singleOldData.isNull("causedByField");
	}
}
