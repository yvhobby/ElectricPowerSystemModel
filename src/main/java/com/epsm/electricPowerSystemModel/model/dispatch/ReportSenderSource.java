package main.java.com.epsm.electricPowerSystemModel.model.dispatch;

public interface ReportSenderSource {
	public PowerObjectState getState();
	public void setReportSenderSender(ReportSender sender);
}
