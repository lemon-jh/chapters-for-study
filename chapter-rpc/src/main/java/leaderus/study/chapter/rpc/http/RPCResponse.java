package leaderus.study.chapter.rpc.http;

import java.util.Arrays;

import leaderus.study.chapter.rpc.utils.RpsConstans;

public class RPCResponse {

	 private int sessionId;
	 
	 private short requestId;
	 
	 private byte retCode;//返回码，字符'0'为正常调用完成，其他为相应错误码
	 
	 private byte[] errMsg; //如果有错误，则提供错误信息
	 
	 private byte[] retValue;//返回值
	 

	public RPCResponse(int sessionId, short requestId, byte retCode, byte[] errMsg, byte[] retValue) {
		this.sessionId = sessionId;
		this.requestId = requestId;
		this.retCode = retCode;
		this.errMsg = errMsg;
		this.retValue = retValue;
	}
	
	public RPCResponse(RPCRequest request) {
		this.sessionId = request.getSessionId();
		this.requestId = request.getRequestId();
		this.retCode = RpsConstans.SUCCESS;
		this.errMsg = "success".getBytes();
		this.retValue = "success".getBytes();
	}

	@Override
	public String toString() {
		return "RPCResponse [sessionId=" + sessionId + ", requestId=" + requestId + ", retCode=" + retCode + ", errMsg="
				+ Arrays.toString(errMsg) + ", retValue=" + Arrays.toString(retValue) + "]";
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public short getRequestId() {
		return requestId;
	}

	public void setRequestId(short requestId) {
		this.requestId = requestId;
	}

	public byte getRetCode() {
		return retCode;
	}

	public void setRetCode(byte retCode) {
		this.retCode = retCode;
	}

	public byte[] getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(byte[] errMsg) {
		this.errMsg = errMsg;
	}

	public byte[] getRetValue() {
		return retValue;
	}

	public void setRetValue(byte[] retValue) {
		this.retValue = retValue;
	}
	 
	 
}
